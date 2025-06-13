<script setup>
import { ref, onMounted } from 'vue'
import { pageQueryLogApi } from '@/apis/log'
import 'element-plus/theme-chalk/el-message.css'
import { ElMessage } from 'element-plus'
import noneImage from '@/assets/images/none.png'

// 日志数据
const logList = ref([])
const loading = ref(false)

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索条件
const searchForm = ref({
  className: '',
  methodName: '',
  operateTimeStart: '',
  operateTimeEnd: ''
})

const searchFormRef = ref()

// 获取日志列表
const getLogList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm.value
    }
    const res = await pageQueryLogApi(params)
    if (res.code === 1) {
      logList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取操作日志失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getLogList()
}

// 重置搜索
const handleReset = () => {
  searchFormRef.value.resetFields()
  currentPage.value = 1
  getLogList()
}

// 分页改变
const handlePageChange = (page) => {
  currentPage.value = page
  getLogList()
}

// 页面大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getLogList()
}

// 格式化操作时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 格式化耗时
const formatCostTime = (costTime) => {
  if (!costTime) return '-'
  return `${costTime}ms`
}

// 页面加载时获取数据
onMounted(() => {
  getLogList()
})
</script>

<template>
  <div class="log-container">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form
        ref="searchFormRef"
        :model="searchForm"
        inline
        class="search-form"
      >
        <el-form-item label="操作类" prop="className">
          <el-input
            v-model="searchForm.className"
            placeholder="请输入操作类名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item label="操作方法" prop="methodName">
          <el-input
            v-model="searchForm.methodName"
            placeholder="请输入方法名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item label="操作时间" prop="operateTime">
          <el-date-picker
            v-model="searchForm.operateTimeStart"
            type="datetime"
            placeholder="开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 180px"
          />
          <span style="margin: 0 10px; color: #909399">至</span>
          <el-date-picker
            v-model="searchForm.operateTimeEnd"
            type="datetime"
            placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 180px"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleSearch" 
            :loading="loading"
          >
            搜索
          </el-button>
          <el-button 
            @click="handleReset"
          >
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区域 -->
    <div class="table-section">
      <!-- 无数据状态 -->
      <div v-if="!loading && logList.length === 0" class="no-data">
        <img :src="noneImage" alt="暂无数据" class="no-data-image" />
        <p class="no-data-text">暂无操作日志数据</p>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        v-else
        :data="logList"
        v-loading="loading"
        style="width: 100%"
        border
        resizable
      >
        <el-table-column prop="id" label="ID" width="80" align="center" resizable>
          <template #default="{ row }">
            <span class="id-badge">{{ row.id }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="operateId" label="操作人ID" width="80" align="center" resizable>
          <template #default="{ row }">
            <span class="id-badge">{{ row.operateId || '-' }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="operateName" label="操作人" width="100" align="center" resizable>
          <template #default="{ row }">
            {{ row.operateName || '-' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="className" label="操作类" min-width="350" resizable>
          <template #default="{ row }">
              <el-tooltip 
              :content="row.className" 
              placement="top" 
              :disabled="!row.className"
            >
              <div class="text-wrapper">
                <span class="class-name">
                  {{ row.className || '-' }}
                </span></div>
            </el-tooltip>
            
          </template>
        </el-table-column>
        
        <el-table-column prop="methodName" label="操作方法" min-width="150" resizable>
          <template #default="{ row }">
            <el-tooltip 
              :content="row.methodName" 
              placement="top" 
              :disabled="!row.methodName"
            >
              <div class="text-wrapper">
                <span class="method-name">
                  {{ row.methodName || '-' }}
                </span></div>
            </el-tooltip>
          </template>
        </el-table-column>
        
        <el-table-column prop="methodParams" label="方法参数" min-width="200" resizable>
          <template #default="{ row }">
            <el-tooltip 
              :content="row.methodParams" 
              placement="top" 
              :disabled="!row.methodParams"
            >
              <div class="text-ellipsis">{{ row.methodParams || '-' }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        
        <el-table-column prop="returnValue" label="返回值" min-width="200" resizable>
          <template #default="{ row }">
            <el-tooltip 
              :content="row.returnValue" 
              placement="top" 
              :disabled="!row.returnValue"
            >
              <div class="text-ellipsis">{{ row.returnValue || '-' }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        
        <el-table-column prop="costTime" label="耗时" width="100" align="center" resizable>
          <template #default="{ row }">
            <span class="cost-time" :class="{ 'slow': row.costTime > 1000}"> {{ formatCostTime(row.costTime) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="operateTime" label="操作时间" width="180" align="center" resizable>
          <template #default="{ row }">
            {{ formatDateTime(row.operateTime) }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div v-if="logList.length > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
          background
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.log-container {
  padding: 24px;
  background-color: #f8f9fa;
  min-height: calc(100vh - 60px);
}

.search-section {
  background: white;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
}

.table-section {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
}

.pagination-wrapper {
  padding: 20px;
  display: flex;
  justify-content: center;
  border-top: 1px solid #ebeef5;
}

.no-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
}

.no-data-image {
  max-width: 120px;
  max-height: 120px;
  width: auto;
  height: auto;
  margin-bottom: 20px;
  opacity: 0.6;
  object-fit: contain;
}

.no-data-text {
  font-size: 16px;
  color: #909399;
  margin: 0;
}

.id-badge {
  background: #f0f9ff;
  color: #0ea5e9;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
}

.cost-time {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
}

.cost-time:not(.slow) {
  background: #f0f9ff;
  color: #0ea5e9;
}

.cost-time.slow {
  background: #fef2f2;
  color: #ef4444;
}

.class-name {
  color: #10b981;
  background: #ecfdf5;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: 'Consolas', monospace;
  font-size: 13px;
  display: inline-block;
  max-width: 100%;
}

.text-wrapper {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
}
.method-name {
  color: #8b5cf6;
  background: #f5f3ff;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: 'Consolas', monospace;
  font-size: 13px;
}
</style>