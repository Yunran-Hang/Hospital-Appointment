<script setup>
import { ref} from 'vue'
import { ElMessage } from 'element-plus'
import 'element-plus/theme-chalk/el-message.css'
import { Lock,Unlock } from '@element-plus/icons-vue'
import { userResetPasswordApi } from '@/apis/user'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()

// 重置密码表单数据
const resetForm = ref({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
})

// 表单引用
const resetFormRef = ref(null)

// 验证新密码与确认密码是否一致
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== resetForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 验证新密码（当新密码改变时，重新验证确认密码）
const validateNewPassword = (rule, value, callback) => {
  // 先进行基本验证
  if (!value) {
    callback(new Error('请输入新密码'))
    return
  }
  if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
    return
  }
  
  // 如果确认密码已经输入，重新验证确认密码字段
  if (resetForm.value.confirmPassword) {
    resetFormRef.value.validateField('confirmPassword')
  }
  
  callback()
}

// 表单验证规则
const resetRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
    { validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const userStore = useUserStore()
// 处理重置密码
const handleResetPassword = async () => {
  resetFormRef.value.validate(async (valid) => {
    if (valid) {
        const params = ref({
            oldPassword: resetForm.value.oldPassword,
            newPassword: resetForm.value.newPassword
        })
        const res = await userResetPasswordApi(params.value)
        console.log(res)
        if (res.code === 1){
            ElMessage.success("密码重置成功")
            // 清空表单
            resetForm.value = {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
            }
            // 重置表单验证状态
            resetFormRef.value.resetFields()
            ElMessage.warning("请重新登录")
            userStore.removeInfo()
            router.push('/login')
        }
    }
  })
}
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header"><span>重置密码</span></div>
    </template>
    
    <el-form
      ref="resetFormRef" :model="resetForm" :rules="resetRules" label-width="100px" class="reset-form">
      <el-form-item prop="oldPassword" label="当前密码">
        <el-input
          v-model="resetForm.oldPassword" type="password" placeholder="请输入当前密码"
          :prefix-icon="Unlock" show-password
        />
      </el-form-item>
      
      <el-form-item prop="newPassword" label="新密码">
        <el-input
          v-model="resetForm.newPassword" type="password" placeholder="请输入新密码"
          :prefix-icon="Lock" show-password />
      </el-form-item>
      
      <el-form-item prop="confirmPassword" label="确认密码">
        <el-input v-model="resetForm.confirmPassword" type="password" placeholder="请再次输入新密码"
          :prefix-icon="Lock" show-password />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="handleResetPassword">确认修改</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<style lang="scss" scoped>
.page-container {
  max-width: 600px;
  margin: 0 auto;
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    span {
      font-size: 18px;
      font-weight: 500;
      color: $text-primary;
    }
  }
  
  .reset-form {
    padding: 20px 0;
    
    .el-form-item:last-child {
      margin-bottom: 0;
      
      .el-button {
        background: linear-gradient(135deg, $primary-color 0%, $primary-dark 100%);
        border: none;
        padding: 10px 30px;
        
        &:hover {
          background: linear-gradient(135deg, $primary-light 0%, $primary-color 100%);
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .page-container {
    margin: 0;
    
    .reset-form {
      padding: 10px 0;
    }
  }
}
</style>