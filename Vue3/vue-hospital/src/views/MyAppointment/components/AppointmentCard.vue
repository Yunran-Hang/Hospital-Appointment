<script setup>
import { ref, onMounted } from 'vue';
import { computed } from 'vue';
import { Calendar, Clock, Location, Document } from '@element-plus/icons-vue'
import 'element-plus/theme-chalk/el-message.css'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  activeTab: {
    type: String,
    default: ''
  },
  appointments: {
    type: Array,
    default: () => []
  }
})

// 修改计算属性：根据activeTab显示不同的预约记录
const filteredAppointments = computed(() => {
  if (props.activeTab === 'current') {
    // 当前预约：显示待就诊的预约
    return props.appointments.filter(apt => apt.appointmentStatus === 1)
  } else if (props.activeTab === 'history') {
    // 历史记录：显示已完成和已取消的预约
    return props.appointments.filter(apt => apt.appointmentStatus === 2 || apt.appointmentStatus === 3)
  }
  // 默认显示所有预约
  return props.appointments
})


// 方法
const getStatusType = (status) => {
  const statusMap = {
    1: 'warning',
    3: 'success',
    2: 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    1: '待就诊',
    3: '已完成',
    2: '已取消'
  }
  return statusMap[status] || '未知状态'
}

const cancelAppointment = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？(不可撤回)', '确认取消', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用API取消预约
    const appointment = props.appointments.value.find(apt => apt.appointmentId === id)
    if (appointment) {
      appointment.status = 'cancelled'
    }

    ElMessage.success('预约已取消')
  } catch {
    ElMessage.info('已取消操作')
  }
}

const viewDetails = (id) => {
  ElMessage.info('查看详情功能开发中...')
}
</script>

<template>
  <!-- 修改：使用filteredAppointments而不是currentAppointments -->
  <div v-for="appointment in filteredAppointments" 
       :key="appointment.appointmentId" 
       :data-status="appointment.appointmentStatus"
       class="appointment-card">
    <!-- 卡片头部 -->
    <div class="card-header">
      <!-- 医生信息 -->
      <div class="doctor-info">
        <img :src="appointment.doctorAvatar" :alt="appointment.doctorName" class="doctor-avatar">
        <div class="doctor-details">
          <h3>{{ appointment.doctorName }}</h3>
          <p>{{ appointment.departmentName }} · {{ appointment.doctorTitle }}</p>
        </div>
      </div>
      <!-- 状态显示 -->
      <el-tag :type="getStatusType(appointment.appointmentStatus)" class="status-tag">
        {{ getStatusText(appointment.appointmentStatus) }}
      </el-tag>
    </div>
    <!-- 具体内容 -->
    <div class="card-content">
      <div class="appointment-info">
        <div class="info-item">
          <el-icon><Calendar /></el-icon>
          <span>{{ appointment.appointmentDate }}</span>
        </div>
        <div class="info-item">
          <el-icon><Clock /></el-icon>
          <span>{{ appointment.startTime }}-{{ appointment.endTime }}</span>
        </div>
        <div class="info-item">
          <el-icon><Location /></el-icon>
          <span>{{ appointment.doctorLocation }}</span>
        </div>
        <!-- 这里是当前预约状态才有的 -->
        <div v-if="activeTab == 'current'" class="info-item">
          <el-icon><Document /></el-icon>
          <span>预约号：{{ appointment.appointmentNo }}</span>
        </div>
      </div>
    </div>
    <!-- 卡片操作按钮 -->
    <div class="card-actions">
      <!-- 根据activeTab和状态显示不同的按钮 -->
      <template v-if="props.activeTab === 'current' && appointment.status === 1">
        <el-button type="danger" size="small" @click="cancelAppointment(appointment.id)">
          取消预约
        </el-button>
      </template>
      
      <template v-if="props.activeTab === 'history' && appointment.status === 3">
        <el-button type="primary" size="small">
          再次预约
        </el-button>
      </template>
      
      <el-button size="small" @click="viewDetails(appointment.id)">
        查看详情
      </el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/var.scss' as hospital-vars;

