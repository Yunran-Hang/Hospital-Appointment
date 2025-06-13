package com.nbdeyy.mapper;

import com.nbdeyy.annotation.AutoFill;
import com.nbdeyy.entity.TimeSlot;
import com.nbdeyy.enumeration.OperationType;
import com.nbdeyy.vo.TimeSlotVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TimeSlotMapper {
    /**
     * 通过排班id查询所有时间段信息
     * @param scheduleId
     * @return
     */
    @Select("select id, schedule_id,number_id, start_time, end_time, status from time_slot where schedule_id = #{scheduleId}")
    List<TimeSlotVO> getTimeSlotListByScheduleId(Long scheduleId);

    /**
     * 通过id获取单个时间表信息
     * @param timeSlotId
     * @return
     */
    @Select("select * from time_slot where id = #{timeSlotId};")
    TimeSlot getTimeSlotById(Long timeSlotId);

    /**
     * 更新timeslot
     * @param timeSlot
     */
    @AutoFill(OperationType.UPDATE)
    void update(TimeSlot timeSlot);

    /**
     * 根据scheduleId删除所有时间表信息
     * @param scheduleId
     */
    @Delete("delete from time_slot where schedule_id = #{scheduleId};")
    void deleteAllTimeSlotByScheduleId(Long scheduleId);

    /**
     * 批量插入号源
     * @param insertTimeSlotList
     */
    void insertBatch(List<TimeSlot> insertTimeSlotList);
}
