<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDepartmentListApi } from '@/apis/department'
import { pageQueryDoctorListApi, addDoctorApi, updateDoctorApi, deleteDoctorApi } from '@/apis/doctor'

// 医生列表数据
const doctorList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加医生')
const currentDoctor = ref({})

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 分页查询医生列表
const pageQueryDoctorList = async () => {
  try {
    loading.value = true;
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm.value
    }
    const res = await pageQueryDoctorListApi(params);
    if (res.code == 1){
      total.value = res.data.total;
      loading.value = false;
      doctorList.value = res.data.records;
    }
  }finally {
    loading.value = false;
  }
}
onMounted(() => pageQueryDoctorList())

// 搜索表单
const searchForm = ref({
  doctorName: '',
  departmentId: '',
  title: '',
})

// 科室选项
const departments = ref([])
const getDepartmentList = async () => {
  const res = await getDepartmentListApi()
  if (res.code === 1){
    departments.value = res.data
  }
}
onMounted(() => getDepartmentList())

// 职称选项(手动修改)
const titles = ['主任医师', '副主任医师', '主治医师', '住院医师','实习医师']

// 院区选项
const campuses = ['宁波市第二医院(本部院区)', '宁波市第二医院(北院区)']

// 状态选项
const statusOptions = [
  { label: '在职', value: 1 },
  { label: '离职', value: 0 }
]

// 搜索医生
const handleSearch = () => {
  currentPage.value = 1
  pageQueryDoctorList()
}

// 重置搜索
const handleReset = () => {
  searchForm.value = {
    doctorName: '',
    departmentId: '',
    title: '',
  }
  pageQueryDoctorList()
}

// 医生表单数据
const doctorForm = ref({
  name: '',
  avatar: '',
  departmentId: '',
  campus: '',
  title: '',
  specialty: '',
  totalSchedule: '',
  description: '',
  fee: '',
  location: '',
  status: 1
})


// 添加医生
const handleAdd = () => {
  dialogTitle.value = '添加医生'
  currentDoctor.value = {}
  doctorForm.value = {
    name: '',
    avatar: '',
    department_id: '',
    campus: '',
    title: '',
    specialty: '',
    totalSchedule: '0',
    description: '',
    fee: '0',
    location: '',
    status: 1
  }
  dialogVisible.value = true
}

// 编辑医生
const handleEdit = (row) => {
  dialogTitle.value = '编辑医生'
  currentDoctor.value = row
  doctorForm.value = { ...row }
  dialogVisible.value = true
}

