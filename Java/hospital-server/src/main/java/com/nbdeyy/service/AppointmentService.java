package com.nbdeyy.service;

import com.nbdeyy.dto.AppointmentInfoDTO;
import com.nbdeyy.dto.AppointmentListPageDTO;
import com.nbdeyy.dto.CreateAppointmentDTO;
import com.nbdeyy.result.PageResult;
import com.nbdeyy.vo.AppointmentInfoVO;
import com.nbdeyy.vo.MyAppointmentListVO;

import java.util.List;

public interface AppointmentService {
    /**
     * 获得用户的所有预约记录
     * @param id
     * @return
     */
    List<MyAppointmentListVO> getAppointmentListByUserId(Long id);

    /**
     * 获取预约详细信息(返回医生信息，和排班信息)
     * @param appointmentInfoDTO
     * @return
     */
    AppointmentInfoVO getAppointmentInfo(AppointmentInfoDTO appointmentInfoDTO);
    /**
     * 创建预约
     * @param createAppointmentDTO
     */
    void createAppointment(CreateAppointmentDTO createAppointmentDTO);

    /**
     * 分页查询预约列表
     * @param appointmentListPageDTO
     * @return
     */
    PageResult pageQuery(AppointmentListPageDTO appointmentListPageDTO);

    /**
     * 更新预约状态
     * @param id
     * @param completed
     */
    void updateStatus(Long id, Integer completed);
}
