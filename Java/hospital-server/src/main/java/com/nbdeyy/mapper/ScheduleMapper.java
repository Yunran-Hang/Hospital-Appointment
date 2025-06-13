package com.nbdeyy.mapper;

import com.nbdeyy.annotation.AutoFill;
import com.nbdeyy.dto.AppointmentInfoDTO;
import com.nbdeyy.entity.Schedule;
import com.nbdeyy.enumeration.OperationType;
import com.nbdeyy.vo.ScheduleListVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ScheduleMapper {
    /**
     * 通过医生ID、预约时间段和预约日期获得 预约排班id
     * @param appointmentInfoDTO
     * @return
     */
    @Select("select id from schedule where period = #{period} and work_date = #{workDate} and doctor_id = #{doctorId}")
    Long getScheduleIdByPeriodAndWordDateAndDoctorId(AppointmentInfoDTO appointmentInfoDTO);

    /**
     * 根据id获取排班信息
     * @param scheduleId
     * @return
     */
    @Select("select * from schedule where id = #{scheduleId}")
    Schedule getScheduleById(Long scheduleId);

    /**
     * 更新schedule表
     * @param schedule
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Schedule schedule);

    /**
     * 根据Id删除该医生的所有日程安排
     * @param doctorId
     */
    @Delete("delete from schedule where doctor_id = #{doctorId}")
    void deleteAllScheduleByDoctorId(Long doctorId);

    /**
     * 获取指定日期的排班信息
     * @param workDate
     * @return
     */
    List<ScheduleListVO> getScheduleList(LocalDate workDate);

    /**
     * 根据Id删除schedule记录
     * @param scheduleId
     */
    @Delete("delete from schedule where id = #{scheduleId}")
    void deleteById(Long scheduleId);

    /**
     * 添加新排班
     * @param schedule
     */
    @AutoFill(value = OperationType.INSERT)
    void save(Schedule schedule);

    /**
     * 获取医生两周内排班信息
     * @param doctorId
     * @param startDate
     * @param endDate
     * @return
     */
    List<ScheduleListVO> getDoctorScheduleInTwoWeeks(Long doctorId, LocalDate startDate, LocalDate endDate);
}
