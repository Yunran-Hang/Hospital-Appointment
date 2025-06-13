package com.nbdeyy.controller;

import com.nbdeyy.constant.MessageConstant;
import com.nbdeyy.result.Result;
import com.nbdeyy.utils.AliOssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
@Tag(name = "通用模块", description = "通用相关接口")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/avatar/upload")
    @Operation(summary = "文件上传接口")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);
        try {
            // 原始文件名
            String originalFilename = file.getOriginalFilename();
            // 截取后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 构建新文件名
            String objectName = UUID.randomUUID().toString() + extension;
            // 文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(),objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败: {}",e.getMessage());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
