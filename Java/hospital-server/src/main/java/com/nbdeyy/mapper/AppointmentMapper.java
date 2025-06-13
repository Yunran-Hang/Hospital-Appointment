package com.nbdeyy.mapper;

import com.github.pagehelper.Page;
import com.nbdeyy.annotation.AutoFill;
import com.nbdeyy.dto.AppointmentListPageDTO;
import com.nbdeyy.entity.Appointment;
import com.nbdeyy.enumeration.OperationType;
import com.nbdeyy.vo.AppointmentPageQueryVO;
import com.nbdeyy.vo.MyAppointmentListVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AppointmentMapper {
    /**
     * 通过用户ID查询用户的预约列表
     * @param id
     * @return
     */
    List<MyAppointmentListVO> getAppointmentListByUserId(Long id);

    /**
     * 插入一条新的预约信息
     * @param appointment
     */
    @AutoFill(OperationType.INSERT)
    void insert(Appointment appointment);

    /**
     * 查询用户是否已经预约过该医生
     * @param appointment
     * @return
     */
    @Select("select count(*) from appointment where patient_id = #{patientId} and doctor_id = #{doctorId} " +
            "and appointment_date = #{appointmentDate} and status = #{status}")
    Integer countByUserIdAndDoctorIdAndWorkDateAndStatus(Appointment appointment);

    /**
     * 查询医生的待处理预约数量
     * @param doctorId
     * @param status
     * @return
     */
    @Select("select count(*) from appointment where doctor_id = #{doctorId} and status <= #{status}")
    Integer countAppointmentsByDoctorId(Long doctorId, Integer status);

    /**
     * 根据医生Id删除所有的医生预约信息
     * @param doctorId
     */
    @Delete("delete from appointment where doctor_id = #{doctorId}")
    void deleteAllAppointmentByDoctorId(Long doctorId);

    /**
     * 查询某个排班下待就诊预约的总数量
     * @param scheduleId
     * @return
     */
    @Select("select count(*) from appointment where schedule_id = #{scheduleId} and status <= #{status}")
    Integer countAppointmentsByScheduleId(Long scheduleId,Integer status);

    /**
     * 根据scheduleId删除所有预约信息
     * @param scheduleId
     */
    @Delete("delete from appointment where schedule_id = #{scheduleId}")
    void deleteAllAppointmentByScheduleId(Long scheduleId);

    /**
     * 分页查询预约列表
     * @param appointmentListPageDTO
     * @return
     */
    Page<AppointmentPageQueryVO> pageQuery(AppointmentListPageDTO appointmentListPageDTO);

    /**
     * 根据用户ID删除所有预约信息
     * @param userId
     */
    @Delete("delete from appointment where patient_id = #{userId}")
    void deleteAllAppointmentByUserId(Long userId);

    /**
     * 更新预约状态
     * @param id
     * @param code
     */
    @Update("update appointment set status = #{code} where id = #{id}")
    void updateStatus(Long id, Integer code);
}
