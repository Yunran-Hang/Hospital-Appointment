<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'
import { userUpdateInfoApi, getUserInfoApi } from '@/apis/user'

const userStore = useUserStore()



// 表单数据
const form = ref({
  username: '',
  realName: '',
  idNumber: '',
  gender: '',
  birthDate: '',
  phone: '',
  medicalCard: ''
})
const getUserInfo = async () => {
    const res = await getUserInfoApi();
    if (res.code == 1){
        form.value = res.data
    }
}
onMounted(() => getUserInfo())

// 从身份证提取生日和性别
const extractInfoFromIdCard = (idCard) => {
  if (idCard.length !== 18) return;
  // 提取生日信息 (格式: YYYYMMDD)
  const year = idCard.substring(6, 10);
  const month = idCard.substring(10, 12);
  const day = idCard.substring(12, 14);
  form.value.birthDate = `${year}-${month}-${day}`;
  // 提取性别信息 (倒数第二位: 奇数为男，偶数为女)
  const genderCode = parseInt(idCard.charAt(16));
  form.value.gender = genderCode % 2 === 1 ? 1 : 0;
};

watch(() => form.value.idNumber, (newValue) => {
  extractInfoFromIdCard(newValue)
})

// 身份证号脱敏显示
const maskedIdNumber = computed(() => {
  if (!form.value.idNumber || isEditing.value) return form.value.idNumber;
  
  const idNum = form.value.idNumber;
  if (idNum.length >= 18) {
    return idNum.substring(0, 3) + '*'.repeat(idNum.length - 7) + idNum.substring(idNum.length - 4);
  } else if (idNum.length >= 6) {
    return idNum.substring(0, 2) + '*'.repeat(idNum.length - 4) + idNum.substring(idNum.length - 2);
  }
  return idNum;
});

// 表单验证规则
const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  medicalCard: [
    { required: true, message: '请输入医保卡号', trigger: 'blur' }
  ]
}

const formRef = ref(null)
const isEditing = ref(false)

// 开始编辑
const startEdit = () => {
  isEditing.value = true
}

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false
  // 重置表单数据
  formRef.value?.clearValidate()
}

// 保存修改
const saveChanges = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await userUpdateInfoApi(form.value)
        if (res.code === 1) {
          ElMessage.success('保存成功')
          isEditing.value = false
        } else {
          ElMessage.error(res.message || '保存失败')
        }
      } catch (error) {
        ElMessage.error('网络错误，请稍后重试')
      }
    }
  })
}
</script>

<template>
  <el-card class="user-info-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <span class="title">基本资料</span>
        <div class="actions">
          <el-button v-if="!isEditing" type="primary" @click="startEdit">
            编辑资料
          </el-button>
          <template v-else>
            <el-button @click="cancelEdit">取消</el-button>
            <el-button type="primary" @click="saveChanges">保存</el-button>
          </template>
        </div>
      </div>
    </template>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      :disabled="!isEditing"
      class="user-form"
    >
      <el-row :gutter="30">
        <el-col :span="12">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" disabled class="input-field" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="form.realName" class="input-field" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="身份证号" prop="idNumber">
            <el-input 
              v-if="isEditing" 
              v-model="form.idNumber" 
              class="input-field" 
            />
            <el-input 
              v-else 
              v-model="maskedIdNumber" 
              disabled 
              class="input-field" 
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="form.gender" disabled  placeholder="请选择性别" class="input-field">
              <el-option label="男" :value="1" />
              <el-option label="女" :value="0" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="出生日期" prop="birthDate">
            <el-input v-model="form.birthDate" disabled class="input-field" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号码" prop="phone">
            <el-input v-model="form.phone" class="input-field" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="医保卡号" prop="medicalCard">
            <el-input v-model="form.medicalCard" placeholder="选填" class="input-field" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <div class="info-tips">
      <el-alert title="温馨提示" type="info" description="请确保您填写的个人信息真实有效，这将有助于医院为您提供更好的服务。"
        show-icon :closable="false"/>
    </div>
  </el-card>
</template>

<style lang="scss" scoped>
.user-info-card {
  margin: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .title {
      font-size: 18px;
      font-weight: 500;
      color: $text-primary;
      position: relative;
      padding-left: 12px;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 18px;
        background-color: $primary-color;
        border-radius: 2px;
      }
    }
    
    .actions {
      display: flex;
      gap: 12px;
    }
  }

  .user-form {
    padding: 20px 0;

    :deep(.el-form-item__label) {
      font-weight: 500;
      color: $text-secondary;
    }

    .input-field {
      width: 90%;
      max-width: 320px;
      transition: all 0.3s ease;
    }
  }

  .info-tips {
    margin-top: 20px;
    
    :deep(.el-alert) {
      border-radius: 6px;
      background-color: rgba($info-color, 0.05);
      border: 1px solid rgba($info-color, 0.1);
      
      .el-alert__title {
        font-weight: 500;
      }
    }
  }
}

// 添加过渡动画
.el-form-item {
  transition: all 0.3s ease;

  &:hover {
    transform: translateX(5px);
  }
}

.el-input, .el-select, .el-date-picker {
  transition: all 0.3s ease;

  &:hover:not(:disabled) {
    transform: translateY(-2px);
  }
}

// 添加禁用状态样式
.el-form.is-disabled {
  .el-input__inner,
  .el-select__input,
  .el-date-picker__input {
    background-color: #f5f7fa;
    border-color: #e4e7ed;
    color: $text-secondary;
    cursor: not-allowed;
  }
}
</style>