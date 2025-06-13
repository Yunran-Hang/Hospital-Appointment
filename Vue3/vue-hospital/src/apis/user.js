import request from '@/utils/request'

export const loginApi = ({username,password}) => {
    return request({
      url: '/api/user/user/login',
      method: 'POST',
      data: {
        username: username,
        password: password
      }
    })
}

export const registerApi = (data) => {
    return request({
      url: '/api/user/user/register',
      method: 'POST',
      data
    })
}

export const userResetPasswordApi = (data) => {
    return request({
      url: '/api/user/user/resetPassword',
      method: 'POST',
      data
    })
}

export const userUpdateInfoApi = (data) => {
    return request({
      url: '/api/user/user/updateInfo',
      method: 'PATCH',
      data
    })
}

export const getUserInfoApi = () => {
    return request({
      url: '/api/user/user/info',
    })
}