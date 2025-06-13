package com.nbdeyy.controller.user;

import com.nbdeyy.constant.JwtClaimsConstant;
import com.nbdeyy.constant.MessageConstant;
import com.nbdeyy.constant.StatusConstant;
import com.nbdeyy.context.BaseContext;
import com.nbdeyy.dto.CaptchaVerifyDTO;
import com.nbdeyy.dto.ResetPasswordDTO;
import com.nbdeyy.dto.UserLoginDTO;
import com.nbdeyy.dto.UserRegisterDto;
import com.nbdeyy.entity.User;
import com.nbdeyy.exception.AccountAlreadyExistException;
import com.nbdeyy.exception.AccountLockedException;
import com.nbdeyy.exception.AccountNotFoundException;
import com.nbdeyy.exception.PasswordErrorException;
import com.nbdeyy.properties.JwtProperties;
import com.nbdeyy.result.Result;
import com.nbdeyy.service.UserService;
import com.nbdeyy.utils.AliCaptchaUtil;
import com.nbdeyy.utils.Md5Util;
import com.nbdeyy.utils.JwtUtil;
import com.nbdeyy.vo.UserInfoVO;
import com.nbdeyy.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
@RequestMapping("/user/user")
@Tag(name = "用户模块", description = "用户端相关接口")
public class UserController {
    @Autowired
    private AliCaptchaUtil aliCaptchaUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/captcha/verify")
    @Operation(summary = "验证码验证接口")
    public Result verifyCaptcha(@RequestBody CaptchaVerifyDTO captchaVerifyDTO) throws ExecutionException, InterruptedException {
        log.info("滑动验证码验证");
        aliCaptchaUtil.verifyIntelligentCaptcha(captchaVerifyDTO.getCaptchaVerifyParam());
        return Result.success();
    }

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录接口")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("用户登录：{}", userLoginDTO);
        // 根据用户名查询用户
        User loginUser = userService.getUserByUsername(userLoginDTO.getUsername());

        // 判断该用户是否存在
        if (loginUser == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        // 判断账户是否被禁用
        if (loginUser.getStatus().equals(StatusConstant.DISABLE)){
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        // 判断密码是否正确
        if (Md5Util.checkPasswordWithSalt(userLoginDTO.getPassword(),loginUser.getPassword())){
            // 登陆成功
            Map<String, Object> claims = new HashMap<>();
            claims.put(JwtClaimsConstant.USER_ID, loginUser.getId());
            claims.put(JwtClaimsConstant.USERNAME, loginUser.getUsername());
            String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);
            // 封装用户信息
            UserLoginVO userLoginVO = UserLoginVO.builder()
                    .id(loginUser.getId())
                    .avatar(loginUser.getAvatar())
                    .username(loginUser.getUsername())
                    .token(token)
                    .build();
            return Result.success(userLoginVO);
        }
        throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
    }

    /**
     * 用户注册
     * @param userRegisterDto
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册接口")
    public Result register(@RequestBody UserRegisterDto userRegisterDto){
        log.info("用户注册：{}", userRegisterDto);
        User registerUser = User.builder()
                .username(userRegisterDto.getUsername())
                .realName(userRegisterDto.getRealName())
                .idNumber(userRegisterDto.getIdCard())
                .password(Md5Util.getMD5StringWithSalt(userRegisterDto.getPassword()))
                .gender(userRegisterDto.getGender())
                .birthDate(userRegisterDto.getBirthDate())
                .medicalCard(userRegisterDto.getMedicalCard())
                .build();
        userService.register(registerUser);
        return Result.success();
    }
    /**
     * 重置密码
     * @param resetPasswordDTO
     * @return
     */
    @PostMapping("/resetPassword")
    @Operation(summary = "用户重置密码接口")
    public Result updatePassword(@RequestBody ResetPasswordDTO resetPasswordDTO){
        log.info("id为{}的用户重置密码请求", BaseContext.getCurrentId());
        userService.updatePassword(resetPasswordDTO);
        return Result.success();
    }

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/info")
    @Operation(summary = "用户信息获取接口")
    public Result<UserInfoVO> getUserInformation(){
        log.info("获得当前登录用户信息,id: {}",BaseContext.getCurrentId());
        UserInfoVO userInfoVO  = userService.getUserInfo();
        return Result.success(userInfoVO);
    }

    /**
     * 更新用户信息
     * @return
     */
    @PatchMapping("/updateInfo")
    @Operation(summary = "更新用户信息接口")
    public Result updateUserInfo(@RequestBody User updateUser){
        log.info("更新用户信息: {}",updateUser);
        userService.updateUserInfo(updateUser);
        return Result.success();
    }

}
