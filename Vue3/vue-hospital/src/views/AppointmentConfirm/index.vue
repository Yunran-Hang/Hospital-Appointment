<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import 'element-plus/theme-chalk/el-message.css'
import { ElMessage } from 'element-plus'
import { Check, Close, Calendar, Clock, User, Money, School } from '@element-plus/icons-vue'
import { createAppointmentApi } from '@/apis/appointment'
import avatar from '@/assets/images/default.png'
import { getAppointmentInfoApi } from '@/apis/appointment'

const route = useRoute()
const router = useRouter()


// 时间段数据
const timeSlots = ref([])
// 预约信息（从路由参数或查询参数获取）
const appointmentInfo = ref({})
// 获取确认预约页的详细信息(医生信息和时间段信息)
const getAppointmentInfo = async () => {
  const params = {
    doctorId: route.query.doctorId,
    workDate: route.query.workDate,
    period: route.query.period
  }
  const res = await getAppointmentInfoApi(params);
  appointmentInfo.value = {...res.data.doctorDetail,...params}
  timeSlots.value = res.data.timeSlotList;
}
onMounted(() => getAppointmentInfo())


// 选中的时间段
const selectedTimeSlot = ref(null)

// 控制状态
const showConfirmDialog = ref(false)
const isSubmitting = ref(false)
const showResult = ref(false)
const appointmentResult = ref({
  success: false,
  message: '',
  appointmentId: ''
})


// 选择时间段
const selectTimeSlot = (slot) => {
  if (slot.status == 1) return
  selectedTimeSlot.value = slot
}

// 显示确认对话框
const showConfirm = () => {
  if (!selectedTimeSlot.value) {
    ElMessage.warning('请先选择预约时间段')
    return
  }
  showConfirmDialog.value = true
}

// 确认预约(向后端发送预约请求)
const confirmAppointment = async () => {
  isSubmitting.value = true
  
  try {
    const data = {
      doctorId: appointmentInfo.value.id,
      workDate: appointmentInfo.value.workDate,
      period: appointmentInfo.value.period,
      timeSlotId: selectedTimeSlot.value.id,
    }
    
    const res = await createAppointmentApi(data)
    if (res.code == 1){
      appointmentResult.value = {
        success: true,
        message: '预约成功！请按时就诊',
        appointmentId: res.data?.id || ''
      }
    }
    
  } catch (error) {
    appointmentResult.value = {
      success: false,
      message: error.message || '预约失败，请稍后重试',
      appointmentId: ''
    }
  } finally {
    isSubmitting.value = false
    showConfirmDialog.value = false
    showResult.value = true
  }
}

// 返回首页
const goHome = () => {
  router.push('/')
}

// 查看我的预约
const goToMyAppointments = () => {
  router.push('/myAppointment')
}

// 重新预约
const retryAppointment = () => {
  showResult.value = false
  selectedTimeSlot.value = null
  appointmentResult.value = {
    success: false,
    message: '',
    appointmentId: ''
  }
}
</script>

