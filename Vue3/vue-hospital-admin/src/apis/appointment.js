import request from '@/utils/request'

/**
 * 分页查询
 * @param {*} params 
 * @returns 
 */
export const pageQueryApi = (params) => {
    return request({
        url: '/api/admin/appointment/page',
        method: 'get',
        params
    })
}

export const updateStatusApi = (id) => {
    return request({
        url: `/api/admin/appointment/finish/${id}`,
        method: 'patch',
    })
}