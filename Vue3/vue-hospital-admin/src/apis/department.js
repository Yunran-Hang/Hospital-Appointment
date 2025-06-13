import request from '@/utils/request'


export const getDepartmentListApi = () => {
    return request({
        url: '/api/admin/dept',
        method: 'get'
    })
}