.appointment-card {
  background: linear-gradient(135deg, hospital-vars.$card-bg 0%, rgba(hospital-vars.$primary-color, 0.01) 100%);
  border: 1px solid rgba(hospital-vars.$primary-color, 0.1);
  border-radius: 12px; // 减小圆角
  padding: 20px; // 减小内边距
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04); // 减小阴影
  position: relative;
  overflow: hidden;
  margin-bottom: 16px; // 减小卡片间距

  // 添加装饰性渐变边框
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px; // 减小装饰条高度
    background: linear-gradient(90deg, hospital-vars.$primary-color, hospital-vars.$success-color, hospital-vars.$primary-light);
    border-radius: 12px 12px 0 0;
  }

  &:hover {
    transform: translateY(-2px) scale(1.005); // 减小悬停效果
    box-shadow: 0 8px 24px rgba(hospital-vars.$primary-color, 0.12);
    border-color: hospital-vars.$primary-color;
    
    .doctor-avatar {
      transform: scale(1.03);
      box-shadow: 0 4px 12px rgba(hospital-vars.$primary-color, 0.2);
    }
    
    .status-tag {
      transform: scale(1.05);
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px; // 减小间距
    padding-bottom: 12px; // 减小内边距
    border-bottom: 1px solid rgba(hospital-vars.$primary-color, 0.08);

    .doctor-info {
      display: flex;
      align-items: center;
      gap: 12px; // 减小间距

      .doctor-avatar {
        width: 50px; // 减小头像尺寸
        height: 50px;
        border-radius: 50%;
        object-fit: cover;
        border: 3px solid hospital-vars.$primary-light;
        transition: all 0.3s ease;
        box-shadow: 0 2px 8px rgba(hospital-vars.$primary-color, 0.15);
      }

      .doctor-details {
        h3 {
          font-size: 16px; // 减小字体
          color: hospital-vars.$text-primary;
          margin: 0 0 4px 0;
          font-weight: 700;
          letter-spacing: 0.3px;
        }

        p {
          color: hospital-vars.$text-secondary;
          margin: 0;
          font-size: 13px; // 减小字体
          font-weight: 500;
          opacity: 0.8;
        }
      }
    }

    .status-tag {
      font-weight: 700; // 增加字体粗细
      padding: 12px 20px; // 增大内边距
      border-radius: 25px; // 增大圆角
      font-size: 16px; // 增大字体
      letter-spacing: 0.8px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); // 增强阴影
      text-transform: uppercase; // 大写字母
    }
  }

  .card-content {
    margin-bottom: 16px; // 减小间距

    .appointment-info {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); // 减小最小宽度
      gap: 12px; // 减小间距
      padding: 12px; // 减小内边距
      background: rgba(hospital-vars.$primary-color, 0.02);
      border-radius: 8px; // 减小圆角
      border: 1px solid rgba(hospital-vars.$primary-color, 0.05);

      .info-item {
        display: flex;
        align-items: center;
        gap: 8px; // 减小间距
        color: hospital-vars.$text-secondary;
        font-size: 13px; // 减小字体
        font-weight: 500;
        padding: 4px 0; // 减小内边距
        transition: all 0.3s ease;

        &:hover {
          color: hospital-vars.$text-primary;
          transform: translateX(2px);
        }

        .el-icon {
          color: hospital-vars.$primary-color;
          font-size: 16px; // 减小图标
          background: rgba(hospital-vars.$primary-color, 0.1);
          padding: 4px; // 减小内边距
          border-radius: 6px;
          transition: all 0.3s ease;
        }

        span {
          font-weight: 600;
        }
      }
    }
  }

  .card-actions {
    display: flex;
    gap: 16px; // 增大按钮间距
    justify-content: flex-end;
    padding-top: 12px; // 减小内边距
    border-top: 1px solid rgba(hospital-vars.$primary-color, 0.08);

    .el-button {
      border-radius: 12px; // 增大圆角
      font-weight: 700; // 增加字体粗细
      padding: 14px 28px; // 增大内边距
      font-size: 16px; // 增大字体
      letter-spacing: 0.8px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12); // 增强阴影
      min-width: 120px; // 设置最小宽度
      text-transform: uppercase; // 大写字母

      &:hover {
        transform: translateY(-3px); // 增强悬停效果
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
      }

      &.el-button--primary {
        background: linear-gradient(135deg, hospital-vars.$primary-color, hospital-vars.$primary-light);
        border: none;
        
        &:hover {
          background: linear-gradient(135deg, hospital-vars.$primary-light, hospital-vars.$success-color);
        }
      }

      &.el-button--danger {
        background: linear-gradient(135deg, hospital-vars.$danger-color, #ff6b6b);
        border: none;
        
        &:hover {
          background: linear-gradient(135deg, #ff6b6b, #ff5252);
        }
      }

      &.el-button--default {
        background: linear-gradient(135deg, #f8f9fa, #e9ecef);
        color: hospital-vars.$text-primary;
        border: 2px solid rgba(hospital-vars.$primary-color, 0.3); // 增强边框
        
        &:hover {
          background: linear-gradient(135deg, hospital-vars.$primary-color, hospital-vars.$primary-light);
          color: white;
          border-color: hospital-vars.$primary-color;
        }
      }
    }
  }
}

// 响应式设计优化
@media (max-width: 768px) {
  .appointment-card {
    padding: 16px; // 移动端进一步减小内边距
    border-radius: 10px;

    .card-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
      margin-bottom: 12px;

      .doctor-info {
        width: 100%;
        
        .doctor-avatar {
          width: 45px; // 移动端更小的头像
          height: 45px;
        }
      }

      .status-tag {
        align-self: flex-end;
        padding: 10px 16px; // 移动端稍小的状态标签
        font-size: 14px;
      }
    }

    .card-content {
      .appointment-info {
        grid-template-columns: 1fr;
        gap: 10px;
        padding: 10px;

        .info-item {
          padding: 3px 0;
          font-size: 12px;
        }
      }
    }

    .card-actions {
      justify-content: center;
      flex-wrap: wrap;
      gap: 12px;

      .el-button {
        flex: 1;
        min-width: 100px;
        padding: 12px 20px; // 移动端稍小的按钮
        font-size: 14px;
      }
    }
  }
}

@media (max-width: 480px) {
  .appointment-card {
    padding: 12px;
    margin: 0 4px;

    .doctor-info {
      flex-direction: column;
      text-align: center;
      gap: 8px;
      
      .doctor-avatar {
        width: 40px;
        height: 40px;
        align-self: center;
      }
    }

    .card-actions {
      .el-button {
        flex: 1;
        min-width: 80px;
        font-size: 12px;
        padding: 10px 16px;
      }
    }
  }
}

// 添加加载动画
@keyframes cardSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px); // 减小动画幅度
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.appointment-card {
  animation: cardSlideIn 0.4s ease-out; // 减短动画时间
}

// 为不同状态的卡片添加特殊样式
.appointment-card {
  &[data-status="1"] { // 待就诊
    border-left: 3px solid hospital-vars.$warning-color;
  }
  
  &[data-status="2"] { // 已取消
    border-left: 3px solid hospital-vars.$danger-color;
    opacity: 0.8;
  }
  
  &[data-status="3"] { // 已完成
    border-left: 3px solid hospital-vars.$success-color;
  }
}
</style>