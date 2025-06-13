import request from '@/utils/request'

export const verifyCaptchaApi = (data) => {
  return request({
    url: '/api/user/user/captcha/verify',
    method: 'POST',
    data
  })
}