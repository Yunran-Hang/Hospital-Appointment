<script setup>
import { ref, onMounted } from 'vue'
import { pageQueryApi, updateStatusApi } from '@/apis/appointment'
import { ElMessage } from 'element-plus'

// 预约记录数据
const appointmentList = ref([])
const loading = ref(false)

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索条件
const searchForm = ref({
  doctorName: '',
  patientName: '',
  appointmentDate: '',
  status: '',
})

// 详情对话框
const detailDialogVisible = ref(false)
const currentAppointment = ref({})

// 预约状态选项
const statusOptions = [
  { value: 1, label: '待就诊' },
  { value: 3, label: '已完成' },
  { value: 2, label: '已取消' }
]

// 获取预约记录
const getAppointmentList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm.value
    }
    const res = await pageQueryApi(params)
    if (res.code === 1){
      appointmentList.value = res.data.records
      total.value = res.data.total
    }
  }catch (error) {
    ElMessage.error('获取预约记录失败')
  } finally {
    loading.value = false
  } 
}

// 搜索预约记录
const handleSearch = (dd) => {
  currentPage.value = 1
  getAppointmentList()
}
// 重置搜索
const handleReset = () => {
  searchForm.value = {
    doctorName: '',
    patientName: '',
    date: '',
    status: ''
  }
  getAppointmentList()
}

// 查看详情
const handleViewDetail = (row) => {
  currentAppointment.value = row
  detailDialogVisible.value = true
}

// 关闭详情对话框
const handleCloseDetail = () => {
  detailDialogVisible.value = false
  currentAppointment.value = {}
}

// 分页改变
const handlePageChange = (page) => {
  currentPage.value = page
  getAppointmentList()
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const option = statusOptions.find(opt => opt.value === status)
  return option ? option.label : status
}

onMounted(() => {
  getAppointmentList()
})

const handleStatusToFinish = async () => {
  const res = await updateStatusApi(currentAppointment.value.appointmentId)
  if (res.code === 1) {
    ElMessage.success('操作成功')
    detailDialogVisible.value = false
    getAppointmentList()
  }
}
</script>

