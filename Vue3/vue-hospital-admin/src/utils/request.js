import axios from 'axios'
import 'element-plus/theme-chalk/el-message.css'
import { ElMessage } from 'element-plus';
import router from '@/router';
import { useUserStore } from '@/stores/userStore'
import { hybridEncrypt, shouldEncrypt } from './crypto'
import { pemToBase64 } from './base64';


// 创建axios实例
const httpInstance = axios.create({
  timeout: 5000
})



// axios请求拦截器
httpInstance.interceptors.request.use( async (config) => {
  const userStore = useUserStore()
  const token = userStore.userInfo.token
  if (token) {
    config.headers.token = `${token}`
  }
  // 数据加密处理
  if (shouldEncrypt(config.method, config.url) && config.data) {
    try {
      const encrypted = await hybridEncrypt(config.data)
      console.log(encrypted)
      config.data = {
        aesKey: encrypted.aesKey,
        content: encrypted.encryptedData,
        signature: encrypted.signature,
        clientPublicKey: pemToBase64(encrypted.clientPublicKey) ,
        t: encrypted.timestamp
      }
      // config.data = {
      //   encryptedData: encrypted.data,
      //   encryptedKey: encrypted.key,
      //   encrypted: true // 标识这是加密数据
      // }
      // // 设置请求头标识
      // config.headers['Content-Encrypted'] = 'true'
    } catch (error) {
      console.error('数据加密失败:', error)
      ElMessage.error('数据加密失败')
      return Promise.reject(error)
    }
  }


  return config
}, e => Promise.reject(e))

// axios响应式拦截器
httpInstance.interceptors.response.use(res => {
  // 判断业务状态码
  if (res.data.code === 1){
    return res.data;
  }
  // 操作失败
  ElMessage.error(res.data.msg?res.data.msg:'服务异常')
  return res.data
}, e => {
  // 统一错误提示
  ElMessage.error(e.response.data.msg?e.response.data.msg:'服务异常')
  // 401 token处理
  if (e.response.status === 401){
    // 1. 清除过期用户信息
    const userStore = useUserStore()
    ElMessage.warning('登录信息已过期')
    userStore.removeInfo();
    // 2. 跳转登录页面
    router.replace('/login') 
  }

  return Promise.reject(e)
})


export default httpInstance
