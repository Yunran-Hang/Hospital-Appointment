<script setup>
import { ref, onMounted, computed,nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Calendar } from '@element-plus/icons-vue'
import {
  getScheduleListApi,
  addScheduleApi,
  updateScheduleApi,
  deleteScheduleApi,
  batchGenerateScheduleApi,
  getDoctorTimeSlotsApi,
} from '@/apis/schedule'
import { getDoctorListApi } from '@/apis/doctor'

// 响应式数据
const currentDate = ref(new Date())
const doctorList = ref([])
const scheduleData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const batchDialogVisible = ref(false)
const timeSlotsDialogVisible = ref(false)
const selectedSchedule = ref(null)
const timeSlotsList = ref([])

// 表单数据
const scheduleForm = ref({
  doctorId: '',
  workDate: '',
  period: 1, // 1上午 2下午
  maxAppointments: 0,
  startTime: '',
  endTime: '',
  slotDuration: 3 // 每个时间段3分钟
})

// 批量生成表单
const batchForm = ref({
  doctorIds: [],
  startDate: '',
  endDate: '',
  period: 1, // 改为单选
  startTime: '08:00',
  endTime: '11:00',
  timePeriodInterval: 3,
  maxAppointments: 0,
  workDays: [1, 2, 3, 4, 5]
})

// 时间段配置
const periodOptions = [
  { value: 1, label: '上午', startTime: '08:00', endTime: '11:00' },
  { value: 2, label: '下午', startTime: '13:30', endTime: '16:30' }
]

// 工作日选项
const weekDayOptions = [
  { value: 1, label: '周一' },
  { value: 2, label: '周二' },
  { value: 3, label: '周三' },
  { value: 4, label: '周四' },
  { value: 5, label: '周五' },
  { value: 6, label: '周六' },
  { value: 7, label: '周日' }
]

// 格式化日期，保证日期是YY-MM
const formattedDate = computed(() => {
  return currentDate.value.toLocaleDateString('sv-SE') // 'sv-SE' 格式为 YYYY-MM-DD
})

// 获取医生列表
const getDoctorList = async () => {
  const res = await getDoctorListApi()
  if (res.code === 1) {
    doctorList.value = res.data
  }
}

// 获取排班数据
const getScheduleData = async () => {
  try{
    loading.value = true
    const res = await getScheduleListApi({ workDate: formattedDate.value })
    if (res.code === 1) {
      scheduleData.value = res.data
    } 
  }finally {
    loading.value = false
  }
}

// 获取医生时间段
const getDoctorTimeSlots = async (scheduleId) => {
  // 先找到对应的排班信息
  selectedSchedule.value = scheduleData.value.find(item => item.scheduleId === scheduleId)
  
  const res = await getDoctorTimeSlotsApi(scheduleId)
  if (res.code === 1) {
    timeSlotsList.value = res.data
    timeSlotsDialogVisible.value = true
    // 在对话框显示后，滚动到顶部
    nextTick(() => {
      if (timeSlotsContainerRef.value) {
        timeSlotsContainerRef.value.scrollTop = 0
      }
    })
  }
}

// 添加/编辑排班
const handleSaveSchedule = async () => {
  // 表单验证
  if (!scheduleFormRef.value) return

  scheduleFormRef.value.validate( async (valid) =>{
    if (valid){
      try {
        let res
        if (selectedSchedule.value) {
          // 编辑
          const data = {
            id: selectedSchedule.value.scheduleId,
            doctorId: selectedSchedule.value.doctorId,
            workDate: scheduleForm.value.workDate,
            period: scheduleForm.value.period,
            maxAppointments: scheduleForm.value.maxAppointments,
            leftAppointments: scheduleForm.value.leftAppointments,
            timePeriodInterval: scheduleForm.value.timePeriodInterval,
            startTime: scheduleForm.value.startTime,
            endTime: scheduleForm.value.endTime
          }
          res = await updateScheduleApi(data)
        } else {
          // 新增
          res = await addScheduleApi(scheduleForm.value)
        }
        
        if (res.code === 1) {
          ElMessage.success(selectedSchedule.value ? '更新成功' : '添加成功')
          dialogVisible.value = false
          resetForm()
          getScheduleData()
        }
      } catch (error) {
        console.error('保存排班失败:', error)
        ElMessage.error('操作失败')
      }

    }
  })

  
}

