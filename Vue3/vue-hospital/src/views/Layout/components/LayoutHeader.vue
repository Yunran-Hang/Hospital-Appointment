<script setup>
import { ref } from 'vue';
import { useUserStore } from '@/stores/userStore';
import { useRouter } from 'vue-router';
import { User, Crop, EditPen, SwitchButton, CaretBottom } from '@element-plus/icons-vue'
import { useCryptoStore } from '@/stores/cryptoStore';
import avatar from '@/assets/images/default.png'

const cryptoStore = useCryptoStore();

// 默认头像图标

const categoryList = ref([
  {
    id: 1,
    name: '预约挂号',
    url: '/appointment',
  },
  {
    id: 2,
    name: '我的预约',
    url: '/myappointment',
  },
  {
    id: 3,
    name: '我的信息',
    url: '/userInfo',
  },
])
const userStore = useUserStore();

const router = useRouter();

// 处理下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'info':
      // 跳转到基本资料页面
      router.push('/userInfo');
      break;
    case 'avatar':
      // 跳转到更换头像页面
      router.push('/userInfo/avatar');
      break;
    case 'resetPassword':
      // 跳转到重置密码页面
      router.push('/userInfo/resetPassword');
      break;
    case 'logout':
      // 退出登录前添加确认框
      ElMessageBox.confirm(
        '确定要退出登录吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
        .then(() => {
          // 用户点击确定，执行退出登录
          userStore.removeInfo();
          cryptoStore.clearKeyCache();
          ElMessage.success('退出登录成功');
          router.push('/login');
        })
        .catch(() => {
          // 用户点击取消，不执行任何操作
          ElMessage.info('已取消退出登录');
        });
      break;
    default:
      break;
  }
};

</script>

<template>
  <header class='app-header'>
    <div class="container">
      <h1 class="logo">
        <RouterLink to="/"></RouterLink>
      </h1>

      <ul class="app-left-nav">
        <li class="home">
          <RouterLink to="/">首页</RouterLink>
        </li>
        <li v-for="item in categoryList" :key="item.id" class="home">
          <RouterLink active-class="active" :to="item.url">{{ item.name }}</RouterLink>
        </li>
      </ul>

      <ul class="app-right-nav">
        <!-- 多模版渲染 区分登录状态和非登录状态 -->
        <!-- 登录状态 -->
        <template v-if="userStore.userInfo.token">
          <el-dropdown placement="bottom-end" @command="handleCommand">
                <span class="el-dropdown__box">
                    <el-avatar :src="userStore.userInfo.avatar?userStore.userInfo.avatar:avatar" />
                    <el-icon>
                        <CaretBottom />
                    </el-icon>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="info" :icon="User">基本资料</el-dropdown-item>
                        <el-dropdown-item command="avatar" :icon="Crop">更换头像</el-dropdown-item>
                        <el-dropdown-item command="resetPassword" :icon="EditPen">重置密码</el-dropdown-item>
                        <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </template>
        <!-- 未登录状态 -->
        <template v-else>
          <li style="margin-right: 20px;"><a href="javascript:;" @click="$router.push('/login')">请先登录</a></li>
          <el-avatar :src="avatar" />
        </template>
      </ul>
    </div>


  </header>
</template>

<style scoped lang='scss'>
li {
      width: auto;
      text-align: center;

      a {
        font-size: 16px;
        line-height: 32px;
        height: 32px;
        display: inline-block;

        &:hover {
          color: $primary-color;
          border-bottom: 1px solid $primary-color;
        }
      }

      .active {
        color: $primary-color;
        border-bottom: 1px solid $primary-color;
      }
    }
.app-header {
  background: #fff;

  .container {
    width: 100%;
    display: flex;
    align-items: center;
    margin: 0;
    padding: 0;
  }

  .logo {
    width: 220px;
    margin-left: 4%;

    a {
      display: block;
      height: 75px;
      width: 100%;
      text-indent: -9999px;
      background: url('@/assets/images/full_logo.png') no-repeat center 18px / contain;
    }
  }

  .app-left-nav {
    display: flex;
    align-items: center;
    gap: 70px;
    margin-left: 8%;
    list-style: none;
  }

  .app-right-nav {
    display: flex;
    align-items: center;
    margin-left: auto;
    margin-right: 4%;
  }
}
</style>
