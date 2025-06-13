import request from '@/utils/request'

/**
 * 登录请求
 *
 * @returns 
 */
export const loginApi = (data) => {
  return request({
    url: '/api/admin/login',
    method: 'post',
    data
  })
}