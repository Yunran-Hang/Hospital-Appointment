import request from '@/utils/request'

/**
 * 查询医生两周内的排班信息
 */
export const getDoctorScheduleApi = (doctorId) => {
    return request({
        url: `/api/user/schedule/${doctorId}`,
    })
}