// 删除排班
const handleDeleteSchedule = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个排班吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteScheduleApi(row.scheduleId)
    if (res.code === 1) {
      ElMessage.success('删除成功')
      getScheduleData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('服务异常')
    }else{
      ElMessage.info('用户取消删除')
    }
  }
}


// 计算最大排班数量
const calculateMaxAppointments = () => {
  const { startTime, endTime, timePeriodInterval } = scheduleForm.value
  
  if (!startTime || !endTime || !timePeriodInterval) {
    return 0
  }
  
  // 将时间字符串转换为分钟数
  const parseTimeToMinutes = (timeStr) => {
    const [hours, minutes] = timeStr.split(':').map(Number)
    return hours * 60 + minutes
  }
  
  const startMinutes = parseTimeToMinutes(startTime)
  const endMinutes = parseTimeToMinutes(endTime)
  
  // 计算总时长（分钟）
  const totalMinutes = endMinutes - startMinutes
  
  // 计算最大排班数量
  const maxAppointments = Math.floor(totalMinutes / timePeriodInterval)
  
  return maxAppointments > 0 ? maxAppointments : 0
}

// 监听时间和间隔变化，自动计算最大排班数量
const updateMaxAppointments = () => {
  scheduleForm.value.maxAppointments = calculateMaxAppointments()
}

// 时段改变时更新时间和最大排班数量
const handlePeriodChange = () => {
  const period = periodOptions.find(p => p.value === scheduleForm.value.period)
  if (period) {
    scheduleForm.value.startTime = period.startTime
    scheduleForm.value.endTime = period.endTime
    updateMaxAppointments()
  }

  // 清除开始时间和结束时间的验证错误
  if (scheduleFormRef.value) {
      scheduleFormRef.value.clearValidate(['startTime', 'endTime'])
  }
}

// 时间改变时重新计算
const handleTimeChange = () => {
  updateMaxAppointments()
}

// 间隔改变时重新计算
const handleTimePeriodIntervalChange = () => {
  updateMaxAppointments()
}

// 批量生成时段改变处理
const handleBatchPeriodChange = () => {
  // 根据选择的时段自动设置对应的时间
  const period = periodOptions.find(p => p.value === batchForm.value.period)
  if (period) {
    batchForm.value.startTime = period.startTime
    batchForm.value.endTime = period.endTime
    updateBatchMaxAppointments()
  }

  // 清除开始时间和结束时间的验证错误
  if (batchFormRef.value) {
      batchFormRef.value.clearValidate(['startTime', 'endTime'])
  }
}

// 批量生成时间改变处理
const handleBatchTimeChange = () => {
  updateBatchMaxAppointments()
}

// 批量生成时间间隔改变处理
const handleBatchTimePeriodIntervalChange = () => {
  updateBatchMaxAppointments()
}

// 更新批量生成的最大预约数
const updateBatchMaxAppointments = () => {
  if (!batchForm.value.startTime || !batchForm.value.endTime || !batchForm.value.timePeriodInterval) {
    batchForm.value.maxAppointments = 0
    return
  }
  
  const [startHour, startMinute] = batchForm.value.startTime.split(':').map(Number)
  const [endHour, endMinute] = batchForm.value.endTime.split(':').map(Number)
  
  const startTotalMinutes = startHour * 60 + startMinute
  const endTotalMinutes = endHour * 60 + endMinute
  
  if (endTotalMinutes <= startTotalMinutes) {
    batchForm.value.maxAppointments = 0
    return
  }
  
  const totalMinutes = endTotalMinutes - startTotalMinutes
  const slots = Math.floor(totalMinutes / batchForm.value.timePeriodInterval)
  batchForm.value.maxAppointments = Math.max(0, slots)
}

