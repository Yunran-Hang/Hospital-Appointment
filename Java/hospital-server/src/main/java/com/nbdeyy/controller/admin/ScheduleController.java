package com.nbdeyy.controller.admin;

import com.nbdeyy.annotation.Log;
import com.nbdeyy.dto.BatchAddScheduleDTO;
import com.nbdeyy.entity.Schedule;
import com.nbdeyy.result.Result;
import com.nbdeyy.service.ScheduleService;
import com.nbdeyy.vo.ScheduleListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController("adminScheduleController")
@Slf4j
@RequestMapping("/admin/schedule")
@Tag(name = "管理员模块", description = "管理员后台管理相关接口")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 获得排班列表
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "获得排班列表接口")
    public Result<List<ScheduleListVO>> getScheduleList(@RequestParam LocalDate workDate){
        log.info( "获得排班列表:{}",workDate);
        List<ScheduleListVO> list = scheduleService.getScheduleList(workDate);
        return Result.success(list);
    }

    /**
     * 根据id删除排班
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "根据id删除排班")
    @Log
    public Result deleteSchedule(@PathVariable Long id){
        log.info("删除排班，id: {}",id);
        scheduleService.deleteById(id);
        return Result.success();
    }

    /**
     * 更新schedule信息
     * @param schedule
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "更新排班信息接口")
    @Log
    public Result updateSchedule(@RequestBody Schedule schedule){
        log.info("更新schedule信息: {}",schedule);
        scheduleService.update(schedule);
        return Result.success();
    }

    /**
     * 添加新排班
     * @param schedule
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "添加排班接口")
    @Log
    public Result saveSchedule(@RequestBody Schedule schedule){
        log.info("添加新排班: {}",schedule);
        scheduleService.save(schedule);
        return Result.success();
    }

    /**
     * 批量添加新排班
     * @param batchAddScheduleDTO
     * @return
     */
    @PostMapping("/addBatch")
    @Operation(summary = "批量添加排班接口")
    @Log
    public Result saveBatchSchedule(@RequestBody BatchAddScheduleDTO batchAddScheduleDTO){
        log.info("批量添加新排班: {}",batchAddScheduleDTO);
        scheduleService.saveBatch(batchAddScheduleDTO);
        return Result.success();
    }

}
