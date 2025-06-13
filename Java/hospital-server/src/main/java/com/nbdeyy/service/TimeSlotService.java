package com.nbdeyy.service;

import com.nbdeyy.vo.TimeSlotVO;

import java.util.List;

public interface TimeSlotService {
    /**
     * 根据排班id查询时间段列表
     * @param scheduleId
     * @return
     */
    List<TimeSlotVO> getTimeSlotListByScheduleId(Long scheduleId);
}
