<script setup>
import { onMounted, ref, watch } from 'vue';
import noneImage from '@/assets/images/none.png'
import { pageQueryUserInfoApi, addNewUserApi, updateUserInfoApi, deleteUserApi} from '@/apis/user'
import { validateIdCard } from '@/utils/validators'; 
import { ElMessage, ElMessageBox } from 'element-plus';

const searchForm = ref({
    username: '',
    realName: '',
    gender: '',
    birthDate: ''
})
const searchFormRef = ref(null)

// 用户信息列表
const userList = ref([])
const tableLoading = ref(false)
// 定义分页信息
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
// 定义对话框相关变量
const dialogVisible = ref(false)
const dialogTitle = ref('')
const uploadLoading = ref(false)

const userFormRef = ref(null)
const userForm = ref({
    username: '',
    realName: '',
    avatar: '',
    gender: '',
    idNumber: '',
    birthDate: '',
    phone: '',
    medicalCard: '',
    status: '',
    password: ''
})

const pageQueryUserList = async () => {
    try {
        tableLoading.value = true;
        const params = {
            page: currentPage.value,
            pageSize: pageSize.value,
            ...searchForm.value
        }
        const res = await pageQueryUserInfoApi(params);
        if (res.code === 1){
            userList.value = res.data.records
            total.value = res.data.total
        }
    }finally {
        tableLoading.value = false;
    }
}

// 响应搜索按钮点击，发送分页查询请求
const handleSearch = () => {
    currentPage.value = 1
    pageQueryUserList()
}
onMounted(() => handleSearch())
// 重置搜索表单
const handleReset = () => {
    searchForm.value = {
        username: '',
        realName: '',
        gender: '',
        birthDate: ''
    }
    pageQueryUserList()
}

const handleSizeChange = (val) => {
    pageSize.value = val
    pageQueryUserList()
}
const handlePageChange = (val) => {
    currentPage.value = val
    pageQueryUserList()
}

// 从身份证提取生日和性别
const extractInfoFromIdCard = (idCard) => {
  if (idCard.length !== 18) return;
  // 提取生日信息 (格式: YYYYMMDD)
  const year = idCard.substring(6, 10);
  const month = idCard.substring(10, 12);
  const day = idCard.substring(12, 14);
  userForm.value.birthDate = `${year}-${month}-${day}`;
  // 提取性别信息 (倒数第二位: 奇数为男，偶数为女)
  const genderCode = parseInt(idCard.charAt(16));
  userForm.value.gender = genderCode % 2 === 1 ? 1 : 0;
};
// 检测当身份证发生变化的时候自动识别性别和出生年月
watch( () => userForm.value.idNumber, (newVal,oldVal) => {
    if (newVal){
        extractInfoFromIdCard(newVal)
    }
})

// 表单验证
const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
    ],
    realName: [
        { required: true, message: '请输入真实姓名', trigger: 'blur' },
    ],
    idNumber: [
        { required: true, message: '请输入身份证号码', trigger: 'blur' },
        {
            validator: (rule,value,callback) => {
                if (!validateIdCard(value)){
                    callback(new Error('请检查身份证号码格式'))
                } else {
                    callback();
                }
            },
            trigger: 'blur'
        }
    ],
    status: [
        { required: true, message: '请选择账号状态', trigger: 'blur' },
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
    ]
}
// 重置验证状态
const resetValidation = () => {
  if (userFormRef.value) {
    userFormRef.value.clearValidate(); // 清除所有验证状态
  } else {
    console.warn('表单引用未准备好');
  }
};
// 点击添加用户按钮
const handleAdd = () => {
    dialogTitle.value = '添加用户'
    userForm.value = {
        username: '',
        realName: '',
        avatar: '',
        gender: '',
        idNumber: '',
        birthDate: '',
        phone: '',
        medicalCard: '',
        status: 1
    }
    resetValidation()
    dialogVisible.value = true
}

// 点击编辑用户按钮
const handleEdit = async (row) => {
    dialogTitle.value = '编辑用户'
    userForm.value = {...row}
    resetValidation()
    dialogVisible.value = true
    
}

// 点击删除用户按钮
const handleDelete = (row) => {
    ElMessageBox.confirm(
        `确定要删除用户 ${row.realName} 吗？`,
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        const res = await deleteUserApi(row.id);
        if (res.code === 1){
            ElMessage.success('删除成功')
            pageQueryUserList()
        }
    }).catch((error) => {
        if (error == 'cancel') {
            ElMessage.info('已取消删除')
        }
    })
}

