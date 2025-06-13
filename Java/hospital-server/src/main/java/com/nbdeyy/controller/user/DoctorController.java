package com.nbdeyy.controller.user;

import com.nbdeyy.dto.DoctorSearchDTO;
import com.nbdeyy.result.PageResult;
import com.nbdeyy.result.Result;
import com.nbdeyy.service.DoctorService;
import com.nbdeyy.vo.DoctorDetailVO;
import com.nbdeyy.vo.DoctorScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/doctor")
@Tag(name = "用户模块", description = "用户端相关接口")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    /**
     * 分页查询医生列表
     * @param doctorSearchDTO
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询医生列表接口")
    public Result<PageResult> page(DoctorSearchDTO doctorSearchDTO){
        log.info("分页查询医生列表: {}",  doctorSearchDTO);
        PageResult pageResult = doctorService.pageQuery(doctorSearchDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取医生详细信息
     * @param doctorId
     * @return
     */
    @GetMapping("/{doctorId}")
    @Operation(summary = "获取医生详细信息接口")
    public Result<DoctorDetailVO> getDoctorDetail(@PathVariable Long doctorId){
        log.info("获取医生详细信息, doctorId: {}", doctorId);
        DoctorDetailVO doctorDetailVO = doctorService.getDoctorDetail(doctorId);
        return Result.success(doctorDetailVO);
    }
}
