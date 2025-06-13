import { createApp } from 'vue'
import App from './App.vue'
import router from "./router";
// 引入初始化的样式
import '@/styles/common.scss'
// 引入懒加载指令插件并且注册
import { lazyPlugin } from './directives'
// pinia
import { createPinia } from 'pinia'
const pinia = createPinia()
// pinia持久化
import { createPersistedState} from 'pinia-plugin-persistedstate'
const persist = createPersistedState()
pinia.use(persist)

import * as ElementPlusIconsVue from '@element-plus/icons-vue'


// app
const app = createApp(App)

app.use(pinia)
app.use(router);
app.use(lazyPlugin);
// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
// 全局注册ElConfigProvider组件
app.component('ElConfigProvider', ElConfigProvider)

app.mount('#app')

// 导航守卫
import {useUserStore} from "@/stores/userStore";
import { ElMessage } from 'element-plus';
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const isAuthenticated = userStore.userInfo.token // 假设 isAutheticated 是一个用于判断用户是否登录的方法或属性

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isAuthenticated) {
      ElMessage.warning('请在登录后再进行操作')
      next({ path: '/login', query: { redirect: to.fullPath } }) // 如果未登录则重定向到登录页，并记录原始路径
    } else {
      next() // 已登录，则允许进入
    }
  } else {
    next() // 不需要验证直接进入
  }
})
