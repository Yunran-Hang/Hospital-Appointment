package com.nbdeyy.service;

import com.nbdeyy.dto.DoctorListPageDTO;
import com.nbdeyy.dto.DoctorSearchDTO;
import com.nbdeyy.entity.Doctor;
import com.nbdeyy.result.PageResult;
import com.nbdeyy.vo.DoctorDetailVO;
import com.nbdeyy.vo.DoctorListVO;
import com.nbdeyy.vo.DoctorScheduleVO;

import java.util.List;

public interface DoctorService {
    /**
     * 分页查询医生列表
     * @param doctorSearchDTO
     * @return
     */
    PageResult pageQuery(DoctorSearchDTO doctorSearchDTO);

    /**
     * 获取医生详细信息
     * @param doctorId
     * @return
     */
    DoctorDetailVO getDoctorDetail(Long doctorId);

    /**
     * 分页查询医生列表
     * @param doctorListPageDTO
     * @return
     */
    PageResult pageQueryDoctorList(DoctorListPageDTO doctorListPageDTO);

    /**
     * 新增医生
     * @param doctor
     */
    void save(Doctor doctor);

    /**
     * 更新医生信息
     * @param doctor
     */
    void update(Doctor doctor);

    /**
     * 根据ID删除医生
     * @param doctorId
     */
    void deleteById(Long doctorId);

    /**
     * 获得所有医生列表
     * @return
     */
    List<DoctorListVO> getDoctorList();

}
