<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';
import 'element-plus/theme-chalk/el-message.css'
import { ElMessage } from 'element-plus';
import { User, Lock, UserFilled, CreditCard,Postcard } from '@element-plus/icons-vue';
import CaptchaComponent from './components/captcha.vue';
import { loginApi, registerApi } from '@/apis/user';

const router = useRouter();
const route = useRoute();

// 控制登录/注册表单的显示
const activeForm = ref('login');

// 登录表单数据
const loginForm = ref({
  username: '',
  password: '',
});

// 登录表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
};

// 注册表单数据
const registerForm = ref({
  username: '',
  realName: '',
  idCard: '',
  medicalCard: '',
  password: '',
  confirmPassword: '',
  gender: '',
  birthDate: '',
});

// 从身份证提取生日和性别
const extractInfoFromIdCard = (idCard) => {
  if (idCard.length !== 18) return;
  // 提取生日信息 (格式: YYYYMMDD)
  const year = idCard.substring(6, 10);
  const month = idCard.substring(10, 12);
  const day = idCard.substring(12, 14);
  registerForm.value.birthDate = `${year}-${month}-${day}`;
  // 提取性别信息 (倒数第二位: 奇数为男，偶数为女)
  const genderCode = parseInt(idCard.charAt(16));
  registerForm.value.gender = genderCode % 2 === 1 ? 1 : 0;
};

// 监听身份证输入变化
const handleIdCardChange = () => {
  if (registerForm.value.idCard.length === 18) {
    extractInfoFromIdCard(registerForm.value.idCard);
  } else {
    registerForm.value.birthDate = '';
    registerForm.value.gender = '';
  }
};

// 验证身份证号码是否合法
const validateIdCard = (rule, value, callback) => {
  // 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
  
  if (!value) {
    return callback(new Error('请输入身份证号码'));
  }
  if (!reg.test(value)) {
    return callback(new Error('身份证号码格式不正确'));
  }
  // 这里可以添加更复杂的身份证验证逻辑，如校验码验证等
  callback();
};

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.value.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

// 注册表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名长度不能少于3位', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号码', trigger: 'blur' },
    { validator: validateIdCard, trigger: 'blur' }
  ],
  medicalCard: [
    { required: true, message: '请输入医保卡号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
};

// 登录表单引用
const loginFormRef = ref(null);
// 注册表单引用
const registerFormRef = ref(null);
const captchaVerified = ref(false);
// 添加对验证码组件的引用
const captchaRef = ref(null);
// 处理登录逻辑
import { useUserStore } from '@/stores/userStore';
const userStore = useUserStore();

const handleLogin = () => {
    const {username,password} = loginForm.value;
    loginFormRef.value.validate(async(valid) => {
        if (valid) {
            // 检查滑块验证
            if (!captchaVerified.value) {
                ElMessage.warning('请先完成滑块验证');
                return;
            }
            // 登录API调用
            console.log('登录表单提交', loginForm.value);
            const res = await loginApi({username,password})
            if (res.code == 1){
                ElMessage.success('登录成功');
                //把得到的token存储到pinia中
                userStore.setUserInfo(res.data);

                const redirect = route.query.redirect || '/'

                try {
                  await router.push(redirect)
                } catch (err) {
                  await router.push('/')
                }
            }else{
                // 登录失败，刷新验证码
                captchaVerified.value = false;
                captchaRef.value.refreshCaptcha(); // 刷新验证码
            }
        }
    });
};
const handleCaptchaVerify = (captchaResult) => {
    // 处理滑块验证结果
    console.log('滑块验证结果: ', captchaResult);
    captchaVerified.value = captchaResult;
}

// 处理注册
const handleRegister = () => {
  registerFormRef.value.validate(async (valid) => {
    if (valid) {
      // 检查滑块验证
      if (!captchaVerified.value) {
        ElMessage.warning('请先完成滑块验证');
        return;
      }
      // 这里添加注册API调用，包括检查用户名是否重复
      console.log('注册表单提交', registerForm.value);
      const res = await registerApi(registerForm.value);
      if (res.code == 1){
        ElMessage.success('注册成功，请登录');
        switchToLogin();
      }
    }
  });
};

// 切换到登录表单
const switchToLogin = () => {
    activeForm.value = 'login';
    captchaVerified.value = false;
    registerForm.value = {
        username: '',
        realName: '',
        idCard: '',
        medicalCard: '',
        password: '',
        confirmPassword: '',
        gender: '',
        birthDate: '',
    };
};

// 切换到注册表单
const switchToRegister = () => {
    activeForm.value = 'register';
    captchaVerified.value = false;
    loginForm.value = {
        username: '',
        password: '',
    };
};
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>宁波市第二医院</h1>
        <p>在线门诊预约挂号系统</p>
      </div>
      <!-- 登录表单 -->
      <div v-if="activeForm === 'login'" class="form-container">
        <h2>账号登录</h2>
        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-position="top">
          <el-form-item prop="username" label="用户名">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" :prefix-icon="User"/>
          </el-form-item>
          <el-form-item prop="password" label="密码">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
          </el-form-item>
          <!-- 滑块验证占位区域 -->
          <div class="slide-verify-container">
            <p class="verify-title">请完成滑块验证</p>
            <!-- 阿里云滑块验证 -->
            <CaptchaComponent ref="captchaRef" @handle-captcha-verify="handleCaptchaVerify" />
          </div>
          
          <el-form-item>
            <el-button type="primary" class="submit-btn" @click="handleLogin">登录</el-button>
          </el-form-item>
        </el-form>
        
        <div class="form-footer">
          <p>还没有账号？<a href="javascript:;" @click="switchToRegister">立即注册</a></p>
        </div>
      </div>
      
      <!-- 注册表单 -->
      <div v-else class="form-container">
        <h2>用户注册</h2>
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          label-position="top"
        >
          <el-form-item prop="username" label="用户名">
            <el-input 
              v-model="registerForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
            />
          </el-form-item>
          
          <el-form-item prop="realName" label="真实姓名">
            <el-input 
              v-model="registerForm.realName"
              placeholder="请输入真实姓名"
              :prefix-icon="UserFilled"
            />
          </el-form-item>
          
          <el-form-item prop="idCard" label="身份证号码">
            <el-input 
              v-model="registerForm.idCard"
              placeholder="请输入身份证号码"
              :prefix-icon="Postcard"
              @input="handleIdCardChange"
            />
          </el-form-item>
          
          <!-- 显示从身份证提取的信息 -->
          <div class="extracted-info" v-if="registerForm.birthday || registerForm.gender">
            <p>系统已自动识别：</p>
            <p v-if="registerForm.birthDate">出生日期：{{ registerForm.birthDate }}</p>
            <p v-if="registerForm.gender">性别：{{ registerForm.gender==1?'男':'女' }}</p>
          </div>
          
          <el-form-item prop="medicalCard" label="医保卡号">
            <el-input 
              v-model="registerForm.medicalCard"
              placeholder="请输入医保卡号"
              :prefix-icon="CreditCard"
            />
          </el-form-item>
          
          <el-form-item prop="password" label="密码">
            <el-input 
              v-model="registerForm.password"
              type="password"
              placeholder="请设置密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <!-- 在确认密码表单项之后，注册按钮之前添加 -->
          <el-form-item prop="confirmPassword" label="确认密码">
            <el-input 
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <!-- 滑块验证占位区域 -->
          <div class="slide-verify-container">
            <p class="verify-title">请完成滑块验证</p>
            <div class="slide-verify-placeholder">
                <!-- 这里集成阿里云滑块验证组件 -->
                <!-- 阿里云滑块验证 -->
                <CaptchaComponent ref="captchaRef" @handle-captcha-verify="handleCaptchaVerify" />
            </div>
          </div>
          
          <el-form-item>
            <el-button type="primary" class="submit-btn" @click="handleRegister">注册</el-button>
          </el-form-item>
        </el-form>
        
        <div class="form-footer">
          <p>已有账号？<a href="javascript:;" @click="switchToLogin">返回登录</a></p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
// 只保留一个导入，移除冲突的导入
@use '@/styles/var.scss' as *;

.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: url('@/assets/images/background.jpg') no-repeat center center;
  background-size: cover;
  padding: 20px;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.4);
    backdrop-filter: blur(3px);
  }
}

