package com.nbdeyy.service.impl;

import com.nbdeyy.constant.AppointmentStatusConstant;
import com.nbdeyy.constant.MessageConstant;
import com.nbdeyy.constant.TimeSlotStatusConstant;
import com.nbdeyy.dto.AppointmentInfoDTO;
import com.nbdeyy.dto.BatchAddScheduleDTO;
import com.nbdeyy.entity.Schedule;
import com.nbdeyy.entity.TimeSlot;
import com.nbdeyy.exception.ScheduleDeleteException;
import com.nbdeyy.exception.ScheduleInsertException;
import com.nbdeyy.exception.ScheduleUpdateException;
import com.nbdeyy.mapper.AppointmentMapper;
import com.nbdeyy.mapper.ScheduleMapper;
import com.nbdeyy.mapper.TimeSlotMapper;
import com.nbdeyy.service.ScheduleService;
import com.nbdeyy.vo.DoctorScheduleVO;
import com.nbdeyy.vo.ScheduleListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private TimeSlotMapper timeSlotMapper;

    /**
     * 获取指定日期的排班信息
     * @param workDate
     * @return
     */
    public List<ScheduleListVO> getScheduleList(LocalDate workDate) {
        return scheduleMapper.getScheduleList(workDate);
    }

    /**
     * 根据id删除排班
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        // 1.如果当前排班存在待确诊的病人的排班(appointment)，就不能删除
        Integer count = appointmentMapper.countAppointmentsByScheduleId(id, AppointmentStatusConstant.PENDING_MEDICAL_ATTENTION);
        if (count > 0){
            throw new ScheduleDeleteException(MessageConstant.EXIST_PENDING_SCHEDULES);
        }
        // 2.删除time_slot表中该排班信息(如果有)
        timeSlotMapper.deleteAllTimeSlotByScheduleId(id);

        // 3.删除appointment表中的排班信息(如果有)
        appointmentMapper.deleteAllAppointmentByScheduleId(id);
        // 4.删除schedule信息
        scheduleMapper.deleteById(id);

    }

    /**
     * 更新schedule信息
     * @param schedule
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Schedule schedule) {
        Schedule DBSchedule = scheduleMapper.getScheduleById(schedule.getId());
        if (schedule.getId() == null || DBSchedule == null){
            throw new ScheduleUpdateException(MessageConstant.ILLEGAL_OPERATION);
        }
        // 如果剩余的预约数不等于最大预约数，则无法更新
        if (!DBSchedule.getMaxAppointments().equals(DBSchedule.getLeftAppointments())){
            // 1.管理端正常是无法触发该异常，因为前端只能查看，无法修改，为了防止使用postman测试工具访问该接口
            // 2.如果管理员操作的时候有患者已经预约了该日程下的号，那也无法再更新
            throw new ScheduleUpdateException(MessageConstant.EXIST_PENDING_SCHEDULES);
        }
        // 否则就执行更新操作
        // 1.更新schedule表的信息
        schedule.setLeftAppointments(schedule.getMaxAppointments());
        scheduleMapper.update(schedule);
        // 2.删除time_slot表中该排班信息
        timeSlotMapper.deleteAllTimeSlotByScheduleId(schedule.getId());
        // 3.批量添加新的time_slot信息
        List<TimeSlot> insertTimeSlotList = genTimeSlotList(schedule.getId(),schedule.getStartTime(),schedule.getEndTime(),schedule.getTimePeriodInterval());
        timeSlotMapper.insertBatch(insertTimeSlotList);
    }

    /**
     * 添加新排班
     * @param schedule
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(Schedule schedule) {
        // 一个医生不能重复排同一天同一时段的排班
        AppointmentInfoDTO appointmentInfoDTO = AppointmentInfoDTO.builder()
                .doctorId(schedule.getDoctorId())
                .period(schedule.getPeriod())
                .workDate(schedule.getWorkDate())
                .build();
        Long scheduleId = scheduleMapper.getScheduleIdByPeriodAndWordDateAndDoctorId(appointmentInfoDTO);
        if (scheduleId != null){
            throw new ScheduleInsertException(MessageConstant.DUPLICATE_SCHEDULE);
        }
        // 1.插入schedule信息
        schedule.setLeftAppointments(schedule.getMaxAppointments());
        scheduleMapper.save(schedule);
        // 2.根据返回的scheduleId插入time_slot的信息(具体的每一个号)
        Long insertScheduleId = schedule.getId();
        LocalTime startTime = schedule.getStartTime();
        LocalTime endTime = schedule.getEndTime();
        Integer timePeriodInterval = schedule.getTimePeriodInterval();

        /* 生成一个自动插入timeslot表中的记录，每隔timePeriodInterval分钟，从startTime开始，要求是最后一个排班的结束时间不能超过
            endTime，先写一个模版出来
         */
        List<TimeSlot> insertTimeSlotList = genTimeSlotList(insertScheduleId,startTime,endTime,timePeriodInterval);
        //  最后统一插入
        timeSlotMapper.insertBatch(insertTimeSlotList);
    }

    /**
     *      batchAddScheduleDTO()
     *     private List<Long> doctorIds;
     *     private List<Integer> workDays;
     *     private LocalDate startDate;
     *     private LocalDate endDate;
     *     private Integer timePeriodInterval;
     *     private Integer maxAppointments;
     *     private Integer period;
     *     private LocalTime startTime;
     *     private LocalTime endTime;
     *
     */
    /**
     * 批量添加新排班
     * @param batchAddScheduleDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(BatchAddScheduleDTO batchAddScheduleDTO) {
        /* 需求分析：批量为医生进行排班，为了确保成功，先对每个医生的档期进行查询，如果存在任何一个医生在时间段内有
                    排班出现，就抛出异常，否则就进行批量添加；先添加schedule表，再添加time_slot表

         */
        /**
         * startDate和endDate是限定范围，包含起始和结尾日期，然后workDays是代表在这个周期内，数字是几就是周几需要排班，然后数字范围是1-7，
         * period是时间段1上午2下午，startTime和endTime则代表了每次排班时间段内的开始时间和结束时间，timePeriodInterval则代表了在开始时间和结束时间内每隔多少分钟排一次班，
         * 下面有genTimeSlotList函数专门可以生成，不用另外再写
         */
        List<LocalDate> validWorkDates = generateValidWorkDates(batchAddScheduleDTO.getStartDate(),batchAddScheduleDTO.getEndDate(),batchAddScheduleDTO.getWorkDays());

        for (LocalDate workDate : validWorkDates){
            for (Long doctorId : batchAddScheduleDTO.getDoctorIds()) {
                Schedule insertSchedule = Schedule.builder()
                        .doctorId(doctorId)
                        .workDate(workDate)
                        .period(batchAddScheduleDTO.getPeriod())
                        .startTime(batchAddScheduleDTO.getStartTime())
                        .endTime(batchAddScheduleDTO.getEndTime())
                        .timePeriodInterval(batchAddScheduleDTO.getTimePeriodInterval())
                        .maxAppointments(batchAddScheduleDTO.getMaxAppointments())
                        .build();
                save(insertSchedule);
            }
        }
    }

    /**
     * 获得医生两周内的排班信息
     * @param doctorId
     * @return
     */
    public List<DoctorScheduleVO> getScheduleInTwoWeeksByDoctorId(Long doctorId) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(13);
        List<ScheduleListVO> scheduleList = scheduleMapper.getDoctorScheduleInTwoWeeks(doctorId,startDate,endDate);
        // 按 workDate 分组
        Map<LocalDate, List<ScheduleListVO>> groupedMap = scheduleList.stream()
                .collect(Collectors.groupingBy(ScheduleListVO::getWorkDate));

        // 转换为 List<GroupedScheduleVO>
        List<DoctorScheduleVO> groupedSchedules = groupedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // 默认升序排列
                .map(entry -> new DoctorScheduleVO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return groupedSchedules;
    }

    /**
     * 根据开始时间、结束时间和间隔时间生成对应的time_slot信息列表
     * @param ScheduleId
     * @param startTime
     * @param endTime
     * @param periodInterval
     * @return
     */
    private List<TimeSlot> genTimeSlotList(Long ScheduleId,LocalTime startTime,LocalTime endTime,Integer periodInterval){
        List<TimeSlot> timeSlotList = new ArrayList<>();
        Integer count = 1;
        LocalTime currentTime = startTime;
        while (!currentTime.isAfter(endTime)) {
            LocalTime nextTime = currentTime.plusMinutes(periodInterval);
            if (nextTime.isAfter(endTime)) {
                break;
            }

            // 当前号符合条件，设置参数
            TimeSlot templateTimeSlot = TimeSlot.builder()
                    .scheduleId(ScheduleId)
                    .numberId(0)
                    .status(TimeSlotStatusConstant.AVAILABLE_FOR_RESERVATION)
                    .startTime(currentTime)
                    .endTime(nextTime)
                    .numberId(count)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            timeSlotList.add(templateTimeSlot);

            count += 1;
            currentTime = nextTime;
        }
        return timeSlotList;
    }
    /**
     * 根据 startDate、endDate 和 workDays 生成所有有效的工作日
     *
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @param workDays   指定的工作日（1~7，对应周一~周日）
     * @return           符合条件的日期列表
     */
    private List<LocalDate> generateValidWorkDates(LocalDate startDate, LocalDate endDate, List<Integer> workDays) {
        List<LocalDate> validDates = new ArrayList<>();
        boolean[] isWorkDay = new boolean[7];
        for (Integer day : workDays) {
            isWorkDay[(day - 1 + 7) % 7] = true; // 将 Monday=0,...Sunday=6
        }

        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (isWorkDay[currentDate.getDayOfWeek().ordinal()]) {
                validDates.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }
        return validDates;
    }

}
