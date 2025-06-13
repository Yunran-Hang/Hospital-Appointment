package com.nbdeyy.service.impl;

import com.nbdeyy.mapper.TimeSlotMapper;
import com.nbdeyy.service.TimeSlotService;
import com.nbdeyy.vo.TimeSlotVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeSlotServiceImpl implements TimeSlotService {
    @Autowired
    private TimeSlotMapper timeSlotMapper;

    /**
     * 根据排班id查询时间段列表
     * @param scheduleId
     * @return
     */
    public List<TimeSlotVO> getTimeSlotListByScheduleId(Long scheduleId) {
        return timeSlotMapper.getTimeSlotListByScheduleId(scheduleId);
    }
}
