package com.nbdeyy.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nbdeyy.constant.MessageConstant;
import com.nbdeyy.constant.TimeSlotStatusConstant;
import com.nbdeyy.context.BaseContext;
import com.nbdeyy.dto.AppointmentInfoDTO;
import com.nbdeyy.dto.AppointmentListPageDTO;
import com.nbdeyy.dto.CreateAppointmentDTO;
import com.nbdeyy.entity.Appointment;
import com.nbdeyy.entity.Schedule;
import com.nbdeyy.entity.TimeSlot;
import com.nbdeyy.exception.DuplicateAppointmentsException;
import com.nbdeyy.exception.IllegalParametersException;
import com.nbdeyy.mapper.AppointmentMapper;
import com.nbdeyy.mapper.DoctorMapper;
import com.nbdeyy.mapper.ScheduleMapper;
import com.nbdeyy.mapper.TimeSlotMapper;
import com.nbdeyy.result.PageResult;
import com.nbdeyy.service.AppointmentService;
import com.nbdeyy.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private TimeSlotMapper timeSlotMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;


    /**
     * 获得用户的所有预约记录
     * @param id
     * @return
     */
    public List<MyAppointmentListVO> getAppointmentListByUserId(Long id) {
        return appointmentMapper.getAppointmentListByUserId(id);
    }

    /**
     * 获取预约详细信息(返回医生信息，和排班信息)
     * @param appointmentInfoDTO
     * @return
     */
    public AppointmentInfoVO getAppointmentInfo(AppointmentInfoDTO appointmentInfoDTO) {
        DoctorDetailVO doctorDetailVO = doctorMapper.getDoctorDetail(appointmentInfoDTO.getDoctorId());
        Long ScheduleId = scheduleMapper.getScheduleIdByPeriodAndWordDateAndDoctorId(appointmentInfoDTO);
        List<TimeSlotVO> timeSlotList = timeSlotMapper.getTimeSlotListByScheduleId(ScheduleId);
        return AppointmentInfoVO.builder().doctorDetail(doctorDetailVO).timeSlotList(timeSlotList).build();
    }

    /**
     * 创建预约
     * @param createAppointmentDTO
     */
    @Transactional(rollbackFor = Exception.class) // 开启事务
    public void createAppointment(CreateAppointmentDTO createAppointmentDTO) {
        Appointment appointment = new Appointment();
        Long scheduleId = scheduleMapper.getScheduleIdByPeriodAndWordDateAndDoctorId(AppointmentInfoDTO.builder()
                .doctorId( createAppointmentDTO.getDoctorId()).period( createAppointmentDTO.getPeriod())
                .workDate( createAppointmentDTO.getWorkDate()).build());
        if (scheduleId == null){
            throw new IllegalParametersException(MessageConstant.ILLEGAL_PARAMETERS);
        }
        // 获取schedule信息，根据ID获取，先获取ID是为了防止前端参数被篡改
        Schedule scheduleDetail =  scheduleMapper.getScheduleById(scheduleId);
        appointment.setScheduleId(scheduleId);
        appointment.setAppointmentDate(scheduleDetail.getWorkDate());
        appointment.setPeriod(scheduleDetail.getPeriod());
        // 获取医生信息
        DoctorDetailVO doctor = doctorMapper.getDoctorDetail(createAppointmentDTO.getDoctorId());
        if (doctor == null) throw new IllegalParametersException(MessageConstant.ILLEGAL_PARAMETERS);
        appointment.setDoctorId(doctor.getId());
        appointment.setFee(doctor.getFee());

        TimeSlot timeSlot = timeSlotMapper.getTimeSlotById(createAppointmentDTO.getTimeSlotId());
        if (timeSlot == null){
            throw new IllegalParametersException(MessageConstant.ILLEGAL_PARAMETERS);
        }
        appointment.setTimeSlotId(timeSlot.getId());
        appointment.setAppointmentNo(timeSlot.getNumberId());
        appointment.setStartTime(timeSlot.getStartTime());
        appointment.setEndTime(timeSlot.getEndTime());
        appointment.setStatus(1);

        appointment.setPatientId(BaseContext.getCurrentId());

        // 如果时间是今天上午，但是现在已经是下午了，则不允许预约，属于非法操作
        if ( (scheduleDetail.getWorkDate().isBefore(LocalDate.now()) || scheduleDetail.getWorkDate().isEqual(LocalDate.now()) )
                && scheduleDetail.getPeriod() == 1 && LocalTime.now().isAfter(LocalTime.of(12,0))){
            throw new IllegalParametersException(MessageConstant.ILLEGAL_PARAMETERS);
        }

        // 判断用户是否在当天已经预约过该医生的号了， 如果有则不允许预约
        Integer count = appointmentMapper.countByUserIdAndDoctorIdAndWorkDateAndStatus(appointment);
        if (count > 0){
            throw new DuplicateAppointmentsException(MessageConstant.DUPLICATE_APPOINTMENTS);
        }

        // 填充完信息之后，接下来就是进行更新插入操作
        // 1.更新schedule表的剩余预约数
        scheduleMapper.update(Schedule.builder()
                .id(scheduleDetail.getId())
                .leftAppointments(scheduleDetail.getLeftAppointments() - 1)
                .build());
        // 2.插入appointment信息
        appointmentMapper.insert(appointment);
        // 3.更改timeslot表中的状态
        timeSlotMapper.update(TimeSlot.builder()
                .id(createAppointmentDTO.getTimeSlotId())
                .status(TimeSlotStatusConstant.RESERVED)
                .build());
    }

    /**
     * 分页查询预约列表
     * @param appointmentListPageDTO
     * @return
     */
    public PageResult pageQuery(AppointmentListPageDTO appointmentListPageDTO) {
        PageHelper.startPage(appointmentListPageDTO.getPage(), appointmentListPageDTO.getPageSize());
        Page<AppointmentPageQueryVO> p = appointmentMapper.pageQuery(appointmentListPageDTO);
        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 更新预约状态
     * @param id
     * @param code
     */
    public void updateStatus(Long id, Integer code) {
        appointmentMapper.updateStatus(id, code);
    }
}
