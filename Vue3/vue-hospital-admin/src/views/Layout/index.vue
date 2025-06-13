<script setup>
import LayoutSidebar from './components/LayoutSidebar.vue'
import LayoutHeader from './components/LayoutHeader.vue'
</script>

<template>
    <div class="layout-container">
      <!-- 侧边栏 -->
      <LayoutSidebar />
      <!-- 主内容区域 -->
      <el-container class="main-container">
        <!-- 顶部导航栏 -->
        <el-header class="header">
          <LayoutHeader />
        </el-header> 
        <!-- 主内容 -->
        <el-main class="main-content">
          <div class="content-wrapper">
            <router-view />
          </div>
        </el-main>
      </el-container>
    </div>
</template>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.main-container {
  flex: 1;
  background: transparent;
}

.main-content {
  padding: 0;
  background: transparent;
  min-height: calc(100vh - 70px);
}

.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: none;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.1);
  height: 70px;
}

.content-wrapper {
  margin: 20px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  min-height: calc(100vh - 130px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}
</style>