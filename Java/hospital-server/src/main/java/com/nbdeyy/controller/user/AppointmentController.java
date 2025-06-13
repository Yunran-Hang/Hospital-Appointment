package com.nbdeyy.controller.user;

import com.nbdeyy.dto.AppointmentInfoDTO;
import com.nbdeyy.dto.CreateAppointmentDTO;
import com.nbdeyy.result.Result;
import com.nbdeyy.service.AppointmentService;
import com.nbdeyy.vo.AppointmentInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/user/appointment")
@Tag(name = "用户模块", description = "用户端相关接口")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;
    /**
     * 创建预约请求
     * @return
     */
    @PostMapping("/confirm")
    @Operation(summary = "创建预约接口")
    public Result createAppointment(@RequestBody CreateAppointmentDTO createAppointmentDTO){
        log.info("创建预约请求,{}",createAppointmentDTO);
        appointmentService.createAppointment(createAppointmentDTO);
        return Result.success();
    }

    /**
     * 获取预约详细信息(返回医生信息，和排班信息)
     * @return
     */
    @GetMapping("/info")
    @Operation(summary = "获取预约详细信息接口")
    public Result<AppointmentInfoVO> getAppointmentInfo(AppointmentInfoDTO appointmentInfoDTO) {
        log.info( "获取预约详细信息,{}",appointmentInfoDTO);
        AppointmentInfoVO appointmentInfoVO = appointmentService.getAppointmentInfo(appointmentInfoDTO);
        return Result.success(appointmentInfoVO);
    }
}
