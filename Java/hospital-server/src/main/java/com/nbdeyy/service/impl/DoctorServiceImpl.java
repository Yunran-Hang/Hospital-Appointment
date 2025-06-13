package com.nbdeyy.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nbdeyy.constant.AppointmentStatusConstant;
import com.nbdeyy.constant.MessageConstant;
import com.nbdeyy.constant.StatusConstant;
import com.nbdeyy.dto.DoctorListPageDTO;
import com.nbdeyy.dto.DoctorSearchDTO;
import com.nbdeyy.entity.Doctor;
import com.nbdeyy.exception.DoctorDeleteException;
import com.nbdeyy.mapper.AppointmentMapper;
import com.nbdeyy.mapper.DoctorMapper;
import com.nbdeyy.mapper.ScheduleMapper;
import com.nbdeyy.result.PageResult;
import com.nbdeyy.service.DoctorService;
import com.nbdeyy.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;
    /**
     * 分页查询医生列表
     * @param doctorSearchDTO
     * @return
     */
    public PageResult pageQuery(DoctorSearchDTO doctorSearchDTO) {
        PageHelper.startPage(doctorSearchDTO.getPage(), doctorSearchDTO.getPageSize());
        //下一条sql进行分页，自动加入limit关键字分页
        Page<DoctorSearchVO> page = doctorMapper.pageQuery(doctorSearchDTO);
        return new PageResult(page.getTotal(), page.getResult());

    }

    /**
     * 获取医生详细信息
     * @param doctorId
     * @return
     */
    public DoctorDetailVO getDoctorDetail(Long doctorId) {
        return doctorMapper.getDoctorDetail(doctorId);
    }

    /**
     * 分页查询医生列表
     * @param doctorListPageDTO
     * @return
     */
    public PageResult pageQueryDoctorList(DoctorListPageDTO doctorListPageDTO) {
        // 1.开启分页查询(注意sql语句结尾不要加;)
        PageHelper.startPage(doctorListPageDTO.getPage(), doctorListPageDTO.getPageSize());
        // 2.进行分页查询
        Page<DoctorDetailVO> p =  doctorMapper.pageQueryDoctorList(doctorListPageDTO);
        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 新增医生
     * @param doctor
     */
    public void save(Doctor doctor) {
        doctorMapper.save(doctor);
    }

    /**
     * 更新医生信息
     * @param doctor
     */
    public void update(Doctor doctor) {
        doctorMapper.update(doctor);
    }

    /**
     * 根据ID删除医生
     * @param doctorId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long doctorId) {
        // 1.如果医生存在未完成的预约，不能删除
        Integer count = appointmentMapper.countAppointmentsByDoctorId(doctorId, AppointmentStatusConstant.PENDING_MEDICAL_ATTENTION);
        if (count > 0){
            throw new DoctorDeleteException(MessageConstant.EXIST_PENDING_APPOINTMENTS);
        }
        // 2.删除doctor表中医生的信息
        doctorMapper.deleteById(doctorId);
        // 3.删除schedule表中医生的排班信息
        scheduleMapper.deleteAllScheduleByDoctorId(doctorId);
        // 4.删除appointment表中医生的所有预约信息
        appointmentMapper.deleteAllAppointmentByDoctorId(doctorId);
    }

    /**
     * 获得所有医生列表
     * @return
     */
    public List<DoctorListVO> getDoctorList() {
        return doctorMapper.getDoctorList(StatusConstant.ENABLE);
    }
}
