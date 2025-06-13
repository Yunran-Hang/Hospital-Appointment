<script setup>
import { ref, onMounted } from 'vue';
import { Search, Loading } from '@element-plus/icons-vue';
import DoctorCard from '@/views/Appointment/components/DoctorCard.vue';
import 'element-plus/theme-chalk/el-message.css'
import { ElMessage } from 'element-plus';
import { getCategoryListApi } from '@/apis/category';
import { getDoctorListApi } from '@/apis/doctor';

// 响应式数据
const searchKeyword = ref('');
const selectedDepartment = ref('');
const selectedTitle = ref('');
const searchResults = ref([]);
const loading = ref(false);

// 科室数据
const departments = ref({})

const getCategoryList = async () => {
  const res = await getCategoryListApi();
  // departmentCategories.value = res.data.categories;
  departments.value = res.data.departments;
}
onMounted(() => getCategoryList())

// 搜索方法
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }
  loading.value = true;
  
  try {
    const params = {
      searchKeyword: searchKeyword.value.trim(),
      page: 1,
      pageSize: 10
    };
    
    const res = await getDoctorListApi(params);
    searchResults.value = res.data.records || [];
    
    if (searchResults.value.length === 0) {
      ElMessage.info('未找到相关医生信息');
    }
  } catch (error) {
    ElMessage.error('搜索失败，请稍后重试');
    searchResults.value = [];
  } finally {
    loading.value = false;
  }
};

// 清空搜索
const clearSearch = () => {
  searchKeyword.value = '';
  selectedDepartment.value = '';
  selectedTitle.value = '';
  searchResults.value = [];
};

</script>

<template>
    <div class="search-container">
      <div class="search-header">
        <h1>医生搜索</h1>
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="请输入医生姓名或科室名称"
            class="search-input"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
  
      <div class="search-results">
        <div v-if="loading" class="loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          搜索中...
        </div>
        
        <div v-else-if="searchKeyword && searchResults.length === 0" class="no-results">
          <el-empty description="未找到相关医生信息">
            <el-button type="primary" @click="clearSearch">重新搜索</el-button>
          </el-empty>
        </div>
        
        <div v-else-if="searchResults.length > 0" class="results-list">
          <div class="results-header">
            <span class="results-count">找到 {{ searchResults.length }} 位医生</span>
          </div>
          
          <div class="doctor-list">
            <DoctorCard v-for="doctor in searchResults" :doctors="doctor" />
          </div>
        </div>
        
        <div v-else class="search-tips">
          <div class="tips-content">
            <el-icon size="48" color="#409EFF"><Search /></el-icon>
            <h3>搜索医生信息</h3>
            <p>请输入医生姓名或科室名称进行搜索</p>
            <div class="search-examples">
              <span class="example-tag">例如：张医生</span>
              <span class="example-tag">心血管内科</span>
              <span class="example-tag">骨科</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
<style scoped>
.search-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-header {
  text-align: center;
  margin-bottom: 30px;
}

.search-header h1 {
  color: #333;
  margin-bottom: 20px;
  font-size: 28px;
}

.search-box {
  max-width: 600px;
  margin: 0 auto;
}

.search-input {
  font-size: 16px;
}
.search-results {
  min-height: 400px;
}

.loading {
  text-align: center;
  padding: 50px;
  font-size: 16px;
  color: #666;
}

.no-results {
  text-align: center;
  padding: 50px;
}

.results-list {
  margin-top: 20px;
}

.results-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e8e8e8;
}

.results-count {
  font-size: 16px;
  color: #666;
  font-weight: bold;
}

.doctor-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-tips {
  text-align: center;
  padding: 80px 20px;
}

.tips-content h3 {
  margin: 20px 0 10px;
  color: #333;
  font-size: 24px;
}

.tips-content p {
  color: #666;
  font-size: 16px;
  margin-bottom: 20px;
}

.search-examples {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.example-tag {
  background-color: #e6f7ff;
  color: #1890ff;
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.example-tag:hover {
  background-color: #1890ff;
  color: white;
}
</style>