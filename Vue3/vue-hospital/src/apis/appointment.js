import request from '@/utils/request'

/**
 * 查询预约信息 
 * @param {*} id 
 * @returns 
 */
export const getMyAppointmentApi = (id) => {
    return request({
        url: `/api/user/myAppointment/${id}`,
        method: 'GET'
    })
}



/**
 * 提交预约信息
 */
export const createAppointmentApi = (data) => {
    return request({
        url: `/api/user/appointment/confirm`,
        method: 'POST',
        data
    })
}

/**
 * 获得预约的详细信息
 */
export const getAppointmentInfoApi = (params) => {
    return request({
        url: '/api/user/appointment/info',
        method: 'GET',
        params
    })
}