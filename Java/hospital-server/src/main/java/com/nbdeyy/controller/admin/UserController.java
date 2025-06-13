package com.nbdeyy.controller.admin;

import com.nbdeyy.annotation.Log;
import com.nbdeyy.dto.UserListPageDTO;
import com.nbdeyy.entity.User;
import com.nbdeyy.result.PageResult;
import com.nbdeyy.result.Result;
import com.nbdeyy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminUserController")
@Slf4j
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户信息
     * @param userListPageDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(UserListPageDTO userListPageDTO){
        log.info("分页查询用户信息: {}",userListPageDTO);
        PageResult p = userService.pageQuery(userListPageDTO);
        return Result.success(p);
    }

    /**
     * 根据ID删除用户
     * @param userId
     * @return
     */
    @DeleteMapping("/delete/{userId}")
    @Log
    public Result deleteUserById(@PathVariable Long userId) {
        log.info("根据ID删除用户: {}", userId);
        userService.deleteById(userId);
        return Result.success();
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @PatchMapping("/update")
    @Log
    public Result update(@RequestBody User user) {
        log.info("更新用户信息: {}", user);
        userService.updateByAdmin(user);
        return Result.success();
    }

    /**
     * 添加新用户
     * @param user
     * @return
     */
    @PostMapping("/add")
    @Log
    public Result add(@RequestBody User user){
        log.info("创建新用户: {}",user);
        userService.save(user);
        return Result.success();
    }
}
