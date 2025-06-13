<script setup>
import DoctorCard from './DoctorCard.vue';
import { ref, computed, onMounted } from 'vue';
import { getCategoryListApi } from '@/apis/category';
import { getDoctorListApi } from '@/apis/doctor';

// 响应式状态
const selectedDate = ref(''); // 日期
const selectedCategory = ref(1); // 一级分类
const selectedDepartment = ref(''); // 二级分类
const selectedTimePeriod = ref(0); // 预约时间段：0全天，1上午，2下午
const availableDates = ref([]);

// 医生数据
const doctors = ref([]);
const params = computed(() => {
    return {
      date: selectedDate.value,
      departmentId: selectedDepartment.value,
      period: selectedTimePeriod.value,
      page: 1,
      pageSize: 15
    };
});
// 查询医生信息
const getDoctorList = async () => {
    const res = await getDoctorListApi(params.value);
    doctors.value = res.data.records;
    if (doctors.value.length === 0){
        disabled.value = true;
    }
}
// 加载更多
const disabled = ref(false)
const load = async () => {
    // console.log('加载更多数据咯...');
    // 获取下一页的数据
    params.value.page += 1;
    const res = await getDoctorListApi(params.value);
    doctors.value = [...doctors.value, ...res.data.records];
    // 全部加载完了，停止监听
    if (res.data.records.length === 0){
        disabled.value = true;
    }
}

// 获得所有科室分类
const departmentCategories = ref([]) // 一级科室分类数据
const departments = ref([]) // 科室数据
// 获取科室分类列表数据
const getCategoryList = async () => {
    const res = await getCategoryListApi();
    departmentCategories.value = res.data.categories;
    departments.value = res.data.departments;
}
onMounted(() => {
    availableDates.value = generateAvailableDates();
    getCategoryList();
    getDoctorList();
})

// 计算属性：根据选中的分类筛选科室
const filteredDepartments = computed(() => {
    return departments.value.filter(dept => dept.categoryId === selectedCategory.value);
});

// 生成可用日期列表
const generateAvailableDates = () => {
  const dates = [];
  const today = new Date();
  for (let i = 0; i < 14; i++) {
    const date = new Date(today);
    date.setDate(today.getDate() + i);
    
    const value = formatDateValue(date);
    const day = getDayOfWeek(date);
    
    // 格式化为 MM·DD 的形式
    const formattedDate = `${(date.getMonth() + 1).toString().padStart(2, '0')}·${date.getDate().toString().padStart(2, '0')}`;
    
    // 为今天添加(今)标记，为明天添加(明)标记
    let dayText = day;
    if (i === 0) {
      dayText = `${day}(今)`;
    } else if (i === 1) {
      dayText = `${day}(明)`;
    }
    
    dates.push({ 
      value, 
      day: dayText, 
      text: formattedDate 
    });
  }
  return dates;
}

