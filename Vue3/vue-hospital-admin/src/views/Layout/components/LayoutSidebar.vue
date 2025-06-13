<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()


// 当前激活的菜单项
const activeMenu = ref(route.path)

// 监听路由变化，更新菜单选中状态
onMounted(() => {
  activeMenu.value = route.path
})

// 菜单选择
const handleMenuSelect = (key) => {
  activeMenu.value = key
  router.push(key)
}

</script>

<template>
    <!-- 侧边栏 -->
    <el-aside width="240px" class="sidebar">
      <div class="logo-container">
        <div class="logo-wrapper">
          <img src="@/assets/images/logo.png" alt="医院logo" class="logo">
          <div class="hospital-info">
            <h3 class="hospital-name">宁波第二医院</h3>
            <p class="hospital-subtitle">后台管理系统</p>
          </div>
        </div>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="transparent"
        text-color="rgba(255,255,255,0.8)"
        active-text-color="#ffffff"
        @select="handleMenuSelect"
      >
        <!-- 医生管理 -->
        <el-sub-menu index="doctor" class="sub-menu">
          <template #title>
            <el-icon class="menu-icon"><User /></el-icon>
            <span class="menu-title">医生管理</span>
          </template>
          <!-- 医生信息 -->
          <el-menu-item index="/doctor-management" class="sub-menu-item">
            <el-icon class="menu-icon"><UserFilled /></el-icon>
            <template #title>
              <span class="menu-title">医生信息</span>
            </template>
          </el-menu-item>
          <!-- 医生排班 -->
          <el-menu-item index="/doctor-schedule" class="sub-menu-item">
            <el-icon class="menu-icon"><Calendar /></el-icon>
            <template #title>
              <span class="menu-title">医生排班</span>
            </template>
          </el-menu-item>
        </el-sub-menu>
        <!-- 用户管理 -->
        <el-menu-item index="/user-management" class="menu-item">
          <el-icon class="menu-icon"><Service /></el-icon>
          <template #title>
            <span class="menu-title">用户管理</span>
          </template>
        </el-menu-item>
        <!-- 预约记录 -->
        <el-menu-item index="/appointment-records" class="menu-item">
          <el-icon class="menu-icon"><List /></el-icon>
          <template #title>
            <span class="menu-title">预约记录</span>
          </template>
        </el-menu-item>
        <!-- 操作日志 -->
        <el-menu-item index="/operation-logs" class="menu-item">
          <el-icon class="menu-icon"><Operation /></el-icon>
          <span>操作日志</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
</template>

<style scoped>
.sidebar {
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  overflow: hidden;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  position: relative;
  animation: slideIn 0.5s ease-out;
}
/* 侧边栏加载动画效果 */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, rgba(255,255,255,0.1) 0%, transparent 50%, rgba(255,255,255,0.05) 100%);
  pointer-events: none;
}

.logo-container {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.1);
}

.logo-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: scale(1.1);
}

.hospital-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.hospital-name {
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  white-space: nowrap;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.hospital-subtitle {
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  margin: 0;
  white-space: nowrap;
}

.sidebar-menu {
  border: none;
  height: calc(100vh - 80px);
  background: transparent;
  padding: 10px 0;
}

.sub-menu {
  margin: 4px 12px;
}

.menu-icon {
  font-size: 18px;
  margin-right: 8px;
}

.menu-title {
  font-weight: 500;
  letter-spacing: 0.5px;
}

.menu-item, .sub-menu-item {
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.menu-item:hover, .sub-menu-item:hover {
  background: linear-gradient(135deg, rgba(255,255,255,0.15) 0%, rgba(255,255,255,0.05) 100%) !important;
  transform: translateX(4px);
}

.menu-item.is-active, .sub-menu-item.is-active {
  background: linear-gradient(135deg, #409EFF 0%, #66b3ff 100%) !important;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

</style>