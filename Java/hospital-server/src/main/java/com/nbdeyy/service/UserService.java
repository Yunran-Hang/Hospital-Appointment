package com.nbdeyy.service;

import com.nbdeyy.dto.ResetPasswordDTO;
import com.nbdeyy.dto.UserListPageDTO;
import com.nbdeyy.entity.Admin;
import com.nbdeyy.entity.User;
import com.nbdeyy.result.PageResult;
import com.nbdeyy.vo.UserInfoVO;

public interface UserService {
    /**
     * 通过用户名查询用户信息
     * @param username
     * @return
     */
    User getUserByUsername(String username);

    /**
     * 注册账号
     *
     * @param registerUser
     */
    void register(User registerUser);

    /**
     * 重置密码
     * @param resetPasswordDTO
     */
    void updatePassword(ResetPasswordDTO resetPasswordDTO);

    /**
     * 获取用户信息
     * @return
     */
    UserInfoVO getUserInfo();

    /**
     * 更新用户信息(用户端)
     * @param updateUser
     */
    void updateUserInfo(User updateUser);

    /**
     * 根据用户名获取管理员信息
     *
     * @param username
     * @return
     */
    Admin getAdminByUsername(String username);

    /**
     * 分页查询用户信息
     * @param userListPageDTO
     * @return
     */
    PageResult pageQuery(UserListPageDTO userListPageDTO);

    /**
     * 根据ID删除用户
     * @param userId
     */
    void deleteById(Long userId);

    /**
     * 管理员更新用户
     * @param user
     */
    void updateByAdmin(User user);

    /**
     * 创建新用户(管理员)
     * @param user
     */
    void save(User user);
}
