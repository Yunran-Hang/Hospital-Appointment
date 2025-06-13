package com.nbdeyy.service;

import com.nbdeyy.dto.BatchAddScheduleDTO;
import com.nbdeyy.entity.Schedule;
import com.nbdeyy.vo.DoctorScheduleVO;
import com.nbdeyy.vo.ScheduleListVO;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    /**
     * 获取指定日期的排班信息
     * @param workDate
     * @return
     */
    List<ScheduleListVO> getScheduleList(LocalDate workDate);

    /**
     * 根据id删除排班
     * @param id
     */
    void deleteById(Long id);

    /**
     * 更新schedule信息
     * @param schedule
     */
    void update(Schedule schedule);

    /**
     * 添加新排班
     * @param schedule
     */
    void save(Schedule schedule);

    /**
     * 批量添加新排班
     * @param batchAddScheduleDTO
     */
    void saveBatch(BatchAddScheduleDTO batchAddScheduleDTO);

    /**
     * 获得医生两周内的排班信息
     * @param doctorId
     * @return
     */
    List<DoctorScheduleVO> getScheduleInTwoWeeksByDoctorId(Long doctorId);
}
