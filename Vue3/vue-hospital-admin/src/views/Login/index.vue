<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import 'element-plus/theme-chalk/el-message.css'
import { ElMessage } from 'element-plus'
import { loginApi } from '@/apis/admin'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()
const router = useRouter()
const loading = ref(false)

// 登录表单数据
const loginForm = ref({
  username: 'admin',
  password: '123456',
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const loginFormRef = ref()

// 登录处理
const handleLogin = () => {
  loginFormRef.value.validate(async(valid) =>{
    if (valid) {
      loading.value = true
      try {
        // 调用登录接口
        const res = await loginApi(loginForm.value)
        if (res.code == 1){
          userStore.setUserInfo(res.data)
          ElMessage.success('登录成功！')
          router.push('/')
        }
      } catch (error) {
        console.error('登录请求失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 当前时间
const currentTime = ref(new Date().toLocaleString())
setInterval(() => {
  currentTime.value = new Date().toLocaleString()
}, 1000)
</script>

<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
      <div class="decoration-wave"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 左侧信息区域 -->
      <div class="login-info">
        <div class="hospital-branding">
          <img src="@/assets/images/logo.png" alt="医院logo" class="hospital-logo">
          <h1 class="hospital-title">宁波第二医院</h1>
          <p class="hospital-subtitle">后台管理系统</p>
        </div>
        
        <div class="info-content">
          <h3 class="welcome-title">欢迎使用</h3>
          <p class="welcome-desc">专业的医院管理解决方案</p>
          <ul class="feature-list">
            <li>
              <el-icon><UserFilled /></el-icon>
              <span>医生信息管理</span>
            </li>
            <li>
              <el-icon><Calendar /></el-icon>
              <span>智能排班系统</span>
            </li>
            <li>
              <el-icon><Document /></el-icon>
              <span>预约记录查询</span>
            </li>
          </ul>
        </div>
        
        <div class="time-display">
          <el-icon><Clock /></el-icon>
          <span>{{ currentTime }}</span>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-form-container">
        <div class="form-header">
          <h2 class="form-title">管理员登录</h2>
          <p class="form-subtitle">请输入您的登录凭据</p>
        </div>

        <el-form ref="loginFormRef" :model="loginForm" :rules="rules" class="login-form" size="large">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" clearable/>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"
                prefix-icon="Lock" show-password clearable />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              <span v-if="!loading">立即登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>
        
        <!-- 演示账号提示 -->
        <div class="demo-info">
          <el-alert
            title="演示账号"
            type="info"
            :closable="false"
            show-icon
          >
            <template #default>
              <p>用户名: <strong>admin</strong></p>
              <p>密码: <strong>123456</strong></p>
            </template>
          </el-alert>
        </div>
      </div>
    </div>
    
    <!-- 底部版权信息 -->
    <div class="footer">
      <p>&copy; 2024 宁波第二医院. All rights reserved.</p>
      <p>Powered by Vue 3 + Element Plus</p>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  padding: 20px;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

.decoration-wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100px;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 120" preserveAspectRatio="none"><path d="M321.39,56.44c58-10.79,114.16-30.13,172-41.86,82.39-16.72,168.19-17.73,250.45-.39C823.78,31,906.67,72,985.66,92.83c70.05,18.48,146.53,26.09,214.34,3V0H0V27.35A600.21,600.21,0,0,0,321.39,56.44Z" fill="rgba(255,255,255,0.1)"></path></svg>') repeat-x;
  background-size: 1200px 100px;
  animation: wave 10s linear infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes wave {
  0% {
    background-position-x: 0;
  }
  100% {
    background-position-x: 1200px;
  }
}

/* 登录卡片 */
.login-card {
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

/* 左侧信息区域 */
.login-info {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: white;
  position: relative;
}

.login-info::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>') repeat;
  opacity: 0.3;
}

.hospital-branding {
  text-align: center;
  position: relative;
  z-index: 1;
}

.hospital-logo {
  width: 80px;
  height: 80px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  margin-bottom: 20px;
}

.hospital-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.hospital-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.info-content {
  position: relative;
  z-index: 1;
}

.welcome-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 12px 0;
}

.welcome-desc {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 30px 0;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-list li {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  font-size: 15px;
  opacity: 0.9;
}

.feature-list li .el-icon {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.8);
}

.time-display {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  opacity: 0.8;
  position: relative;
  z-index: 1;
}

/* 右侧登录表单 */
.login-form-container {
  flex: 1;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
}

.form-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.login-form {
  width: 100%;
}

.login-form .el-form-item {
  margin-bottom: 24px;
}

.login-form .el-input {
  height: 50px;
}

.login-form .el-input__wrapper {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.login-form .el-input__wrapper:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.login-form .el-input.is-focus .el-input__wrapper {
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.3);
}

.login-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #409EFF 0%, #66b3ff 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.4);
}

.demo-info {
  margin-top: 20px;
}

.demo-info .el-alert {
  border-radius: 12px;
  border: none;
  background: rgba(64, 158, 255, 0.1);
}

.demo-info p {
  margin: 4px 0;
  font-size: 13px;
}

/* 底部版权 */
.footer {
  position: absolute;
  bottom: 20px;
  text-align: center;
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
  z-index: 1;
}

.footer p {
  margin: 4px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    max-width: 400px;
    min-height: auto;
  }
  
  .login-info {
    padding: 30px 20px;
    min-height: 300px;
  }
  
  .login-form-container {
    padding: 30px 20px;
  }
  
  .hospital-title {
    font-size: 24px;
  }
  
  .form-title {
    font-size: 24px;
  }
  
  .feature-list {
    display: none;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 10px;
  }
  
  .login-card {
    margin: 0;
    border-radius: 16px;
  }
  
  .login-info,
  .login-form-container {
    padding: 20px;
  }
}
</style>