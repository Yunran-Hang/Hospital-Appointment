import request from '@/utils/request'

// 获取排班列表(已测试)
export const getScheduleListApi = (params) => {
  return request({
    url: '/api/admin/schedule/list',
    method: 'get',
    params
  })
}

// 添加排班(已测试)
export const addScheduleApi = (data) => {
  return request({
    url: '/api/admin/schedule/add',
    method: 'post',
    data
  })
}

// 更新排班(已测试)
export const updateScheduleApi = (data) => {
  return request({
    url: '/api/admin/schedule/update',
    method: 'put',
    data
  })
}

// 删除排班(已测试)
export const deleteScheduleApi = (id) => {
  return request({
    url: `/api/admin/schedule/delete/${id}`,
    method: 'delete'
  })
}

// 批量生成排班(已测试)
export const batchGenerateScheduleApi = (data) => {
  return request({
    url: '/api/admin/schedule/addBatch',
    method: 'post',
    data
  })
}

// 获取医生可用时间段
export const getDoctorTimeSlotsApi = (scheduleId) => {
  return request({
    url: `/api/admin/timeslot/getAll/${scheduleId}`,
    method: 'get',
  })
}

// 生成时间段
export const generateTimeSlotsApi = (data) => {
  return request({
    url: '/api/schedule/generate-time-slots',
    method: 'post',
    data
  })
}

// 更新时间段状态
export const updateTimeSlotStatusApi = (data) => {
  return request({
    url: '/api/schedule/update-time-slot-status',
    method: 'put',
    data
  })
}