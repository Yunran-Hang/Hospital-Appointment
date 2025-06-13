import request from '@/utils/request'

/**
 * 分页查询操作日志
 * @param {Object} params 查询参数
 * @returns 
 */
export const pageQueryLogApi = (params) => {
  return request({
    url: '/api/admin/log/page',
    method: 'get',
    params
  })
}