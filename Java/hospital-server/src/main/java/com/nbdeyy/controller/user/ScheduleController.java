package com.nbdeyy.controller.user;

import com.nbdeyy.result.Result;
import com.nbdeyy.service.ScheduleService;
import com.nbdeyy.vo.DoctorScheduleVO;
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
@RequestMapping("/user/schedule")
@Tag(name = "用户模块", description = "用户端相关接口")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 获取医生两周内排班信息
     * @param doctorId
     * @return
     */
    @GetMapping("/{doctorId}")
    @Operation(summary = "获得医生两周内排班信息接口")
    public Result<List<DoctorScheduleVO>> getDoctorSchedule(@PathVariable Long doctorId){
        log.info("获取医生两周内排班信息, doctorId: {}", doctorId);
        List<DoctorScheduleVO> doctorScheduleVO = scheduleService.getScheduleInTwoWeeksByDoctorId(doctorId);
        return Result.success(doctorScheduleVO);
    }
}
