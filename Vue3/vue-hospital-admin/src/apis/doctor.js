import request from '@/utils/request'

/**
 * 分页查询医生列表
 * @param {*} params 
 * @returns 
 */
export const pageQueryDoctorListApi = (params) => {
  return request({
    url: '/api/admin/doctor/page',
    method: 'get',
    params
  })
}

/**
 * 新增医生
 * @param {*} data
 * @returns
 */
export const addDoctorApi = (data) => {
  return request({
    url: '/api/admin/doctor/save',
    method: 'post',
    data
  })
}

/**
 * 修改医生
 * @param {*} data
 * @returns
 */
export const updateDoctorApi = (data) => {
  return request({
    url: '/api/admin/doctor/update',
    method: 'put',
    data
  })
}
/**
 * 删除医生
 * @param {*} id 
 * @returns 
 */
export const deleteDoctorApi = (id) => {
  return request({
    url: `/api/admin/doctor/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 获得所有医生列表
 */
export const getDoctorListApi = () => {
  return request({
    url: '/api/admin/doctor/list',
    method: 'get'
  })
}