<template>
  <div class="appointment-records">
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="医生姓名">
          <el-input 
            v-model="searchForm.doctorName" 
            placeholder="请输入医生姓名" 
            clearable 
            style="width: 180px;"
          />
        </el-form-item>
        <el-form-item label="患者姓名">
          <el-input 
            v-model="searchForm.patientName" 
            placeholder="请输入患者姓名" 
            clearable 
            style="width: 180px;"
          />
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="searchForm.appointmentDate"
            type="date"
            placeholder="选择日期"
            clearable
            style="width: 150px;"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择状态" 
            clearable
            style="width: 150px;"
          >
            <el-option
              v-for="option in statusOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <span class="table-title">预约记录列表</span>
      </template>

      <el-table 
        :data="appointmentList" 
        v-loading="loading" 
        stripe 
        class="beautiful-table"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
        :row-style="{ height: '50px' }"
      >
        <el-table-column prop="appointmentId" label="预约ID" width="110" align="center" />
        <el-table-column prop="appointmentNo" label="预约号" width="110" align="center" />
        <el-table-column prop="doctorName" label="医生" width="100" align="center" />
        <el-table-column prop="departmentName" label="科室" width="100" align="center" />
        <el-table-column prop="patientName" label="患者姓名" width="100" align="center" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender == 1 ? 'primary' : 'success'" size="small">
              {{ row.gender == 1 ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="patientPhone" label="患者电话" width="130" align="center" />
        <el-table-column label="年龄" width="80" align="center">
          <template #default="{ row }">
            <span class="age-text">{{ row.age }} 岁</span>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentDate" label="预约日期" width="120" align="center" />
        <el-table-column prop="period" label="时间段" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.period == 1 ? 'warning' : 'info'" size="small">
              {{ row.period == 1 ? '上午' : '下午' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)" round>
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="预约详情"
      width="600px"
      :before-close="handleCloseDetail"
      class="detail-dialog"
    >
      <div class="detail-content">
        <div class="detail-section">
          <h3 class="section-title">
            <el-icon><User /></el-icon>
            患者信息
          </h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">姓名：</span>
              <span class="value">{{ currentAppointment.patientName }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别：</span>
              <span class="value">
                <el-tag :type="currentAppointment.gender == 1 ? 'primary' : 'success'" size="small">
                  {{ currentAppointment.gender == 1 ? '男' : '女' }}
                </el-tag>
              </span>
            </div>
            <div class="info-item">
              <span class="label">年龄：</span>
              <span class="value">{{ currentAppointment.age }} 岁</span>
            </div>
            <div class="info-item">
              <span class="label">联系电话：</span>
              <span class="value">{{ currentAppointment.patientPhone }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">
            <el-icon><Avatar /></el-icon>
            医生信息
          </h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">医生姓名：</span>
              <span class="value">{{ currentAppointment.doctorName }}</span>
            </div>

            <div class="info-item">
              <span class="label">所属科室：</span>
              <span class="value">{{ currentAppointment.departmentName }}</span>
            </div>

            <div class="info-item">
              <span class="label">所属院区：</span>
              <span class="value">{{ currentAppointment.campus }}</span>
            </div>

            <div class="info-item">
              <span class="label">医生职称：</span>
              <span class="value">{{ currentAppointment.title }}</span>
            </div>

            <div class="info-item">
              <span class="label">医生诊金：</span>
              <span class="value">{{ currentAppointment.fee }}</span>
            </div>

            <div class="info-item">
              <span class="label">门诊位置：</span>
              <span class="value">{{ currentAppointment.location }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">
            <el-icon><Calendar /></el-icon>
            预约信息
          </h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">预约ID：</span>
              <span class="value">{{ currentAppointment.appointmentId }}</span>
            </div>
            <div class="info-item">
              <span class="label">预约号：</span>
              <span class="value">{{ currentAppointment.appointmentNo }}</span>
            </div>
            <div class="info-item">
              <span class="label">预约日期：</span>
              <span class="value">{{ currentAppointment.appointmentDate }}</span>
            </div>
            <div class="info-item">
              <span class="label">时间段：</span>
              <span class="value">
                <el-tag :type="currentAppointment.period == 1 ? 'warning' : 'info'" size="small">
                  {{ currentAppointment.period == 1 ? '上午' : '下午' }}
                </el-tag>
              </span>
            </div>
            <div class="info-item">
              <span class="label">预约时间:</span>
              <span class="value">{{ currentAppointment.startTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">预约状态：</span>
              <span class="value">
                <el-tag :type="getStatusType(currentAppointment.status)" size="small">
                  {{ getStatusText(currentAppointment.status) }}
                </el-tag>
              </span>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCloseDetail">关闭</el-button>
          <el-button v-if="currentAppointment.status == 1" @click="handleStatusToFinish" type="warning">已就诊</el-button>
          <el-button type="primary" @click="handleCloseDetail">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.appointment-records {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.table-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.table-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.beautiful-table {
  border-radius: 6px;
  overflow: hidden;
}

.beautiful-table :deep(.el-table__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.beautiful-table :deep(.el-table__header th) {
  background: transparent;
  color: white;
  font-weight: 600;
  border-bottom: none;
}

.beautiful-table :deep(.el-table__row) {
  transition: all 0.3s ease;
}

.beautiful-table :deep(.el-table__row:hover) {
  background-color: #f0f9ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.beautiful-table :deep(.el-table td) {
  border-bottom: 1px solid #ebeef5;
  padding: 12px 0;
}

.age-text {
  color: #606266;
  font-weight: 500;
}

.pagination {
  margin-top: 20px;
  text-align: right;
  padding: 20px 0;
}

:deep(.el-pagination) {
  justify-content: flex-end;
}

:deep(.el-button--small) {
  padding: 5px 15px;
  font-size: 12px;
}

:deep(.el-tag--small) {
  padding: 0 8px;
  height: 24px;
  line-height: 22px;
  border-radius: 12px;
}
.detail-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  margin: 0;
}

.detail-dialog :deep(.el-dialog__title) {
  color: white;
  font-size: 18px;
  font-weight: bold;
}

.detail-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white;
  font-size: 20px;
}

.detail-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.detail-content {
  padding: 20px;
}

.detail-section {
  margin-bottom: 25px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  border-bottom: 2px solid #e4e7ed;
  padding-bottom: 8px;
}

.section-title .el-icon {
  margin-right: 8px;
  color: #409eff;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #409eff;
}

.info-item .label {
  font-weight: 600;
  color: #606266;
  min-width: 80px;
  margin-right: 10px;
}

.info-item .value {
  color: #303133;
  flex: 1;
}

.dialog-footer {
  text-align: right;
  padding: 20px;
  border-top: 1px solid #e4e7ed;
  background-color: #fafafa;
}

.dialog-footer .el-button {
  margin-left: 10px;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .detail-dialog :deep(.el-dialog) {
    width: 90% !important;
    margin: 5vh auto;
  }
}
</style>