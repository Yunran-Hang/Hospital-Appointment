import request from '@/utils/request'


// 分页查询用户信息
export const pageQueryUserInfoApi = (params) => {
    return request({
        url: '/api/admin/user/page',
        method: 'get',
        params
    })
}

export const addNewUserApi = (data) => {
    return request({
        url: '/api/admin/user/add',
        method: 'post',
        data
    })
}

// 更新用户信息
export const updateUserInfoApi = (data) => {
    return request({
        url: '/api/admin/user/update',
        method: 'patch',
        data
    })
}

// 根据id删除用户
export const deleteUserApi = (userId) => {
    return request({
        url: `/api/admin/user/delete/${userId}`,
        method: 'delete'
    })
}
