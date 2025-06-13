package com.nbdeyy.controller.admin;

import com.nbdeyy.dto.OperateLogPageDTO;
import com.nbdeyy.result.PageResult;
import com.nbdeyy.result.Result;
import com.nbdeyy.service.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/admin/log")
public class OperateLogController {

    @Autowired
    private OperateLogService operateLogService;

    /**
     * 分页查询操作日志
     * @param operateLogPageDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(OperateLogPageDTO operateLogPageDTO) {
        log.info("分页查询操作日志: {}",operateLogPageDTO);
        PageResult p = operateLogService.pageQuery(operateLogPageDTO);
        return Result.success(p);
    }
}