<template>
  <div class="appointment-confirm-container">
    <!-- 预约信息展示 -->
    <div class="appointment-info-section" v-if="!showResult">
      <div class="page-header">
        <h1>选择预约时间</h1>
        <p>请选择具体的预约时间段，确认无误后点击确认预约</p>
      </div>
      
      <div class="appointment-card">
        <div class="doctor-info">
          <el-avatar class="doctor-avatar" :size="80">
            <img :src="appointmentInfo.avatar || avatar" alt="医生头像" />
          </el-avatar>
          <div class="doctor-details">
            <h2>{{ appointmentInfo.name }}</h2>
            <p class="title">{{ appointmentInfo.title }}</p>
            <p class="department">{{ appointmentInfo.departmentName }}</p>
          </div>
        </div>
        
        <el-divider />
        
        <div class="appointment-details">
          <div class="detail-row">
            <el-icon class="detail-icon"><School /></el-icon>
            <span class="detail-label">就诊地点：</span>
            <span class="detail-value">{{ appointmentInfo.campus }}</span>
          </div>
          
          <div class="detail-row" v-if="appointmentInfo.location">
            <el-icon class="detail-icon"><Location /></el-icon>
            <span class="detail-label">诊室位置：</span>
            <span class="detail-value">{{ appointmentInfo.location }}</span>
          </div>
          
          <div class="detail-row">
            <el-icon class="detail-icon"><Calendar /></el-icon>
            <span class="detail-label">就诊日期：</span>
            <span class="detail-value">{{ appointmentInfo.workDate }}</span>
          </div>
          
          <div class="detail-row">
            <el-icon class="detail-icon"><Clock /></el-icon>
            <span class="detail-label">就诊时间：</span>
            <span class="detail-value">{{ route.query.period == 1 ? '上午' : '下午' }}</span>
          </div>
          
          <div class="detail-row" v-if="appointmentInfo.fee">
            <el-icon class="detail-icon"><Money /></el-icon>
            <span class="detail-label">诊疗费用：</span>
            <span class="detail-value price">¥{{ appointmentInfo.fee.toFixed(2) }}</span>
          </div>
        </div>
        
        <el-divider />
        
        <!-- 时间段选择 -->
        <div class="time-slot-section">
          <h3 class="section-title">
            <el-icon><Clock /></el-icon>
            选择预约时间段
          </h3>
          <p class="section-desc">请选择您希望的就诊时间段（灰色表示已被预约）</p>
          
          <div class="time-slots-grid">
            <div 
              v-for="slot in timeSlots" 
              :key="slot.id"
              class="time-slot-item"
              :class="{
                'selected': selectedTimeSlot?.id === slot.id,
                'unavailable': slot.status==1,
                'available': slot.status!=1
              }"
              @click="selectTimeSlot(slot)"
            >
              <div class="slot-time">{{ slot?.startTime.slice(0,5) }}</div>
              <div class="slot-number">{{ slot.numberId }}号</div>
              <div class="slot-status" v-if="slot.status==1">已约</div>
            </div>
          </div>
          
          <div class="selected-info" v-if="selectedTimeSlot">
            <p>已选择时间段：<span class="selected-time">{{ selectedTimeSlot.startTime }} {{ selectedTimeSlot.numberId }}号</span></p>
          </div>
        </div>
        
        <div class="action-section">
          <el-button size="large" @click="router.back()">返回修改</el-button>
          <el-button type="primary" size="large" @click="showConfirm" :disabled="!selectedTimeSlot">
            确认预约
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 预约结果展示 -->
    <div class="result-section" v-if="showResult">
      <div class="result-card">
        <div class="result-icon" :class="{ success: appointmentResult.success, error: !appointmentResult.success }">
          <el-icon v-if="appointmentResult.success" class="success-icon"><Check /></el-icon>
          <el-icon v-else class="error-icon"><Close /></el-icon>
        </div>
        
        <h2 class="result-title">{{ appointmentResult.success ? '预约成功' : '预约失败' }}</h2>
        <p class="result-message">{{ appointmentResult.message }}</p>
        
        <div class="result-info" v-if="appointmentResult.success">
          <p v-if="appointmentResult.appointmentId">预约单号：<span class="appointment-id">{{ appointmentResult.appointmentId }}</span></p>
          <p v-if="selectedTimeSlot">预约时间：<span class="appointment-time">{{ appointmentInfo.workDate }} {{ selectedTimeSlot.time }} {{ selectedTimeSlot.number }}</span></p>
        </div>
        
        <div class="result-actions">
          <el-button v-if="!appointmentResult.success" @click="retryAppointment">重新预约</el-button>
          <el-button v-if="appointmentResult.success" @click="goToMyAppointments">查看我的预约</el-button>
          <el-button type="primary" @click="goHome">返回首页</el-button>
        </div>
      </div>
    </div>
    
    <!-- 确认对话框 -->
    <el-dialog v-model="showConfirmDialog" title="确认预约" width="400px" center :close-on-click-modal="false">
      <div class="confirm-content">
        <el-icon class="confirm-icon"><User /></el-icon>
        <p>确定要预约 <strong>{{ appointmentInfo.name }}</strong> 医生吗？</p>
        <p class="confirm-time">{{ appointmentInfo.workDate }} {{ appointmentInfo.period === 1 ? '上午' : '下午' }}</p>
        <p class="confirm-slot" v-if="selectedTimeSlot">时间段：{{ selectedTimeSlot.startTime.slice(0,5) }} {{ selectedTimeSlot.number }}号</p>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showConfirmDialog = false" :disabled="isSubmitting">取消</el-button>
          <el-button type="primary" @click="confirmAppointment" :loading="isSubmitting">
            {{ isSubmitting ? '预约中...' : '确认预约' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.appointment-confirm-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.appointment-info-section {
  max-width: 800px;
  margin: 0 auto;
  
  .page-header {
    text-align: center;
    margin-bottom: 30px;
    
    h1 {
      font-size: 28px;
      color: #262626;
      margin-bottom: 10px;
      font-weight: 600;
    }
    
    p {
      color: #666;
      font-size: 16px;
    }
  }
  
  .appointment-card {
    background: white;
    border-radius: 12px;
    padding: 30px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    
    .doctor-info {
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      
      .doctor-avatar {
        margin-right: 20px;
        border: 3px solid #f0f0f0;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
      
      .doctor-details {
        h2 {
          margin: 0 0 8px 0;
          color: #262626;
          font-size: 24px;
          font-weight: 600;
        }
        
        .title {
          margin: 0 0 5px 0;
          color: #1890ff;
          font-weight: 500;
          font-size: 16px;
        }
        
        .department {
          margin: 0;
          color: #666;
          font-size: 14px;
        }
      }
    }
    
    .appointment-details {
      margin: 20px 0;
      
      .detail-row {
        display: flex;
        align-items: center;
        margin-bottom: 16px;
        
        .detail-icon {
          color: #1890ff;
          margin-right: 12px;
          font-size: 18px;
        }
        
        .detail-label {
          width: 100px;
          color: #666;
          font-weight: 500;
        }
        
        .detail-value {
          color: #262626;
          font-weight: 500;
          
          &.price {
            color: #ff6b35;
            font-size: 18px;
            font-weight: 600;
          }
        }
      }
    }
    
    .time-slot-section {
      margin: 30px 0;
      
      .section-title {
        display: flex;
        align-items: center;
        font-size: 18px;
        color: #262626;
        margin-bottom: 8px;
        font-weight: 600;
        
        .el-icon {
          margin-right: 8px;
          color: #1890ff;
        }
      }
      
      .section-desc {
        color: #666;
        font-size: 14px;
        margin-bottom: 20px;
      }
      
      .time-slots-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
        gap: 12px;
        margin-bottom: 20px;
      }
      
      .time-slot-item {
        position: relative;
        padding: 12px 8px;
        border: 2px solid #e8e8e8;
        border-radius: 8px;
        text-align: center;
        cursor: pointer;
        transition: all 0.3s ease;
        background: white;
        
        .slot-time {
          font-size: 14px;
          font-weight: 600;
          color: #262626;
          margin-bottom: 4px;
        }
        
        .slot-number {
          font-size: 12px;
          color: #666;
        }
        
        .slot-status {
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          background: rgba(0, 0, 0, 0.6);
          color: white;
          padding: 2px 6px;
          border-radius: 4px;
          font-size: 10px;
        }
        
        &.available {
          &:hover {
            border-color: #1890ff;
            box-shadow: 0 2px 8px rgba(24, 144, 255, 0.2);
          }
        }
        
        &.selected {
          border-color: #1890ff;
          background: linear-gradient(135deg, #1890ff, #40a9ff);
          color: white;
          
          .slot-time,
          .slot-number {
            color: white;
          }
        }
        
        &.unavailable {
          background: #f5f5f5;
          border-color: #d9d9d9;
          cursor: not-allowed;
          
          .slot-time,
          .slot-number {
            color: #bfbfbf;
          }
        }
      }
      
      .selected-info {
        background: #f0f9ff;
        border: 1px solid #bae7ff;
        border-radius: 6px;
        padding: 12px 16px;
        text-align: center;
        
        p {
          margin: 0;
          color: #1890ff;
          font-size: 14px;
        }
        
        .selected-time {
          font-weight: 600;
          color: #0050b3;
        }
      }
    }
    
    .action-section {
      display: flex;
      justify-content: center;
      gap: 20px;
      margin-top: 30px;
      
      .el-button {
        padding: 12px 30px;
        font-size: 16px;
        border-radius: 8px;
        
        &:disabled {
          opacity: 0.6;
        }
      }
    }
  }
}

.result-section {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  
  .result-card {
    background: white;
    border-radius: 16px;
    padding: 50px;
    text-align: center;
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
    max-width: 500px;
    
    .result-icon {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 20px;
      
      &.success {
        background: linear-gradient(135deg, #52c41a, #73d13d);
        
        .success-icon {
          color: white;
          font-size: 40px;
        }
      }
      
      &.error {
        background: linear-gradient(135deg, #ff4d4f, #ff7875);
        
        .error-icon {
          color: white;
          font-size: 40px;
        }
      }
    }
    
    .result-title {
      font-size: 24px;
      margin-bottom: 10px;
      color: #262626;
      font-weight: 600;
    }
    
    .result-message {
      font-size: 16px;
      color: #666;
      margin-bottom: 20px;
      line-height: 1.5;
    }
    
    .result-info {
      background: #f8f9fa;
      padding: 15px;
      border-radius: 8px;
      margin-bottom: 30px;
      
      p {
        margin: 5px 0;
        font-size: 14px;
        color: #666;
      }
      
      .appointment-id,
      .appointment-time {
        color: #1890ff;
        font-weight: 600;
      }
    }
    
    .result-actions {
      display: flex;
      justify-content: center;
      gap: 15px;
      
      .el-button {
        padding: 10px 25px;
        border-radius: 8px;
      }
    }
  }
}

.confirm-content {
  text-align: center;
  padding: 20px 0;
  
  .confirm-icon {
    font-size: 48px;
    color: #1890ff;
    margin-bottom: 15px;
  }
  
  p {
    margin-bottom: 10px;
    font-size: 16px;
    color: #262626;
    
    strong {
      color: #1890ff;
    }
  }
  
  .confirm-time,
  .confirm-slot {
    color: #666;
    font-size: 14px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 15px;
}

// 响应式设计
@media (max-width: 768px) {
  .appointment-confirm-container {
    padding: 10px;
  }
  
  .appointment-card {
    padding: 20px !important;
  }
  
  .doctor-info {
    flex-direction: column;
    text-align: center;
    
    .doctor-avatar {
      margin-right: 0 !important;
      margin-bottom: 15px;
    }
  }
  
  .time-slots-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr)) !important;
    gap: 8px !important;
  }
  
  .time-slot-item {
    padding: 10px 6px !important;
  }
  
  .action-section {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
  
  .result-card {
    padding: 30px 20px !important;
    margin: 0 10px;
  }
  
  .result-actions {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
}
</style>