package com.nbdeyy.controller.user;

import com.nbdeyy.result.Result;
import com.nbdeyy.service.AppointmentService;
import com.nbdeyy.vo.MyAppointmentListVO;
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
@RequestMapping("/user/myAppointment")
@Tag(name = "用户模块", description = "用户端相关接口")
public class MyAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * 获得用户的所有预约记录
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "获得用户所有预约记录接口")
    public Result<List<MyAppointmentListVO>> getMyAppointmentList(@PathVariable Long id){
        log.info("获得用户所有预约记录: {}",id);
        List<MyAppointmentListVO> list = appointmentService.getAppointmentListByUserId(id);
        return Result.success(list);
    }

}