// 批量生成提交服务端
const handleBatchGenerate = async () => {
  batchFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await batchGenerateScheduleApi(batchForm.value)
        if (res.code === 1) {
          ElMessage.success('批量生成成功')
          batchDialogVisible.value = false
          resetBatchForm()
          getScheduleData()
        }
      } catch (error) {
        ElMessage.error('服务异常')
      }
    }
  })
}

// 打开批量添加对话框
const handleAddBatchSchedule = () => {
  resetBatchForm()
  batchDialogVisible.value = true; // 显示对话框
}

// 打开添加对话框
const handleAddSchedule = () => {
  selectedSchedule.value = null
  resetForm()
  dialogVisible.value = true
}

// 打开编辑对话框
const handleEditSchedule = (row) => {
  selectedSchedule.value = row
  scheduleForm.value = { ...row }
  dialogVisible.value = true

  // 清除表单验证状态
  if (scheduleFormRef.value) {
    scheduleFormRef.value.clearValidate()
  }
}

// 重置表单
const resetForm = () => {
  scheduleForm.value = {
    doctorId: '',
    workDate: formattedDate.value,
    period: 1,
    maxAppointments: 60,
    startTime: '08:00',
    endTime: '11:00',
    timePeriodInterval: 3
  }
  // 清除表单验证状态
  if (scheduleFormRef.value) {
    scheduleFormRef.value.clearValidate()
  }
}

// 重置批量表单
const resetBatchForm = () => {
  batchForm.value = {
    doctorIds: [],
    startDate: '',
    endDate: '',
    period: 1,
    startTime: '08:00',
    endTime: '11:00',
    timePeriodInterval: 3,
    maxAppointments: 60,
    workDays: [1, 2, 3, 4, 5]
  }
  // 清除表单验证状态
  if (batchFormRef.value) {
    batchFormRef.value.clearValidate()
  }
  
}

// 日期改变
const handleDateChange = () => {
  // 如果日期被删除（为null或undefined），自动设置为今天
  if (!currentDate.value) {
    currentDate.value = new Date()
  }
  getScheduleData()
}

// 获取时段名称
const getPeriodName = (period) => {
  const periodOption = periodOptions.find(p => p.value === period)
  return periodOption ? periodOption.label : '未知时段'
}

// 初始化
onMounted(() => {
  getDoctorList()
  getScheduleData()
  resetForm()
})

// 获取预约状态类型
const getAppointmentStatusType = (row) => {
  if (row.leftAppointments === 0) {
    return 'danger' // 没号时显示danger
  }
  
  const remainingPercentage = (row.leftAppointments / row.maxAppointments) * 100
  
  if (remainingPercentage >= 50) {
    return 'success' // 剩余号源50%以上显示绿色
  } else {
    return 'warning' // 否则显示黄色
  }
}

// 计算是否已有预约
const hasAppointments = computed(() => {
  if (!selectedSchedule.value) return false
  const { maxAppointments = 0, leftAppointments = 0 } = selectedSchedule.value
  return maxAppointments - leftAppointments > 0
})

// 修改对话框标题
const dialogTitle = computed(() => {
  if (!selectedSchedule.value) return '添加排班'
  return hasAppointments.value ? '查看排班详情' : '编辑排班'
})

// 添加/修改对话框表单对象
const scheduleFormRef = ref(null)

