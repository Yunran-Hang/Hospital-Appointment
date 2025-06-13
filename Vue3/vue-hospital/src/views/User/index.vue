<script setup>
import { ref,onMounted,watch } from 'vue';
import { useRoute } from 'vue-router';
// 默认头像
import avatar from '@/assets/images/default.png';
import { useUserStore } from '@/stores/userStore';

const userStore = useUserStore();
const route = useRoute();

// 当前激活的菜单项
const activeMenu = ref('/userInfo');

// 根据当前路由路径设置激活的菜单项
const updateActiveMenu = () => {
  const path = route.path;
  if (path === '/userInfo') {
    activeMenu.value = '/userInfo';
  } else if (path === '/userInfo/avatar') {
    activeMenu.value = '/userInfo/avatar';
  } else if (path === '/userInfo/resetPassword') {
    activeMenu.value = '/userInfo/resetPassword';
  }
};

// 初始化时设置激活菜单
onMounted(() => {
  updateActiveMenu();
});

// 监听路由变化
watch(() => route.path, () => {
  updateActiveMenu();
});
</script>

<template>
    <el-container class="layout-container">
        <!-- 左侧菜单-->
        <el-aside width="240px">
            <div class="user-sidebar">
                <div class="user-profile">
                    <div class="avatar-container">
                        <img :src="userStore.userInfo.avatar?userStore.userInfo.avatar:avatar" alt="用户头像" class="user-avatar" />
                    </div>
                    <h3 class="welcome-text">个人中心</h3>
                </div>
                
                <div class="menu-container">
                    <div 
                        class="menu-item" 
                        :class="{ active: activeMenu === '/userInfo' }"
                        @click="activeMenu = '/userInfo'; $router.push('/userInfo')"
                    >
                        <el-icon><Setting /></el-icon>
                        <span>基本资料</span>
                    </div>
                    
                    <div 
                        class="menu-item" 
                        :class="{ active: activeMenu === '/userInfo/avatar' }"
                        @click="activeMenu = '/userInfo/avatar'; $router.push('/userInfo/avatar')"
                    >
                        <el-icon><Avatar /></el-icon>
                        <span>更换头像</span>
                    </div>
                    
                    <div 
                        class="menu-item" 
                        :class="{ active: activeMenu === '/userInfo/resetPassword' }"
                        @click="activeMenu = '/userInfo/resetPassword'; $router.push('/userInfo/resetPassword')"
                    >
                        <el-icon><Lock /></el-icon>
                        <span>重置密码</span>
                    </div>
                </div>
            </div>
        </el-aside>
        
        <!-- 右侧主区域 -->
        <el-container>
            <!-- 中间区域 -->
            <el-main>
                <router-view></router-view>
            </el-main>
        </el-container>
    </el-container>
</template>

<style lang="scss" scoped>
.layout-container {
    height: 100vh;
    background-color: #f8f9fa;

    .el-aside {
        background-color: #ffffff;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        transition: all 0.3s ease;
    }

    .user-sidebar {
        height: 100%;
        display: flex;
        flex-direction: column;
        
        .user-profile {
            padding: 30px 0;
            text-align: center;
            border-bottom: 1px solid rgba(0, 0, 0, 0.05);
            
            .avatar-container {
                width: 100px;
                height: 100px;
                margin: 0 auto 15px;
                border-radius: 50%;
                overflow: hidden;
                border: 3px solid #5dade2; /* 使用项目中定义的浅蓝色 */
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                
                .user-avatar {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                }
            }
            
            .welcome-text {
                font-size: 18px;
                color: #3498db; /* 使用项目中定义的主色 */
                font-weight: 500;
                margin: 0;
            }
        }
        
        .menu-container {
            flex: 1;
            padding: 20px 0;
            
            .menu-item {
                display: flex;
                align-items: center;
                padding: 16px 24px;
                margin: 8px 16px;
                border-radius: 8px;
                cursor: pointer;
                transition: all 0.3s ease;
                
                .el-icon {
                    font-size: 18px;
                    margin-right: 12px;
                    color: #666666; /* 使用项目中定义的次要文本色 */
                }
                
                span {
                    font-size: 16px;
                    color: #666666; /* 使用项目中定义的次要文本色 */
                }
                
                &:hover {
                    background-color: rgba(52, 152, 219, 0.05); /* 使用项目中定义的主色透明版 */
                    
                    .el-icon, span {
                        color: #3498db; /* 使用项目中定义的主色 */
                    }
                }
                
                &.active {
                    background-color: rgba(52, 152, 219, 0.1); /* 使用项目中定义的主色透明版 */
                    
                    .el-icon, span {
                        color: #3498db; /* 使用项目中定义的主色 */
                        font-weight: 500;
                    }
                }
            }
        }
    }

    .el-main {
        padding: 20px;
    }
}
</style>