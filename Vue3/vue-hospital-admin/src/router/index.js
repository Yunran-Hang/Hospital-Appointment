// createRouter: 创建router实例对象
// createWebHistory: 创建history模式的路由

import { createRouter, createWebHistory } from 'vue-router';
import LayoutView from '@/views/Layout/index.vue';
import LoginView from '@/views/Login/index.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'layout',
      component: LayoutView,
      redirect: '/doctor-management',
      meta: { requiresAuth: true },
      children: [
        {
          path: '/doctor-management',
          name: 'doctor-management',
          component: () => import('@/views/DoctorManagement/index.vue'),
          meta: { title: '医生管理' }
        },
        {
          path: '/doctor-schedule',
          name: 'doctor-schedule',
          component: () => import('@/views/DoctorSchedule/index.vue'),
          meta: { title: '医生排班' }
        },
        {
          path: '/appointment-records',
          name: 'appointment-records',
          component: () => import('@/views/AppointmentRecords/index.vue'),
          meta: { title: '预约记录' }
        },
        {
          path: '/operation-logs',
          name: 'operation-logs',
          component: () => import('@/views/OperationLogs/index.vue'),
          meta: { title: '操作日志' }
        },
        {
          path: '/user-management',
          component: () => import('@/views/UserManagement/index.vue'),
          meta: { title: '用户管理' }
        }
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    }
  ],
  // 路由滚动行为定制
  scrollBehavior () {
    return {
      top: 0
    }
  }
})

export default router