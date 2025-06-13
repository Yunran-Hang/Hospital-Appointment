import request from '@/utils/request'


// 获得分类列表
export const getCategoryListApi = () => {
  return request({
    url: '/api/user/category',
  })
}