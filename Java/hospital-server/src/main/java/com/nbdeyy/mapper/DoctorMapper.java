package com.nbdeyy.mapper;

import com.github.pagehelper.Page;
import com.nbdeyy.annotation.AutoFill;
import com.nbdeyy.dto.DoctorListPageDTO;
import com.nbdeyy.dto.DoctorSearchDTO;
import com.nbdeyy.entity.Doctor;
import com.nbdeyy.enumeration.OperationType;
import com.nbdeyy.vo.DoctorDetailVO;
import com.nbdeyy.vo.DoctorListVO;
import com.nbdeyy.vo.DoctorSearchVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DoctorMapper {
    /**
     * 分页查询医生列表(用户端)
     * @param doctorSearchDTO
     * @return
     */
    Page<DoctorSearchVO> pageQuery(DoctorSearchDTO doctorSearchDTO);

    /**
     * 查询医生详细信息
     * @param doctorId
     * @return
     */
    DoctorDetailVO getDoctorDetail(Long doctorId);

    /**
     * 分页查询医生列表(管理端)
     * @param doctorListPageDTO
     * @return
     */
    Page<DoctorDetailVO> pageQueryDoctorList(DoctorListPageDTO doctorListPageDTO);

    /**
     * 新增医生
     * @param doctor
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into doctor (name, avatar, department_id, campus, title, specialty, totalSchedule, description, fee, location, status, create_time, update_time)" +
            "values (#{name},#{avatar},#{departmentId},#{campus},#{title},#{specialty},#{totalSchedule},#{description},#{fee},#{location},#{status},#{createTime},#{updateTime})")
    void save(Doctor doctor);

    /**
     * 更新医生信息
     * @param doctor
     */
    void update(Doctor doctor);

    /**
     * 根据ID删除医生信息
     * @param doctorId
     */
    @Delete("delete from doctor where id = #{doctorId}")
    void deleteById(Long doctorId);

    /**
     * 获得所有医生列表
     * @return
     */
    @Select("select * from doctor where status = #{status}")
    List<DoctorListVO> getDoctorList(Integer status);
}