// 方法：格式化日期值
const formatDateValue = (date) => {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 方法：获取星期几
const getDayOfWeek = (date) => {
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return days[date.getDay()];
}

// 响应式状态
const handleDateSelect = (dateValue) => {
  selectedDate.value = dateValue;
  changeQuery();
  // console.log('选择的日期:', dateValue ? dateValue : '所有日期');
}

// 方法：处理科室分类选择
const handleCategorySelect = (categoryId) => {
  selectedCategory.value = categoryId;
  selectedDepartment.value = ''; // 重置科室选择
}

// 方法：处理科室选择
const handleDepartmentSelect = (deptId) => {
  selectedDepartment.value = deptId;
  changeQuery();
}

const handleTimePeriodSelect = (period) => {
  selectedTimePeriod.value = period;
  changeQuery();
  console.log('Selected time period:', period ? period : '全天');
}
const changeQuery = () => {
    params.value.page = 1; 
    doctors.value = []; // 清空医生列表
    disabled.value = false; // 重置加载下一页状态
    getDoctorList();
}

</script>

<template>
    <div class="filter-section">
      <div class="date-filter">
        <h3>选择预约日期</h3>
        <div class="date-list">
          <!-- 添加"所有日期"选项 -->
          <div class="date-item" :class="{active: selectedDate === ''}" @click="handleDateSelect('')">
            <div class="date-day">全部</div>
            <div class="date-text">所有日期</div>
          </div>
          
          <div v-for="date in availableDates" :key="date.value" class="date-item" :class="{active:selectedDate === date.value}"
                @click="handleDateSelect(date.value)">
            <div class="date-day">{{ date.day }}</div>
            <div class="date-text">{{ date.text }}</div>
          </div>
        </div>
      </div>

      <!-- 就诊时间选择: 全天/上午/下午 -->
      <div class="time-period-filter">
        <h3>选择就诊时间</h3>
        <div class="time-period-options">
          <div class="time-period-item" :class="{active: selectedTimePeriod === 0}" @click="handleTimePeriodSelect(0)">
            全天
          </div>
          <div class="time-period-item" :class="{active: selectedTimePeriod === 1}" @click="handleTimePeriodSelect(1)">
            <div class="period-text">上午</div>
          </div>
          <div class="time-period-item" :class="{active: selectedTimePeriod === 2}" @click="handleTimePeriodSelect(2)">
            <div class="period-text">下午</div>
          </div>
        </div>
      </div>

      <!-- 科室分类 -->
      <div class="department-filter">
        <h3>选择科室</h3>
        <div class="department-categories">
          <!-- 一级科室列表 -->
          <div class="department-category-list">
            <div 
              v-for="category in departmentCategories" 
              :key="category.id"
              :class="['department-category', selectedCategory === category.id ? 'active' : '']"
              @click="handleCategorySelect(category.id)"
            >
              <div>{{ category.name }}</div>
            </div>
          </div>
          
          <!-- 二级科室列表 -->
          <div class="department-list">
            <div 
              class="department-item"
              :class="{active: selectedDepartment === ''}" 
              @click="handleDepartmentSelect('')"
            >
              全部科室
            </div>
            <div v-for="dept in filteredDepartments" :key="dept.id" 
                :class="['department-item', selectedDepartment === dept.id ? 'active' : '']" @click="handleDepartmentSelect(dept.id)"
            >
              {{ dept.name }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="doctor-list-section">
      <h2>医生列表</h2>
      <div class="doctor-list" v-infinite-scroll="load" :infinite-scroll-disabled="disabled">
        <DoctorCard style="margin-left: auto;margin-top: 20px;" v-for="doctor in doctors" :doctors="doctor"/>
        <div v-if="doctors.length === 0" class="no-doctors">
          当前条件下没有可预约的医生，请调整筛选条件
        </div>
      </div>
    </div>


</template>

<style scoped lang="scss">
.time-period-options {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin: 15px 10px 20px 0px;
}

.time-period-item {
    padding: 12px 24px;
    background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
    border: 2px solid #e8e8e8;
    cursor: pointer;
    transition: all 0.3s ease;
    flex-shrink: 0;
    border-radius: 8px;
    font-weight: 500;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.time-period-item:hover {
    background: linear-gradient(135deg, #40a9ff 0%, #1890ff 100%);
    border-color: #1890ff;
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.time-period-item.active {
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    border-color: #096dd9;
    color: white;
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.4);
}

.filter-section {
    background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
    padding: 24px;
    border-radius: 12px;
    margin-bottom: 24px;
    box-shadow: 0 4px 16px rgba(0,0,0,0.08);
    border: 1px solid #e8e8e8;
}

.date-filter, .department-filter, .time-period-filter {
    margin-bottom: 24px;
}

.date-filter h3, .department-filter h3, .time-period-filter h3 {
    margin-top: 0;
    margin-bottom: 15px;
    font-size: 18px;
    font-weight: 600;
    color: #262626;
    position: relative;
    padding-left: 12px;
}

.date-filter h3::before, .department-filter h3::before, .time-period-filter h3::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 18px;
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    border-radius: 2px;
}

.date-list {
    display: flex;
    overflow-x: auto;
    gap: 12px;
    padding: 8px 0 12px 0;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: thin;
}

.date-list::-webkit-scrollbar {
    height: 6px;
}

.date-list::-webkit-scrollbar-thumb {
    background: linear-gradient(90deg, #d9d9d9 0%, #bfbfbf 100%);
    border-radius: 3px;
}

.date-item {
    min-width: 100px;
    padding: 12px;
    background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
    border: 2px solid #e8e8e8;
    border-radius: 8px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s ease;
    flex-shrink: 0;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.date-item:hover {
    background: linear-gradient(135deg, #40a9ff 0%, #1890ff 100%);
    border-color: #1890ff;
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(24, 144, 255, 0.3);
}

.date-item.active {
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    border-color: #096dd9;
    box-shadow: 0 6px 16px rgba(24, 144, 255, 0.4);
}

.date-item.active .date-day,
.date-item.active .date-text,
.date-item:hover .date-day,
.date-item:hover .date-text {
    color: white;
}

.date-day {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 2px;
}

.date-text {
    font-size: 14px;
    color: #666;
}

.doctor-list-section h2 {
    font-size: 24px;
    margin-bottom: 20px;
    font-weight: 600;
    color: #262626;
    position: relative;
    padding-left: 16px;
}

.doctor-list-section h2::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 6px;
    height: 24px;
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    border-radius: 3px;
}

.doctor-list {
    display: block;
    width: 100%;
}

.no-doctors {
    grid-column: 1 / -1;
    text-align: center;
    padding: 40px;
    background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
    border-radius: 12px;
    color: #666;
    border: 2px dashed #d9d9d9;
    font-size: 16px;
}

/* 科室分类样式 */
.department-categories {
    display: flex;
    flex-direction: column;
    gap: 18px;
}

.department-category-list {
    display: flex;
    overflow-x: auto;
    gap: 12px;
    padding: 8px 0 12px 0;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: thin;
}

.department-category-list::-webkit-scrollbar {
    height: 6px;
}

.department-category-list::-webkit-scrollbar-thumb {
    background: linear-gradient(90deg, #d9d9d9 0%, #bfbfbf 100%);
    border-radius: 3px;
}

.department-category {
    padding: 12px 24px;
    background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
    border: 2px solid #e8e8e8;
    border-radius: 24px;
    cursor: pointer;
    transition: all 0.3s ease;
    flex-shrink: 0;
    font-weight: 600;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.department-category:hover {
    background: linear-gradient(135deg, #40a9ff 0%, #1890ff 100%);
    border-color: #1890ff;
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(24, 144, 255, 0.3);
}

.department-category.active {
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    border-color: #096dd9;
    color: white;
    box-shadow: 0 6px 16px rgba(24, 144, 255, 0.4);
}

.department-list {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    padding-top: 15px;
    border-top: 2px dashed #e8e8e8;
}

.department-item {
    padding: 10px 18px;
    background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
    border: 2px solid #e8e8e8;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
    font-weight: 500;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.department-item:hover {
    background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
    border-color: #1890ff;
    color: #1890ff;
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(24, 144, 255, 0.2);
}

.department-item.active {
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    border-color: #096dd9;
    color: white;
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.4);
}
</style>