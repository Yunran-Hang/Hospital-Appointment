package com.nbdeyy.controller.admin;

import com.nbdeyy.constant.JwtClaimsConstant;
import com.nbdeyy.constant.MessageConstant;
import com.nbdeyy.constant.StatusConstant;
import com.nbdeyy.dto.UserLoginDTO;
import com.nbdeyy.entity.Admin;
import com.nbdeyy.exception.AccountLockedException;
import com.nbdeyy.exception.AccountNotFoundException;
import com.nbdeyy.exception.PasswordErrorException;
import com.nbdeyy.properties.JwtProperties;
import com.nbdeyy.result.Result;
import com.nbdeyy.service.UserService;
import com.nbdeyy.utils.JwtUtil;
import com.nbdeyy.utils.Md5Util;
import com.nbdeyy.vo.AdminLoginVO;
import com.nbdeyy.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController("adminLoginController")
@Slf4j
@RequestMapping("/admin/login")
@Tag(name = "管理员模块", description = "管理员后台管理相关接口")
public class LoginController {
    @Autowired
    private UserService  userService;
    @Autowired
    private JwtProperties jwtProperties;
    /**
     * 登录请求
     * @param userLoginDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "管理员登录接口")
    public Result login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("管理员登录: {}",userLoginDTO);
        Admin adminInfo = userService.getAdminByUsername(userLoginDTO.getUsername());
        if (adminInfo == null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        // 判断账户是否被禁用
        if (adminInfo.getStatus().equals(StatusConstant.DISABLE)){
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        // 判断密码是否正确
        if (Md5Util.checkPasswordWithSalt(userLoginDTO.getPassword(),adminInfo.getPassword())){
            // 登陆成功
            Map<String, Object> claims = new HashMap<>();
            claims.put(JwtClaimsConstant.ADMIN_ID, adminInfo.getId());
            claims.put(JwtClaimsConstant.USERNAME, adminInfo.getUsername());
            String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(),jwtProperties.getUserTtl(),claims);
            // 封装管理员信息
            AdminLoginVO adminLoginVO = AdminLoginVO.builder()
                    .id(adminInfo.getId())
                    .token(token)
                    .build();
            return Result.success(adminLoginVO);
        }
        throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
    }
}
