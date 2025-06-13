package com.nbdeyy.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nbdeyy.constant.MessageConstant;
import com.nbdeyy.constant.StatusConstant;
import com.nbdeyy.context.BaseContext;
import com.nbdeyy.dto.ResetPasswordDTO;
import com.nbdeyy.dto.UserListPageDTO;
import com.nbdeyy.entity.Admin;
import com.nbdeyy.entity.User;
import com.nbdeyy.exception.AccountAlreadyExistException;
import com.nbdeyy.exception.DuplicatePasswordException;
import com.nbdeyy.exception.PasswordErrorException;
import com.nbdeyy.mapper.AppointmentMapper;
import com.nbdeyy.mapper.UserMapper;
import com.nbdeyy.result.PageResult;
import com.nbdeyy.result.Result;
import com.nbdeyy.service.UserService;
import com.nbdeyy.utils.Md5Util;
import com.nbdeyy.vo.UserInfoVO;
import com.nbdeyy.vo.UserPageQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    /**
     * 通过用户名查询用户信息
     * @param username
     * @return
     */
    public User getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    /**
     * 注册账号
     *
     * @param registerUser
     */
    public void register(User registerUser) {
        // 1.判断用户名是否已存在
        Integer countByUsername = userMapper.countByUsername(registerUser.getUsername());
        if (countByUsername > 0) throw new AccountAlreadyExistException(MessageConstant.ACCOUNT_ALREADY_EXIST);
        User dbUser = userMapper.getUserByIdNumber(registerUser.getIdNumber());
        if (dbUser != null) {
            throw new AccountAlreadyExistException(MessageConstant.ID_NUMBER_ALREADY_EXIST);
        }
        // 2.注册用户，向数据库插入数据
        registerUser.setStatus(StatusConstant.ENABLE);
        userMapper.insert(registerUser);

    }

    /**
     * 重置密码
     * @param resetPasswordDTO
     */
    public void updatePassword(ResetPasswordDTO resetPasswordDTO) {
        User user = userMapper.getUserById(BaseContext.getCurrentId());
        // 先判断旧密码是否正确
        if (!Md5Util.checkPasswordWithSalt(resetPasswordDTO.getOldPassword(),user.getPassword())){
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR); // 旧密码错误异常
        }
        // 然后判断旧密码是否和新密码一致
        if (user.getPassword().equals(Md5Util.getMD5StringWithSalt(resetPasswordDTO.getNewPassword()))){
            throw new DuplicatePasswordException(MessageConstant.DUPLICATE_PASSWORD);

        }
        // 2.修改密码
        user.setPassword(Md5Util.getMD5StringWithSalt(resetPasswordDTO.getNewPassword()));
        userMapper.update(user);
    }

    /**
     * 获取用户信息
     * @return
     */
    public UserInfoVO getUserInfo() {
        User user = userMapper.getUserById(BaseContext.getCurrentId());
        return UserInfoVO.builder()
                .username(user.getUsername())
                .realName(user.getRealName())
                .idNumber(user.getIdNumber())
                .gender(user.getGender())
                .avatar(user.getAvatar())
                .birthDate(user.getBirthDate())
                .phone(user.getPhone())
                .medicalCard(user.getMedicalCard())
                .build();
    }

    /**
     * 更新用户信息(用户端)
     * @param updateUser
     */
    public void updateUserInfo(User updateUser) {
        // 设置从token中获得的用户id
        updateUser.setId(BaseContext.getCurrentId());
        userMapper.update(updateUser);
    }

    /**
     * 根据用户名获取管理员信息
     * @param username
     * @return
     */
    public Admin getAdminByUsername(String username) {
        return userMapper.getAdminByUsername(username);
    }

    /**
     * 分页查询用户信息
     * @param userListPageDTO
     * @return
     */
    public PageResult pageQuery(UserListPageDTO userListPageDTO) {
        PageHelper.startPage(userListPageDTO.getPage(), userListPageDTO.getPageSize());
        Page<UserPageQueryVO> p = userMapper.pageQuery(userListPageDTO);
        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 根据ID删除用户
     * @param userId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long userId) {
        // 1.删除user表中的用户信息
        userMapper.deleteById(userId);
        // 2.删除appointment表中的预约信息
        appointmentMapper.deleteAllAppointmentByUserId(userId);
    }

    /**
     * 管理员更新用户信息
     * @param user
     */
    public void updateByAdmin(User user) {
        if (user.getPassword() != null && user.getPassword().length() > 0){
            user.setPassword(Md5Util.getMD5StringWithSalt(user.getPassword()));
        }
        userMapper.update(user);
    }

    /**
     * 创建新用户(管理员)
     * @param user
     */
    public void save(User user) {
        // 1.判断用户名是否已存在
        User dbUser = userMapper.getUserByUsername(user.getUsername());
        if (dbUser != null) {
            throw new AccountAlreadyExistException(MessageConstant.ACCOUNT_ALREADY_EXIST);
        }
        dbUser = userMapper.getUserByIdNumber(user.getIdNumber());
        if (dbUser != null) {
            throw new AccountAlreadyExistException(MessageConstant.ID_NUMBER_ALREADY_EXIST);
        }
        userMapper.save(user);
    }
}
