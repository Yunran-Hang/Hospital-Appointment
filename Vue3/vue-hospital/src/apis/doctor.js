import request from '@/utils/request'

export const getDoctorListApi = (params) => {
    return request({
        url: '/api/user/doctor/list',
        method: 'get',
        params
    })
}

/**
 * 查询医生的详细信息
 */
export const getDoctorDetailApi = (doctorId) => {
    return request({
        url: `/api/user/doctor/${doctorId}`,
    })
}

