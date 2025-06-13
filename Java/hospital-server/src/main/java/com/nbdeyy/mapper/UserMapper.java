package com.nbdeyy.mapper;

import com.github.pagehelper.Page;
import com.nbdeyy.annotation.AutoFill;
import com.nbdeyy.dto.UserListPageDTO;
import com.nbdeyy.entity.Admin;
import com.nbdeyy.entity.User;
import com.nbdeyy.enumeration.OperationType;
import com.nbdeyy.vo.UserPageQueryVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    @Select("select * from user where username = #{username} ")
    User getUserByUsername(String username);

    /**
     * 根据用户名统计有多少账户，用来判断用户名是否重复
     * @param username
     * @return
     */
    @Select("select count(*) from user where username = #{username};")
    Integer countByUsername(String username);

    /**
     * 插入新用户
     * @param registerUser
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into user (username, password, real_name, id_number, gender, birth_date, medical_card, status, create_time, update_time) VALUES " +
            "(#{username},#{password},#{realName},#{idNumber},#{gender},#{birthDate},#{medicalCard},#{status},#{createTime},#{updateTime})")
    void insert(User registerUser);

    /**
     * 根据id获取用户信息
     * @param currentId
     * @return
     */
    @Select("select * from user where id = #{currentId}")
    User getUserById(Long currentId);

    /**
     * 更新用户信息
     * @param user
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(User user);

    /**
     * 根据用户名获取管理员信息
     * @param username
     * @return
     */
    @Select("select * from admin where username = #{username}")
    Admin getAdminByUsername(String username);

    /**
     * 分页查询用户信息
     * @param userListPageDTO
     * @return
     */
    Page<UserPageQueryVO> pageQuery(UserListPageDTO userListPageDTO);

    /**
     * 根据ID删除用户
     * @param userId
     */
    @Delete("delete from user where id = #{userId}")
    void deleteById(Long userId);

    /**
     * 通过身份证号查询用户信息
     * @param idNumber
     * @return
     */
    @Select("select * from user where id_number = #{idNumber}")
    User getUserByIdNumber(String idNumber);

    /**
     * 管理员添加用户信息
     * @param user
     */
    @AutoFill(value = OperationType.INSERT)
    void save(User user);
}