// 添加表单验证规则
const scheduleFormRules = {
  doctorId: [
    { required: true, message: '请选择医生', trigger: 'change' }
  ],
  workDate: [
    { required: true, message: '请选择排班日期', trigger: 'change' }
  ],
  period: [
    { required: true, message: '请选择时段', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  timePeriodInterval: [
    { required: true, message: '请选择时间段间隔', trigger: 'change' }
  ]
}

// 批量添加对话框表单对象
const batchFormRef = ref(null)

// 批量添加表单验证规则
const batchFormRules = {
  doctorIds: [
    { required: true, message: '请选择医生', trigger: 'change' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ],
  period: [
    { required: true, message: '请选择时段', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  timePeriodInterval: [
    { required: true, message: '请选择时间段间隔', trigger: 'change' }
  ],
  workDays: [
    { required: true, message: '请选择工作日', trigger: 'change' }
  ]
}
const timeSlotsContainerRef = ref(null)
</script>

<template>
  <div class="doctor-schedule">
    <!-- 操作栏 -->
    <el-card class="operation-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-date-picker v-model="currentDate" type="date" placeholder="选择日期" @change="handleDateChange" style="width: 100%"/>
        </el-col>
        <el-col :span="16">
          <el-button type="primary" :icon="Plus" @click="handleAddSchedule">
            添加排班
          </el-button>
          <el-button type="success" :icon="Calendar" @click="handleAddBatchSchedule">
            批量生成
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 排班列表 -->
    <el-card class="schedule-card">
      <template #header>
        <div class="card-header">
          <span>排班列表 - {{ formattedDate }}</span>
          <div class="header-info">
            <el-tag size="small" type="info">共 {{ scheduleData.length }} 条记录</el-tag>
          </div>
        </div>
      </template>

      <el-table 
        :data="scheduleData" 
        v-loading="loading" 
        stripe 
        :header-cell-style="{ backgroundColor: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
        :row-style="{ height: '60px' }"
        style="width: 100%"
        empty-text="暂无排班数据"
      >
        <!-- doctorName -->
        <el-table-column prop="doctorName" label="医生姓名" width="120" align="center" sortable>
          <template #default="{ row }">
            <div class="doctor-info">
              <el-avatar :size="32" style="margin-right: 8px; background-color: #409eff;">
                {{ row.doctorName?.charAt(0) || '医' }}
              </el-avatar>
              <span class="doctor-name">{{ row.doctorName || '未知医生' }}</span>
            </div>
          </template>
        </el-table-column>
        <!-- departmentName -->
        <el-table-column prop="departmentName" label="科室" width="140" align="center" sortable>
          <template #default="{ row }">
            <el-tag type="" size="small" effect="plain">
              {{ row.departmentName || '未知科室' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- workDate -->
        <el-table-column prop="workDate" label="排班日期" width="120" align="center">
          <template #default="{ row }">
            <div class="date-info">
              <i class="el-icon-calendar" style="margin-right: 4px; color: #909399;"></i>
              {{ row.workDate }}
            </div>
          </template>
        </el-table-column>
        <!-- period -->
        <el-table-column prop="period" label="时段" width="100" align="center" sortable>
          <template #default="{ row }">
            <el-tag 
              :type="row.period === 1 ? 'warning' : 'success'" 
              size="small"
              effect="light"
            >
              {{ getPeriodName(row.period) }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- maxAppointments -->
        <el-table-column prop="maxAppointments" label="总号源" width="100" align="center" sortable>
          <template #default="{ row }">
            <span class="number-display">{{ row.maxAppointments || 0 }}</span>
          </template>
        </el-table-column>
        <!-- leftAppointments -->
        <el-table-column prop="leftAppointments" label="剩余号源" width="120" align="center" sortable>
          <template #default="{ row }">
            <el-tag 
              :type="row.leftAppointments > 10 ? 'success' : row.leftAppointments > 0 ? 'warning' : 'danger'"
              size="medium"
              effect="dark"
            >
              {{ row.leftAppointments || 0 }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 预约状态 -->
        <el-table-column label="预约状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="getAppointmentStatusType(row)"
              size="medium"
              effect="dark"
            >
              <i :class="row.leftAppointments > 0 ? 'el-icon-check' : 'el-icon-close'" style="margin-right: 4px;"></i>
              {{ row.leftAppointments > 0 ? '可预约' : '没号' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 操作按钮 -->
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" :icon="Edit" @click="handleEditSchedule(row)" plain>
                编辑
              </el-button>
              <el-button type="info" size="small" @click="getDoctorTimeSlots(row.scheduleId)" plain>
                查看时间段
              </el-button>
              <el-button type="danger" size="small" :icon="Delete" @click="handleDeleteSchedule(row)" plain>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 表格底部统计信息 -->
      <div class="table-footer" v-if="scheduleData.length > 0">
        <div class="footer-stats">
          <!-- 医生数量 -->
          <span class="stat-item">
            <i class="el-icon-user" style="margin-right: 4px;"></i>
            医生数量: {{ new Set(scheduleData.map(item => item.doctorName)).size }}
          </span>
          <!-- 总号源 -->
          <span class="stat-item">
            <i class="el-icon-tickets" style="margin-right: 4px;"></i>
            总号源: {{ scheduleData.reduce((sum, item) => sum + (item.maxAppointments || 0), 0) }}
          </span>
          <!-- 剩余号源 -->
          <span class="stat-item">
            <i class="el-icon-warning" style="margin-right: 4px;"></i>
            剩余号源: {{ scheduleData.reduce((sum, item) => sum + (item.leftAppointments || 0), 0) }}
          </span>
        </div>
      </div>
    </el-card>

    <!-- 添加/编辑排班对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form 
        ref="scheduleFormRef" 
        :model="scheduleForm"
        :rules="scheduleFormRules" 
        label-width="100px">
        <!-- doctorId -->
        <el-form-item prop="doctorId" label="医生" required>
          <el-select 
            v-model="scheduleForm.doctorId" 
            placeholder="请选择医生" 
            style="width: 100%"
            filterable
            :disabled="selectedSchedule"
          >
            <el-option
              v-for="doctor in doctorList"
              :key="doctor.id"
              :label="doctor.name"
              :value="doctor.id"
            />
          </el-select>
        </el-form-item>
        <!-- workDate -->
        <el-form-item prop="workDate" label="排班日期" required>
          <el-date-picker
            v-model="scheduleForm.workDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
            :disabled="hasAppointments"
          />
        </el-form-item>
        <!-- period -->
        <el-form-item prop="period" label="时段" required>
          <el-select 
            v-model="scheduleForm.period" 
            @change="handlePeriodChange" 
            style="width: 100%"
            :disabled="hasAppointments"
          >
            <el-option
              v-for="period in periodOptions"
              :key="period.value"
              :label="period.label"
              :value="period.value"
            />
          </el-select>
        </el-form-item>
        
        <!-- 时间相关字段也需要禁用 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <!-- startTime -->
            <el-form-item prop="startTime" required label="开始时间">
              <el-time-picker
                v-model="scheduleForm.startTime"
                format="HH:mm"
                value-format="HH:mm"
                @change="handleTimeChange"
                style="width: 100%"
                :disabled="hasAppointments"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- endTime -->
            <el-form-item prop="endTime" required label="结束时间">
              <el-time-picker
                v-model="scheduleForm.endTime"
                format="HH:mm"
                value-format="HH:mm"
                @change="handleTimeChange"
                style="width: 100%"
                :disabled="hasAppointments"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 时间段间隔和最大预约数也需要禁用 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <!-- timePeriodInterval -->
            <el-form-item prop="timePeriodInterval" required label="时间段间隔">
              <el-select 
                v-model="scheduleForm.timePeriodInterval" 
                @change="handleTimePeriodIntervalChange" 
                style="width: 100%"
                :disabled="hasAppointments"
              >
                <el-option label="3分钟" :value="3" />
                <el-option label="5分钟" :value="5" />
                <el-option label="10分钟" :value="10" />
                <el-option label="15分钟" :value="15" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大预约数">
              <el-input-number 
                v-model="scheduleForm.maxAppointments" 
                :min="1" 
                :max="200" 
                :disabled="true"
                style="width: 100%" 
              />
              <div class="form-tip">根据时间段自动计算</div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 如果有预约，显示预约信息 -->
        <el-form-item v-if="hasAppointments" label="预约情况">
          <el-alert
            title="该排班已有用户预约，无法修改时间和号源设置"
            type="warning"
            :closable="false"
            show-icon
          >
            <template #default>
              <p>已预约：{{ (selectedSchedule?.maxAppointments || 0) - (selectedSchedule?.leftAppointments || 0) }} 个号源</p>
              <p>剩余：{{ selectedSchedule?.leftAppointments || 0 }} 个号源</p>
            </template>
          </el-alert>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">{{ hasAppointments ? '关闭' : '取消' }}</el-button>
        <el-button 
          v-if="!hasAppointments" 
          type="primary" 
          @click="handleSaveSchedule"
        >
          保存
        </el-button>
      </template>
    </el-dialog>

    <!-- 批量生成对话框 -->
    <el-dialog v-model="batchDialogVisible" title="批量生成排班" width="700px">
      <el-form 
        ref="batchFormRef" 
        :model="batchForm"
        :rules="batchFormRules" 
        label-width="100px">
        <!-- doctorIds -->
        <el-form-item prop="doctorIds" label="选择医生" required>
          <el-select v-model="batchForm.doctorIds" filterable multiple placeholder="请选择医生" style="width: 100%">
            <el-option
              v-for="doctor in doctorList"
              :key="doctor.id"
              :label="doctor.name"
              :value="doctor.id"
            />
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <!-- startDate -->
            <el-form-item prop="startDate" label="开始日期" required>
              <el-date-picker
                v-model="batchForm.startDate"
                type="date"
                placeholder="选择开始日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- endDate -->
            <el-form-item prop="endDate" label="结束日期" required>
              <el-date-picker
                v-model="batchForm.endDate"
                type="date"
                placeholder="选择结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- period -->
        <el-form-item label="时段" required>
          <el-radio-group v-model="batchForm.period" @change="handleBatchPeriodChange">
            <el-radio v-for="period in periodOptions" :key="period.value" :label="period.value">
              {{ period.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        
        <!-- 时间设置 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <!-- startTime -->
            <el-form-item prop="startTime" label="开始时间">
              <el-time-picker
                v-model="batchForm.startTime"
                format="HH:mm"
                value-format="HH:mm"
                @change="handleBatchTimeChange"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- endTime -->
            <el-form-item prop="endTime" label="结束时间">
              <el-time-picker
                v-model="batchForm.endTime"
                format="HH:mm"
                value-format="HH:mm"
                @change="handleBatchTimeChange"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 时间段间隔和最大预约数 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <!-- timePeriodInterval -->
            <el-form-item prop="timePeriodInterval" label="时间段间隔">
              <el-select 
                v-model="batchForm.timePeriodInterval" 
                @change="handleBatchTimePeriodIntervalChange" 
                style="width: 100%"
              >
                <el-option label="3分钟" :value="3" />
                <el-option label="5分钟" :value="5" />
                <el-option label="10分钟" :value="10" />
                <el-option label="15分钟" :value="15" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- maxAppointments -->
            <el-form-item label="最大预约数">
              <el-input-number 
                v-model="batchForm.maxAppointments" 
                :min="1" 
                :max="200" 
                :disabled="true"
                style="width: 100%" 
              />
              <div class="form-tip">根据时间段自动计算</div>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- workDays -->
        <el-form-item prop="workDays" label="工作日">
          <el-checkbox-group v-model="batchForm.workDays">
            <el-checkbox v-for="day in weekDayOptions" :key="day.value" :label="day.value">
              {{ day.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchGenerate">生成</el-button>
      </template>
    </el-dialog>

    <!-- 时间段查看对话框 -->
    <el-dialog v-model="timeSlotsDialogVisible" title="时间段查看" width="900px" :close-on-click-modal="false">
      <div ref="timeSlotsContainerRef" class="time-slots-container">
        <!-- 删除生成按钮的头部区域 -->
        <div class="time-slots-info" v-if="selectedSchedule">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="医生姓名">
              <el-tag type="primary">{{ selectedSchedule.doctorName }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="科室">
              <el-tag>{{ selectedSchedule.departmentName }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="排班日期">
              <el-tag type="info">{{ selectedSchedule.workDate }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="时段">
              <el-tag :type="selectedSchedule.period === 1 ? 'warning' : 'success'">
                {{ getPeriodName(selectedSchedule.period) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="总号源">
              <span class="number-highlight">{{ selectedSchedule.maxAppointments }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="剩余号源">
              <el-tag :type="selectedSchedule.leftAppointments > 10 ? 'success' : selectedSchedule.leftAppointments > 0 ? 'warning' : 'danger'">
                {{ selectedSchedule.leftAppointments }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="time-slots-content">
          <div class="content-header">
            <h4>时间段详情</h4>
            <el-tag size="small" type="info">共 {{ timeSlotsList.length }} 个时间段</el-tag>
          </div>
          
          <div class="time-slots-grid" v-if="timeSlotsList.length > 0">
            <div
              v-for="slot in timeSlotsList"
              :key="slot.id"
              :class="[
                'time-slot-item',
                {
                  'available': slot.status === 0,
                  'booked': slot.status === 1,
                  'cancelled': slot.status === 2
                }
              ]"
            >
              <div class="slot-time">{{ slot.startTime }} - {{ slot.endTime }}</div>
              <div class="slot-number">第{{ slot.numberId }}号</div>
              <div class="slot-status">
                <el-tag 
                  :type="slot.status === 0 ? 'success' : slot.status === 1 ? 'warning' : 'danger'"
                  size="small"
                >
                  {{ slot.status === 0 ? '可预约' : slot.status === 1 ? '已预约' : '已取消' }}
                </el-tag>
              </div>
            </div>
          </div>
          
          <el-empty v-else description="暂无时间段数据" :image-size="100" />
        </div>
      </div>
      
      <template #footer>
        <el-button @click="timeSlotsDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.doctor-schedule {
  padding: 0;
  width: 100%;
  min-width: 100%;
  box-sizing: border-box;
}

.operation-card {
  margin-bottom: 20px;
  width: 100%;
}

.operation-card :deep(.el-card__body) {
  width: 100%;
}

.schedule-card {
  margin-bottom: 20px;
  width: 100%;
}

.schedule-card :deep(.el-card__body) {
  width: 100%;
  padding: 0;
}

/* 强制表格撑满 */
.schedule-card :deep(.el-table) {
  width: 100% !important;
  min-width: 100% !important;
}

.schedule-card :deep(.el-table__body-wrapper) {
  width: 100% !important;
}

.schedule-card :deep(.el-table__header-wrapper) {
  width: 100% !important;
}

.schedule-card :deep(.el-table__body) {
  width: 100% !important;
}

.schedule-card :deep(.el-table__header) {
  width: 100% !important;
}

/* 表格行撑满 */
.schedule-card :deep(.el-table tr) {
  width: 100% !important;
}

/* 表格单元格字体优化 */
.schedule-card :deep(.el-table th) {
  font-size: 15px !important;
  font-weight: 600 !important;
  padding: 12px 8px !important;
}

.schedule-card :deep(.el-table td) {
  font-size: 14px !important;
  padding: 12px 8px !important;
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  width: 100%;
}

.header-info {
  display: flex;
  gap: 10px;
}

/* 医生信息样式 */
.doctor-info {
  display: flex;
  align-items: center;
  justify-content: center;
}

.doctor-name {
  font-weight: 500;
  color: #303133;
  font-size: 15px;
}

/* 日期信息样式 */
.date-info {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #606266;
}

/* 数字显示样式 */
.number-display {
  font-weight: bold;
  font-size: 16px;
  color: #409eff;
}

/* 状态信息样式 */
.status-info {
  text-align: center;
}

.progress-info {
  margin-top: 4px;
}

.progress-text {
  font-size: 13px;
  color: #909399;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 6px;
  justify-content: center;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  margin: 0;
  font-size: 13px;
  padding: 6px 12px;
}

/* 表格底部统计 */
.table-footer {
  margin-top: 16px;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  width: 100%;
  box-sizing: border-box;
}

.footer-stats {
  display: flex;
  justify-content: space-around;
  align-items: center;
  width: 100%;
}

.stat-item {
  display: flex;
  align-items: center;
  font-size: 15px;
  color: #606266;
  font-weight: 500;
}

.stat-item i {
  color: #409eff;
}

/* 标签样式优化 */
:deep(.el-tag) {
  font-size: 13px !important;
  padding: 4px 8px !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }
  
  .footer-stats {
    flex-direction: column;
    gap: 8px;
  }
  
  .schedule-card :deep(.el-table td) {
    font-size: 13px !important;
    padding: 8px 4px !important;
  }
}

/* 时间段查看相关样式 - 全新设计 */
.time-slots-container {
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  padding: 20px;
  background-color: #f9fafb; /* Slightly off-white background for the container */
  border-radius: 8px; /* Consistent rounding */
  max-height: 70vh; /* Limit height to 70% of viewport height */
  overflow-y: auto;
}

.time-slots-info {
  margin-bottom: 25px;
  padding: 18px;
  background-color: #ffffff; /* White background for info section */
  border: 1px solid #e5e7eb; /* Light border */
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.time-slots-info h3 {
  font-size: 1.15em;
  color: #1f2937; /* Darker text for headings */
  margin-top: 0;
  margin-bottom: 8px;
}

.time-slots-info p {
  font-size: 0.95em;
  color: #4b5563; /* Medium-dark text for paragraphs */
  margin-bottom: 5px;
}

.time-slots-info .highlight {
  font-weight: 600;
  color: #059669; /* A distinct highlight color (e.g., green) */
}

.time-slots-content {
  margin-top: 20px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e5e7eb; /* Consistent light border */
}

.content-header h4 {
  margin: 0;
  color: #1f2937;
  font-size: 1.1em;
  font-weight: 600;
}

.number-highlight {
  font-weight: bold;
  color: #2563eb; /* Blue for number highlights */
  font-size: 1.1em;
}

.time-slots-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); /* Slightly smaller min-width */
  gap: 18px; /* Slightly increased gap */
}

.time-slot-item {
  padding: 18px 15px;
  border: 1px solid #d1d5db; /* Default border */
  border-radius: 6px;
  text-align: center;
  transition: transform 0.2s ease-out, box-shadow 0.2s ease-out, border-color 0.2s ease-out;
  background-color: #ffffff;
  cursor: default; /* Default cursor, can be changed if items are clickable */
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.time-slot-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.07);
}

.time-slot-item.available {
  border-left: 5px solid #10b981; /* Green left border for available */
  background-color: #f0fdf4;
  color: #065f46;
}

.time-slot-item.available:hover {
  border-color: #059669;
  background-color: #dcfce7;
}

.time-slot-item.booked {
  border-left: 5px solid #f59e0b; /* Amber left border for booked */
  background-color: #fffbeb;
  color: #78350f;
  opacity: 0.9;
}

.time-slot-item.booked:hover {
  border-color: #d97706;
  background-color: #fef3c7;
}

.time-slot-item.cancelled {
  border-left: 5px solid #ef4444; /* Red left border for cancelled */
  background-color: #fef2f2;
  color: #991b1b;
  opacity: 0.8;
  text-decoration: line-through;
}

.time-slot-item.cancelled:hover {
  border-color: #dc2626;
  background-color: #fee2e2;
}

.time-slot-details p {
  margin: 5px 0;
  font-size: 0.9em;
}

.slot-time {
  font-weight: 600;
  font-size: 1.05em;
  color: #374151;
  margin-bottom: 8px;
}

.slot-status {
  font-size: 0.85em;
  font-style: italic;
  margin-top: 10px;
}

.time-slot-item.available .slot-status {
  color: #047857;
}

.time-slot-item.booked .slot-status {
  color: #b45309;
}

.time-slot-item.cancelled .slot-status {
  color: #b91c1c;
}

/* Responsive adjustments for smaller screens */
@media (max-width: 768px) {
  .time-slots-container {
    padding: 15px;
  }
  .time-slots-grid {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 12px;
  }
  .time-slot-item {
    padding: 15px 12px;
  }
  .content-header h4,
  .number-highlight {
    font-size: 1em;
  }
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>



