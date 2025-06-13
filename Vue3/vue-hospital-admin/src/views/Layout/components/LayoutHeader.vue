<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import 'element-plus/theme-chalk/el-message.css'
import { ElMessage } from 'element-plus'
import avatar from '@/assets/images/default.png'
import { useUserStore } from '@/stores/userStore'
import { useCryptoStore } from '@/stores/cryptoStore'

const userStore = useUserStore()
const cryptoStore = useCryptoStore()
const router = useRouter()
const route = useRoute()

// 动态面包屑
const breadcrumbItems = computed(() => {
    const routeMap = {
        '/doctor-management': ['后台管理', '医生管理', '医生信息'],
        '/doctor-schedule': ['后台管理', '医生管理', '医生排班'],
        '/appointment-records': ['后台管理', '预约记录'],
        '/operation-logs': ['后台管理', '操作日志'],
        '/user-management': ['后台管理','用户管理']
    }
    return routeMap[route.path] || ['后台管理']
})

// 退出登录
const handleLogout = () => {
    userStore.removeInfo()
    cryptoStore.clearClientKeys()
    ElMessage.success('退出登录成功')
    router.push('/login')
}

// 当前时间
const currentTime = ref(new Date().toLocaleString())

setInterval(() => {
    currentTime.value = new Date().toLocaleString()
}, 1000)

</script>

<template>
    <!-- 顶部导航栏 -->
    <div class="header-left">
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item 
          v-for="(item, index) in breadcrumbItems" 
          :key="index"
          :class="{ 'breadcrumb-active': index === breadcrumbItems.length - 1 }"
        >
          {{ item }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="header-right">
      <div class="time-display">
        <el-icon><Clock /></el-icon>
        <span>{{ currentTime }}</span>
      </div>
      <el-dropdown @command="handleLogout" class="user-dropdown">
        <span class="user-info">
          <el-avatar :size="36" :src="avatar" class="user-avatar" />
          <div class="user-details">
            <span class="username">管理员</span>
            <span class="user-role">系统管理员</span>
          </div>
          <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu class="user-menu">
            <el-dropdown-item command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>


</template>

<style scoped>


.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.breadcrumb {
  font-size: 14px;
}

.breadcrumb-active {
  color: #409EFF;
  font-weight: 600;
}

.time-display {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 13px;
  padding: 6px 12px;
  background: rgba(64, 158, 255, 0.1);
  border-radius: 20px;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 25px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-info:hover {
  background: rgba(64, 158, 255, 0.1);
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.user-avatar {
  border: 2px solid rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.user-info:hover .user-avatar {
  border-color: #409EFF;
}

.user-info:hover .dropdown-icon {
  transform: rotate(180deg);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  color: #333;
  font-size: 14px;
  font-weight: 600;
}

.user-role {
  color: #999;
  font-size: 12px;
}

.dropdown-icon {
  color: #999;
  transition: transform 0.3s ease;
}

.user-menu {
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(0, 0, 0, 0.1);
}

</style>