// 添加或编辑用户信息
const handleSave = () => {
    userFormRef.value.validate().then( async (valid) => {
        if (valid){
            // 表单验证通过
            const res = userForm.value.id ? 
                await updateUserInfoApi(userForm.value) :
                await addNewUserApi(userForm.value)

            if (res.code === 1) {
                ElMessage.success('操作成功')
                dialogVisible.value = false;
                pageQueryUserList()
            }
        }
    })
}


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
    userForm.value.avatar = response.data
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
    <div class="container">
        <!-- 搜索区域 -->
        <div class="search-section">
            <el-form ref="searchFormRef" :model="searchForm" inline class="search-form">
                <!-- 用户名 -->
                <el-form-item label="用户名" prop="username">
                    <el-input
                        v-model="searchForm.username"
                        placeholder="请输入用户名"
                        clearable
                        style="width: 160px"
                    />
                </el-form-item>
                <!-- 真实姓名 -->
                <el-form-item label="真实姓名" prop="realName">
                    <el-input
                        v-model="searchForm.realName"
                        placeholder="请输入用户真实姓名"
                        clearable
                        style="width: 180px"
                    />
                </el-form-item>
                <!-- 性别 -->
                <el-form-item label="性别" prop="realName">
                    <el-select v-model="searchForm.gender" placeholder="请选择性别" clearable style="width: 120px;">
                        <el-option label="男" value="1" />
                        <el-option label="女" value="2" />
                    </el-select>
                </el-form-item>
                <!-- 出生日期 -->
                <el-form-item label="出生日期">
                    <el-date-picker
                        v-model="searchForm.birthDate"
                        type="date"
                        placeholder="选择日期"
                        clearable
                        style="width: 150px;"
                    />
                </el-form-item>
                <!-- 操作按钮组 -->
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
            

        </div>
        <!-- 表格展示区域 -->
        <el-card class="table-card">
            <template #header>
                <div class="card-header">
                    <span class="table-title">用户信息列表</span>
                    <!-- 添加用户按钮 -->
                    <el-button type="primary" @click="handleAdd">
                        <el-icon><Plus /></el-icon>
                        添加新用户
                    </el-button>
                </div>
                
            </template>
            <!-- 无数据状态 -->
            <div v-if="!tableLoading && userList.length === 0" class="no-data">
                <img :src="noneImage" alt="暂无数据" class="no-data-image" />
                <p class="no-data-text">暂无用户信息</p>
            </div>
            <!-- 表格数据 -->
            <el-table 
                v-else
                :data="userList" 
                v-loading="tableLoading" 
                stripe 
                class="data-table"
                :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
                :row-style="{ height: '50px' }"
            >
                <el-table-column prop="id" label="ID" width="110" align="center" >
                    <template #default="{row}">
                        <el-tag type="info"> {{ row.id }}</el-tag>
                    </template>
                </el-table-column>

                <el-table-column prop="username" label="用户名" width="110" align="center" />
                <el-table-column prop="realName" label="真实姓名" width="110" align="center" />
                <el-table-column prop="avatar" label="用户头像" width="110" align="center" >
                    <template #default="{row}">
                        <el-image :src="row.avatar" fit="cover" style="width: 40px; height: 40px; border-radius: 50%;" />
                    </template>
                </el-table-column>
                <el-table-column prop="gender" label="性别" width="110" align="center" >
                    <template #default="{ row }">
                        <span> {{ row.gender === 1 ? '男' : '女' || '-'}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="idNumber" label="身份证号" width="200" align="center" />
                <el-table-column prop="birthDate" label="出生日期" width="120" align="center" >
                    <template #default="{row}">
                        <el-tag>{{ row.birthDate }}</el-tag>
                    </template>    
                </el-table-column>
                <el-table-column prop="phone" label="电话号码" width="120" align="center" />
                <el-table-column prop="medicalCard" label="医保卡号" width="130" align="center" />
                <el-table-column prop="status" label="状态" width="110" align="center" >
                    <template #default="{row}">
                        <el-tag :type="row.status==1?'success':'danger'"> {{ row.status == 1 ? '正常': '禁用' }} </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
                <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />    
                <el-table-column fixed="right" label="操作" width="180" align="center" > 
                    <template #default="{ row }">
                        <el-button type="primary" size="mini" @click="handleEdit(row)">编辑</el-button>
                        <el-button type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <!-- 分页 -->
            <div v-if="userList.length > 0" class="pagination-wrapper">
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
        </el-card>
        <!-- 添加/编辑用户信息 -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
            <el-form ref="userFormRef" :rules="rules" :model="userForm" label-width="100px">
                <!-- 第一行 -->
                <el-row :gutter="20">
                    <!-- 用户名 -->
                    <el-col :span="12">
                        <el-form-item prop="username" label="用户名" required>
                        <el-input clearable v-model="userForm.username" placeholder="请输入用户名" />
                        </el-form-item>
                    </el-col>
                    <!-- 头像 -->
                    <el-col :span="12" >
                        <el-form-item label="头像" >
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
                                <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar" />
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
                <!-- 第二行 -->
                <el-row :gutter="20">
                    <!-- 真实姓名 -->
                    <el-col :span="12">
                        <el-form-item prop="realName" label="真实姓名" required>
                        <el-input clearable v-model="userForm.realName" placeholder="请输入用户真实姓名" />
                        </el-form-item>
                    </el-col>
                    <!-- 性别 -->
                    <el-col :span="12">
                        <el-form-item label="性别" required>
                        <el-select v-model="userForm.gender" disabled placeholder="请选择性别" style="width: 200px;">
                            <el-option label="男" :value="1" />
                            <el-option label="女" :value="0" />
                        </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <!-- 第三行 -->
                <el-row :gutter="20">
                    <!-- 身份证号 -->
                    <el-col :span="12">
                        <el-form-item prop="idNumber" label="身份证号" required>
                            <el-input clearable v-model="userForm.idNumber" placeholder="请输入身份证号" />
                        </el-form-item>
                    </el-col>
                    <!-- 出生日期 -->
                    <el-col :span="12">
                        <el-form-item label="出生日期" required>
                            <el-date-picker
                                disabled
                                v-model="userForm.birthDate"
                                type="date"
                                placeholder="选择出生日期"
                                clearable
                                style="width: 200px;"
                            />
                        </el-form-item>
                    </el-col>
                </el-row>
                <!-- 第四行 -->
                <el-row :gutter="20">
                    <!-- 电话号码 -->
                    <el-col :span="12">
                        <el-form-item label="电话号码">
                            <el-input clearable v-model="userForm.phone" placeholder="请输入电话号码" ></el-input>
                        </el-form-item>
                    </el-col>
                    <!-- 医保卡号 -->
                    <el-col :span="12">
                        <el-form-item label="医保卡号">
                            <el-input clearable v-model="userForm.medicalCard" placeholder="请输入医保卡号 " />
                        </el-form-item>
                    </el-col>
                </el-row>
                <!-- 第五行 -->
                <el-row :gutter="20">
                    <!-- 用户密码 -->
                     <el-col :span="12">
                        <el-form-item label="密码" :required="!userForm.id" prop="password">
                            <el-input type="password" show-password clearable v-model="userForm.password" placeholder="请输入用户密码 " />
                        </el-form-item>
                     </el-col>
                    <!-- 账号状态 -->
                    <el-col :span="12">
                        <el-form-item prop="status" label="账号状态" required>
                            <el-select v-model="userForm.status" placeholder="请选择账号状态" style="width: 200px;">
                                <el-option label="启用" :value="1" />
                                <el-option label="禁用" :value="0" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSave">保存</el-button>
            </template>
        </el-dialog>
    </div>

</template>

<style scoped>

.container {
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.search-section {
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    background-color: #ffffff;
    margin-bottom: 40px;
}

.search-form {
        display: flex;
        flex-wrap: wrap;
        padding: 0;
        align-items: center;
        gap: 16px;
        justify-content: center;
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

.data-table {
    border-radius: 6px;
    overflow: hidden;
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

.pagination-wrapper {
    padding: 20px;
    display: flex;
    justify-content: center;
    border-top: 1px solid #ebeef5;
}

.avatar-uploader {
    margin-left: auto;
    margin-right: auto;
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

.avatar {
    width: 100px;
    height: 100px;
    display: block;
    object-fit: cover;
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    text-align: center;
    line-height: 100px;
}

.avatar-uploader-icon.is-loading {
    animation: rotating 2s linear infinite;
}

</style>