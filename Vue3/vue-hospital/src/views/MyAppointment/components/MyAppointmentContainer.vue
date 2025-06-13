<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'
import AppointmentCard from './AppointmentCard.vue'
import { getMyAppointmentApi } from '@/apis/appointment'
import { useUserStore } from '@/stores/userStore'

// 响应式数据
// 搜索关键字
const searchKeyword = ref('')
// 预约状态
const statusFilter = ref()
// 日期范围
const dateRange = ref([])
// 标签页状态
const activeTab = ref('current')

// 预约数据
const appointments = ref([])
const appointmentsFilter = ref([])

// 筛选函数
const filterAppointments = () => {
  let filtered = [...appointments.value]
  
  // 1. 根据搜索关键字筛选（医生姓名或科室）
  if (searchKeyword.value && searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.trim().toLowerCase()
    filtered = filtered.filter(appointment => {
      const doctorName = appointment.doctorName?.toLowerCase() || ''
      const department = appointment.departmentName?.toLowerCase() || ''
      return doctorName.includes(keyword) || department.includes(keyword)
    })
  }
  
  // 2. 根据预约状态筛选
  if (statusFilter.value && statusFilter.value !== '') {
    filtered = filtered.filter(appointment => {
      return appointment.appointmentStatus === parseInt(statusFilter.value)
    })
  }
  
  // 3. 根据日期范围筛选
  if (dateRange.value && dateRange.value.length === 2) {
    const [startDate, endDate] = dateRange.value
    filtered = filtered.filter(appointment => {
      if (!appointment.appointmentDate) return false
      
      const appointmentDate = new Date(appointment.appointmentDate)
      const start = new Date(startDate)
      const end = new Date(endDate)
      
      // 设置时间为当天的开始和结束
      start.setHours(0, 0, 0, 0)
      end.setHours(23, 59, 59, 999)
      
      return appointmentDate >= start && appointmentDate <= end
    })
  }
  
  // 更新筛选后的数据
  appointmentsFilter.value = filtered
}

// 监听筛选条件变化
watch([searchKeyword, statusFilter, dateRange], () => {
  filterAppointments()
}, { immediate: true })

// 监听原始数据变化，重新筛选
watch(appointments, () => {
  filterAppointments()
}, { deep: true })

const userStore = useUserStore()
const getMyAppointment = async () => {
  const res = await getMyAppointmentApi(userStore.userInfo.id)
  
  if (res.code == 1) {
    appointments.value = res.data
    // 初始化时也要执行一次筛选
    filterAppointments()
  }
}

onMounted(() => getMyAppointment())
</script>

<template>
  <!-- 筛选区域 -->
  <div class="filter-section">
    <div class="filter-row">
      <el-input v-model="searchKeyword" placeholder="搜索医生姓名或科室" class="search-input" clearable>
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <!-- 预约状态 -->
      <el-select v-model="statusFilter" placeholder="预约状态" class="status-select">
        <el-option label="全部状态" value=""></el-option>
        <el-option label="待就诊" value="1"></el-option>
        <el-option label="已完成" value="3"></el-option>
        <el-option label="已取消" value="2"></el-option>
      </el-select>
      <!-- 日期筛选 -->
      <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
        end-placeholder="结束日期" class="date-picker" />
    </div>
  </div>

  <!-- 预约记录标签页 -->
  <el-tabs v-model="activeTab" class="appointment-tabs">
    <el-tab-pane label="当前预约" name="current">
      <div class="appointment-list">
        <!-- 使用筛选后的数据 -->
        <AppointmentCard :activeTab="activeTab" :appointments="appointmentsFilter" />
      </div>
    </el-tab-pane>
    <!-- 历史记录 -->

    
    <el-tab-pane label="历史记录" name="history">
      <div class="appointment-list">
        <!-- 使用筛选后的数据 -->
        <AppointmentCard :activeTab="activeTab" :appointments="appointmentsFilter" />
      </div>
    </el-tab-pane>
  </el-tabs>
</template>

<style scoped lang="scss">
// 使用别名导入避免变量冲突
@use '@/styles/var.scss' as hospital-vars;

.filter-section {
  background-color: hospital-vars.$card-bg;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

  .filter-row {
    display: flex;
    gap: 16px;
    align-items: center;
    flex-wrap: wrap;

    .search-input {
      flex: 1;
      min-width: 200px;
    }

    .status-select {
      width: 150px;
    }

    .date-picker {
      width: 280px;
    }
  }
}

.appointment-tabs {
  background-color: hospital-vars.$card-bg;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.appointment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}



// 响应式设计
@media (max-width: 768px) {
  .my-appointment-container {
    padding: 16px;
  }

  .filter-section {
    .filter-row {
      flex-direction: column;
      align-items: stretch;

      .search-input,
      .status-select,
      .date-picker {
        width: 100%;
      }
    }
  }


}
</style>