// 删除医生
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除医生 ${row.name} 吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    const res = await deleteDoctorApi(row.id);
    if (res.code === 1){
      ElMessage.success('删除成功')
      pageQueryDoctorList()
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 保存医生信息
const handleSave = async () => {
  // 验证必填字段
  if (!doctorForm.value.name) {
    ElMessage.warning('请输入医生姓名')
    return
  }
  if (!doctorForm.value.departmentId) {
    ElMessage.warning('请选择科室')
    return
  }
  if (!doctorForm.value.title) {
    ElMessage.warning('请选择职称')
    return
  }
  if (!doctorForm.value.campus) {
    ElMessage.warning('请选择院区')
    return
  }
  if (!doctorForm.value.totalSchedule) {
    doctorForm.value.totalSchedule = 0
  }
  // 方案2：直接在条件中声明
  const res = dialogTitle.value === '添加医生' 
    ? await addDoctorApi(doctorForm.value)
    : await updateDoctorApi(doctorForm.value);
  
  if (res.code == 1){
    ElMessage.success(dialogTitle.value === '添加医生' ? '添加成功' : '修改成功')
  }
  console.log(res)
  
  dialogVisible.value = false
  pageQueryDoctorList()
}

// 分页改变
const handlePageChange = (page) => {
  currentPage.value = page
  pageQueryDoctorList()
}

// 头像上传相关
const uploadRef = ref()
const uploadLoading = ref(false)

// 头像上传前的校验
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 头像上传成功回调
const handleAvatarSuccess = (response) => {
  uploadLoading.value = false
  if (response.code === 1) {
    doctorForm.value.avatar = response.data
    ElMessage.success('头像上传成功')
  }
}

// 头像上传失败回调
const handleAvatarError = () => {
  uploadLoading.value = false
}

// 头像上传中
const handleAvatarProgress = () => {
  uploadLoading.value = true
}
</script>

<template>
  <div class="doctor-management">
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="医生姓名">
          <el-input v-model="searchForm.doctorName" placeholder="请输入医生姓名" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item label="科室">
          <el-select 
            v-model="searchForm.departmentId" 
            placeholder="请选择科室" 
            clearable 
            filterable
            style="width: 180px;"
          >
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职称">
          <el-select v-model="searchForm.title" placeholder="请选择职称" clearable style="width: 150px;">
            <el-option v-for="title in titles" :key="title" :label="title" :value="title" />
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
        <div class="card-header">
          <span class="table-title">医生列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加医生
          </el-button>
        </div>
      </template>

      <el-table :data="doctorList" v-loading="loading" stripe>
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40">
              <el-icon><User /></el-icon>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="科室" width="120" />
        <el-table-column prop="campus" label="院区" width="100" />
        <el-table-column prop="title" label="职称" width="120" />
        <el-table-column prop="specialty" label="专长" width="150" show-overflow-tooltip />
        <el-table-column prop="totalSchedule" label="总预约量" width="100" />
        <el-table-column prop="fee" label="挂号费" width="100">
          <template #default="{ row }">
            ¥{{ row?.fee.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="诊室" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="简介" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 添加/编辑医生对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="doctorForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" required>
              <el-input v-model="doctorForm.name" placeholder="请输入医生姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="头像">
              <el-upload
                ref="uploadRef"
                class="avatar-uploader"
                action="/api/common/avatar/upload"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :on-success="handleAvatarSuccess"
                :on-error="handleAvatarError"
                :on-progress="handleAvatarProgress"
              >
                <img v-if="doctorForm.avatar" :src="doctorForm.avatar" class="avatar" />
                <el-icon v-else-if="uploadLoading" class="avatar-uploader-icon is-loading">
                  <Loading />
                </el-icon>
                <el-icon v-else class="avatar-uploader-icon">
                  <Plus />
                </el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="科室" required>
              <el-select 
                v-model="doctorForm.departmentId" 
                placeholder="请选择科室"
                filterable
                style="width: 100%;"
              >
                <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="院区" required>
              <el-select 
                v-model="doctorForm.campus" 
                placeholder="请选择院区"
                style="width: 100%;"
              >
                <el-option v-for="campus in campuses" :key="campus" :label="campus" :value="campus" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职称" required>
              <el-select 
                v-model="doctorForm.title" 
                placeholder="请选择职称"
                style="width: 100%;"
              >
                <el-option v-for="title in titles" :key="title" :label="title" :value="title" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="总预约量" >
              <el-input v-model="doctorForm.totalSchedule" placeholder="请输入总预约量" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="挂号费">
              <el-input-number 
                v-model="doctorForm.fee" 
                :min="0" 
                :precision="2"
                placeholder="请输入挂号费"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="诊室">
              <el-input v-model="doctorForm.location" placeholder="请输入诊室位置" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select 
                v-model="doctorForm.status" 
                placeholder="请选择状态"
                style="width: 100%;"
              >
                <el-option v-for="status in statusOptions" :key="status.value" :label="status.label" :value="status.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="专长">
          <el-input v-model="doctorForm.specialty" placeholder="请输入专业特长" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input
            v-model="doctorForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入医生简介"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.doctor-management {
  padding: 0;
}

.table-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

/* 头像上传样式 */
.avatar-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.avatar-uploader-icon.is-loading {
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>