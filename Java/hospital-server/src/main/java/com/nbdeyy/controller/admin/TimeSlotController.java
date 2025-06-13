package com.nbdeyy.controller.admin;

import com.nbdeyy.result.Result;
import com.nbdeyy.service.TimeSlotService;
import com.nbdeyy.vo.TimeSlotVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/timeslot")
@Tag(name = "管理员模块", description = "管理员后台管理相关接口")
public class TimeSlotController {

    @Autowired
    private TimeSlotService timeSlotService;

    /**
     * 获取医生号源信息
     * @param scheduleId
     * @return
     */
    @Operation(summary = "获得医生所有号源信息接口")
    @GetMapping("/getAll/{scheduleId}")
    public Result getDoctorTimeSlot(@PathVariable Long scheduleId){
        log.info("获取医生号源信息: {}",scheduleId);
        List<TimeSlotVO> list = timeSlotService.getTimeSlotListByScheduleId(scheduleId);
        return Result.success(list);
    }
}
