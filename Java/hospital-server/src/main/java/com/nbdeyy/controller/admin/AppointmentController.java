package com.nbdeyy.controller.admin;

import com.nbdeyy.annotation.Log;
import com.nbdeyy.constant.AppointmentStatusConstant;
import com.nbdeyy.dto.AppointmentListPageDTO;
import com.nbdeyy.result.PageResult;
import com.nbdeyy.result.Result;
import com.nbdeyy.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminAppointmentController")
@Slf4j
@RequestMapping("/admin/appointment")
@Tag(name = "管理员模块", description = "管理员后台管理相关接口")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * 分页查询预约列表
     * @param appointmentListPageDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询预约列表接口")
    public Result<PageResult> page(AppointmentListPageDTO appointmentListPageDTO){
        log.info("分页查询预约列表: {}",  appointmentListPageDTO);
        PageResult p = appointmentService.pageQuery(appointmentListPageDTO);
        return Result.success(p);
    }

    @PatchMapping("/finish/{id}")
    @Log
    public Result updateStatus(@PathVariable Long id){
        log.info("设置用户预约状态为完成: {}", id);
        appointmentService.updateStatus(id, AppointmentStatusConstant.COMPLETED);
        return Result.success();
    }
}
