package com.nbdeyy.controller.admin;

import com.nbdeyy.annotation.Log;
import com.nbdeyy.dto.DoctorListPageDTO;
import com.nbdeyy.entity.Doctor;
import com.nbdeyy.result.PageResult;
import com.nbdeyy.result.Result;
import com.nbdeyy.service.DoctorService;
import com.nbdeyy.vo.DoctorListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminDoctorController")
@Slf4j
@RequestMapping("/admin/doctor")
@Tag(name = "管理员模块", description = "管理员后台管理相关接口")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;

    /**
     * 分页查询医生列表
     * @param doctorListPageDTO
     * @return
     */
    @Operation(summary = "分页查询医生列表接口")
    @GetMapping("/page")
    public Result<PageResult> pageQueryDoctorList (DoctorListPageDTO doctorListPageDTO) {
        log.info("分页查询医生列表: {}",  doctorListPageDTO);
        PageResult page = doctorService.pageQueryDoctorList(doctorListPageDTO);
        return Result.success(page);
    }

    /**
     * 新增医生
     * @return
     */
    @Operation(summary = "新增医生接口")
    @PostMapping("/save")
    @Log
    public Result saveDoctor(@RequestBody Doctor doctor){
        log.info("新增医生: {}", doctor);
        doctorService.save(doctor);
        return Result.success();
    }

    /**
     * 更新医生信息
     * @param doctor
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "更新医生接口")
    @Log
    public Result updateDoctor(@RequestBody Doctor doctor){
        log.info("更新医生: {}", doctor);
        doctorService.update(doctor);
        return Result.success();
    }

    /**
     * 根据ID删除医生
     * @param doctorId
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除医生接口")
    @Log
    public Result deleteDoctor(@PathVariable("id") Long doctorId){
        log.info("删除医生: {}", doctorId);
        doctorService.deleteById(doctorId);
        return Result.success();
    }

    /**
     * 获得所有医生列表
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有医生接口")
    public Result<List<DoctorListVO>> getDoctorList(){
        log.info("获得所有医生列表");
        List<DoctorListVO> list = doctorService.getDoctorList();
        return Result.success(list);
    }
}