.login-box {
  width: 100%;
  max-width: 500px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
  }
}

.login-header {
  background: linear-gradient(135deg, $primary-color, lighten($primary-color, 10%));
  color: white;
  text-align: center;
  padding: 30px 20px;
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    bottom: -10px;
    left: 50%;
    transform: translateX(-50%);
    width: 0;
    height: 0;
    border-left: 10px solid transparent;
    border-right: 10px solid transparent;
    border-top: 10px solid $primary-color;
  }
  
  h2 {
    margin: 0;
    font-size: 28px;
    font-weight: 600;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  }
}

.form-container {
  padding: 40px 30px 30px;
  
  .el-form-item {
    margin-bottom: 25px;
    
    .el-input {
      height: 45px;
      
      .el-input__wrapper {
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
        
        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }
        
        &.is-focus {
          box-shadow: 0 0 0 2px rgba($primary-color, 0.2);
        }
      }
    }
  }
}

.submit-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  background: linear-gradient(135deg, $primary-color, lighten($primary-color, 5%));
  border: none;
  transition: all 0.3s ease;
  
  &:hover {
    background: linear-gradient(135deg, darken($primary-color, 5%), $primary-color);
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba($primary-color, 0.3);
  }
  
  &:active {
    transform: translateY(0);
  }
}

.form-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
  
  p {
    margin: 0;
    color: $text-secondary;
    font-size: 14px;
    
    a {
      color: $primary-color;
      text-decoration: none;
      font-weight: 500;
      transition: color 0.3s ease;
      
      &:hover {
        color: darken($primary-color, 10%);
        text-decoration: underline;
      }
    }
  }
}

.slide-verify-container {
  margin-bottom: 20px;
  
  .verify-title {
    font-size: 14px;
    color: $text-secondary;
    margin-bottom: 10px;
  }
  
  .slide-verify-placeholder {
    height: 40px;
    background-color: #f5f7fa;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.extracted-info {
  background-color: #f0f9ff;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 15px;
  
  p {
    margin: 0;
    font-size: 13px;
    color: #1e40af;
    
    &:first-child {
      font-weight: 500;
      margin-bottom: 5px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .login-container {
    padding: 10px;
  }
  
  .login-box {
    max-width: 100%;
    margin: 0 10px;
  }
  
  .login-header {
    padding: 20px 15px;
    
    h2 {
      font-size: 24px;
    }
  }
  
  .form-container {
    padding: 30px 20px 20px;
  }
}
</style>