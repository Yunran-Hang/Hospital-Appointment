package com.nbdeyy.controller.admin;

import com.nbdeyy.result.Result;
import com.nbdeyy.service.DepartmentService;
import com.nbdeyy.vo.DepartmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("adminDepartmentController")
@Slf4j
@RequestMapping("/admin/dept")
@Tag(name = "管理员模块", description = "管理员后台管理相关接口")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    /**
     * 查询所有科室列表
     * @return
     */
    @GetMapping
    @Operation(summary = "查询所有科室列表接口")
    public Result<List<DepartmentVO>> getDepartmentList(){
        log.info("查询所有科室列表");
        return Result.success(departmentService.getDepartmentList());
    }
}
