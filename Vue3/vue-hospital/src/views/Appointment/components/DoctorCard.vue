<script setup>
import { Sunrise } from '@element-plus/icons-vue'
import avatar from '@/assets/images/default.png'
import { getDoctorDetailApi } from '@/apis/doctor'
import { computed, ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getDoctorScheduleApi } from '@/apis/schedule'

const router = useRouter();

const props = defineProps({
  doctors: {
    type: Object,
    default: () => {}
  }
})


// 控制医生详情弹窗显示
const showDoctorDialog = ref(false)
const doctorDetails = ref({})
// 医生排班数据
const doctorSchedule = ref([])


// 点击医生姓名显示详情
const showDoctorDetails = async (id) => {
    let res = await getDoctorDetailApi(id)
    doctorDetails.value = res.data
    // 生成排班数据
    res = await getDoctorScheduleApi(id)
    temp()
    doctorSchedule.value = res.data
    showDoctorDialog.value = true
}

const temp = () => {
  // 将数字转换为对应的星期名称
  const days = ['星期天', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  doctorSchedule.value.forEach((item) => {
    item.dayOfWeek = days[new Date(item.workDate).getDay()];
  });
  console.log(doctorSchedule.value);
}

// 点击排班跳转预约
const goToAppointment = (schedule) => {
  router.push(`/appointmentConfirm?doctorId=${props.doctors.doctorId}&workDate=${schedule.workDate}&period=${schedule.period}`)
  showDoctorDialog.value = false
}

// 动态将长文本内容计算成短文本
const shortText = computed(() => {
    const maxLength = 17;
    if (props.doctors.campus.length > maxLength){
        return props.doctors.campus.slice(0,maxLength-3)+ '...';
    }
    return props.doctors.campus
})
</script>

<template>
  <div class="card-container">
    <el-card v-if="props.doctors" shadow="hover" class="box-card">
        <el-row style="height: 100%;width: 100%;">
            <!-- 头像以及医生信息 -->
            <el-col :span="8">
                <div style="display: flex; align-items: center;">
                    <el-avatar class="doctor-image">
                        <!--设置图片懒加载-->
                        <img style="object-fit: cover;" v-img-lazy="props.doctors.avatar?props.doctors.avatar:avatar" />
                    </el-avatar>
                    <div class="doctor-info">
                        <p>
                            <strong 
                              class="doctor-name" 
                              @click="showDoctorDetails(props.doctors.doctorId)"
                              style="font-size: 16px; cursor: pointer; color: #1890ff;"
                            >
                              {{ props.doctors.name }}
                            </strong>
                            <span style="margin-left: 10px;font-size: 13px;">{{ props.doctors.title }}</span>
                        </p>
                        <p>
                            <!-- 使用 el-tooltip 包裹需要处理的文本 -->
                            <el-tooltip :content="props.doctors.campus" placement="top">
                                <div class="ellipsis-text">{{ shortText }}</div>
                            </el-tooltip>
                        </p>
                        <p>
                            累计预约量<span class="totalSchedule" >{{ props.doctors.totalSchedule }}</span>
                        </p>
                    </div>
                </div>
            </el-col>
            <!-- 挂号信息 -->
            <el-col :span="13">
                <el-row style="display: flex;align-items: center;height: 100%;">
                    <el-col :span="5">{{ props.doctors.workDate }}</el-col>
                    <el-col :span="5" style="display: flex;padding: 0" :style="{ color: props.doctors.period == 1 ? '#409EFF' : '#FFA500' }">
                        <el-icon  :size="17" style="margin-top: auto;"><Sunrise :style="props.doctors.period == 1 ?none:{ transform: 'scaleY(-1)' }" /></el-icon>
                        <span class="time">{{ props.doctors.period == 1 ? '上午' : '下午' }}</span>
                    </el-col>
                    <el-col :span="4">{{ props.doctors.departmentName }}</el-col>
                    <el-col :span="6">
                        <p>诊疗费: <span style="color: #FFA500;">¥{{ props.doctors.fee }}</span></p>
                        <p style="color: #888888;">(医院现场支付)</p>
                    </el-col>
                    <el-col :span="4">
                        <div>
                            <p style="margin-bottom: 10px;"><el-tag type="success">余 {{ props.doctors.leftAppointment }}</el-tag></p>
                            <p><el-tag type="primary">总 {{ props.doctors.maxAppointment }}</el-tag></p>
                        </div>
                    </el-col>
                </el-row>
                
            </el-col>
            <el-col :span="3">
                <div style="height: 100%;display: flex;align-items: center;justify-content: center;">
                    <el-button style="width: 100px;" type="primary" @click="router.push(`/appointmentConfirm?doctorId=${props.doctors.doctorId}&&workDate=${props.doctors.workDate}&&period=${props.doctors.period}`)">预约</el-button>
                </div>
            </el-col>
        </el-row>
    </el-card>

    <!-- 医生详情弹窗 -->
    <el-dialog 
      v-model="showDoctorDialog" 
      :title="`${doctorDetails.name} 医生详情`" 
      width="800px" 
      center
    >
      <div class="doctor-detail">
        <div class="doctor-header">
          <el-avatar class="detail-avatar" :size="80">
            <img style="object-fit: cover;" :src="doctorDetails.avatar || avatar" />
          </el-avatar>
          <div class="doctor-basic-info">
            <h3>{{ doctorDetails.name }}</h3>
            <p class="title">{{ doctorDetails.title }}</p>
            <p class="department">{{ doctorDetails.departmentName }}</p>
          </div>
        </div>
        
        <el-divider />
        
        <div class="doctor-details">
          <div class="detail-item">
            <span class="label">工作地点：</span>
            <span class="value">{{ doctorDetails.campus }}</span>
          </div>
          <div class="detail-item" v-if="doctorDetails.location">
            <span class="label">诊室位置：</span>
            <span class="value">{{ doctorDetails.location }}</span>
          </div>
          <div class="detail-item">
            <span class="label">出诊时间：</span>
            <span class="value">{{ props.doctors?.workDate }} {{ props.doctors?.period == 1 ? '上午' : '下午' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">诊疗费用：</span>
            <span class="value price">¥{{ doctorDetails.fee }}</span>
          </div>
          <div class="detail-item">
            <span class="label">累计预约：</span>
            <span class="value">{{ doctorDetails.totalSchedule }} 次</span>
          </div>
          <div class="detail-item">
            <span class="label">剩余号源：</span>
            <span class="value">
              <el-tag type="success" size="small">余 {{ props.doctors?.leftAppointment }}</el-tag>
              <el-tag type="primary" size="small" style="margin-left: 8px;">总 {{ props.doctors?.maxAppointment }}</el-tag>
            </span>
          </div>
          
          <!-- 专长信息 -->
          <div class="detail-item" v-if="doctorDetails.specialty">
            <span class="label">专业专长：</span>
            <span class="value specialty">{{ doctorDetails.specialty }}</span>
          </div>
          
          <!-- 医生描述 -->
          <div class="detail-item description-item" v-if="doctorDetails.description">
            <span class="label">医生简介：</span>
            <div class="value description">
              {{ doctorDetails.description }}
            </div>
          </div>
        </div>
        <!-- 分割线 -->
        <el-divider />
        
        <!-- 未来两周排班情况 -->
        <div class="schedule-section">
          <h4 class="schedule-title">
            <el-icon style="margin-right: 8px;"><Calendar /></el-icon>
            未来两周排班情况
          </h4>
          <p class="schedule-desc">点击可用时段即可预约</p>
          
          <div class="schedule-grid">
            <div 
              v-for="(item, index) in doctorSchedule" 
              :key="index"
              class="schedule-day"
              :class="{ 'no-schedule': item.schedules.length === 0  }"
            >
              <div class="day-header">
                <div class="day-name">{{ item.dayOfWeek }}</div>
                <div class="day-date">{{ item.workDate }}</div>
              </div>
              
              <div class="day-periods" v-if="item.schedules.length">
                <div 
                  v-for="schedule in item.schedules" 
                  :key="schedule.scheduleId"
                  class="period-item"
                  :class="{ 'available': schedule.leftAppointments > 0, 'full': schedule.leftAppointments === 0 }"
                  @click="schedule.leftAppointments > 0 && goToAppointment(schedule)"
                >
                  <div class="period-name">{{ schedule.period == 1 ? '上午' : '下午' }}</div>
                  <div class="period-count">
                    <span v-if="schedule.leftAppointments > 0" class="available-count">余{{ schedule.leftAppointments }}</span>
                    <span v-else class="full-text">已满</span>
                  </div>
                </div>
              </div>
              
              <div class="no-schedule-text" v-else>
                休息
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDoctorDialog = false">关闭</el-button>
          <el-button type="primary" @click="router.push(`/appointmentConfirm?doctorId=${props.doctors.doctorId}&&workDate=${props.doctors.workDate}&&period=${props.doctors.period}`)">立即预约</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.totalSchedule {
    color: $warning-color
}
.el-tag {
    width: 60px;
    height: 25px;
}
.card-container {
    width: 100%;

    .box-card {
        width: 1150px;
        height: 120px;

        .doctor-image {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            margin-right: 15px;
        }
        .doctor-info {
            p {
                margin-top: 5px;
            }
        }
    }
}

.time {
  margin-left: 10px;
}

.el-tag {
  margin-left: 10px;
}

// 医生姓名悬停效果
.doctor-name {
  transition: all 0.3s ease;
  
  &:hover {
    color: #40a9ff !important;
    text-decoration: underline;
  }
}

// 医生详情弹窗样式
.doctor-detail {
  .doctor-header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    
    .detail-avatar {
      margin-right: 20px;
    }
    
    .doctor-basic-info {
      h3 {
        margin: 0 0 8px 0;
        color: #262626;
        font-size: 20px;
      }
      
      .title {
        margin: 0 0 5px 0;
        color: #1890ff;
        font-weight: 500;
      }
      
      .department {
        margin: 0;
        color: #666;
        font-size: 14px;
      }
    }
  }
  
  .doctor-details {
    .detail-item {
      display: flex;
      align-items: flex-start;
      margin-bottom: 16px;
      
      .label {
        width: 80px;
        color: #666;
        font-weight: 500;
        flex-shrink: 0;
        margin-top: 2px;
      }
      
      .value {
        color: #262626;
        flex: 1;
        
        &.price {
          color: #FFA500;
          font-weight: 600;
          font-size: 16px;
        }
        
        &.specialty {
          color: #1890ff;
          font-weight: 500;
        }
        
        &.description {
          line-height: 1.6;
          color: #555;
          font-size: 14px;
          position: relative;
          padding-left: 12px;
          
          &::before {
            content: '';
            position: absolute;
            left: 0;
            top: 6px;
            width: 3px;
            height: 3px;
            background: #1890ff;
            border-radius: 50%;
          }
          
          &::after {
            content: '';
            position: absolute;
            left: 0;
            bottom: 6px;
            width: 3px;
            height: 3px;
            background: #52c41a;
            border-radius: 50%;
          }
        }
      }
    }
    
    .description-item {
      flex-direction: column;
      align-items: flex-start;
      
      .label {
        margin-bottom: 8px;
        width: auto;
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          bottom: -2px;
          left: 0;
          width: 20px;
          height: 2px;
          background: linear-gradient(90deg, #1890ff, #52c41a);
          border-radius: 1px;
        }
      }
    }
  }
  
  // 排班部分样式
  .schedule-section {
    margin-top: 20px;
    
    .schedule-title {
      display: flex;
      align-items: center;
      font-size: 16px;
      color: #262626;
      margin-bottom: 8px;
      font-weight: 600;
    }
    
    .schedule-desc {
      color: #666;
      font-size: 14px;
      margin-bottom: 16px;
    }
    
    .schedule-grid {
      display: grid;
      grid-template-columns: repeat(7, 1fr);
      gap: 8px;
      max-height: 300px;
      overflow-y: auto;
    }
    
    .schedule-day {
      border: 1px solid #e8e8e8;
      border-radius: 6px;
      padding: 8px;
      background: white;
      transition: all 0.3s ease;
      
      &.no-schedule {
        background: #f5f5f5;
        
        .day-header {
          .day-name,
          .day-date {
            color: #bfbfbf;
          }
        }
      }
      
      .day-header {
        text-align: center;
        margin-bottom: 8px;
        
        .day-name {
          font-size: 12px;
          color: #666;
          margin-bottom: 2px;
        }
        
        .day-date {
          font-size: 11px;
          color: #999;
        }
      }
      
      .day-periods {
        display: flex;
        flex-direction: column;
        gap: 4px;
      }
      
      .period-item {
        padding: 4px 6px;
        border-radius: 4px;
        text-align: center;
        cursor: pointer;
        transition: all 0.3s ease;
        
        .period-name {
          font-size: 11px;
          margin-bottom: 2px;
        }
        
        .period-count {
          font-size: 10px;
        }
        
        &.available {
          background: #e6f7ff;
          border: 1px solid #91d5ff;
          color: #1890ff;
          
          &:hover {
            background: #bae7ff;
            transform: translateY(-1px);
            box-shadow: 0 2px 4px rgba(24, 144, 255, 0.2);
          }
          
          .available-count {
            color: #52c41a;
            font-weight: 600;
          }
        }
        
        &.full {
          background: #f5f5f5;
          border: 1px solid #d9d9d9;
          color: #bfbfbf;
          cursor: not-allowed;
          
          .full-text {
            color: #ff4d4f;
          }
        }
      }
      
      .no-schedule-text {
        text-align: center;
        color: #bfbfbf;
        font-size: 12px;
        padding: 8px 0;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

// 响应式设计
@media (max-width: 768px) {
  .schedule-grid {
    grid-template-columns: repeat(4, 1fr) !important;
  }
}

@media (max-width: 480px) {
  .schedule-grid {
    grid-template-columns: repeat(3, 1fr) !important;
  }
}
</style>