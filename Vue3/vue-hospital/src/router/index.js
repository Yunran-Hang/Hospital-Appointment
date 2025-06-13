// createRouter: 创建router实例对象
// createWebHistory: 创建history模式的路由

import { createRouter, createWebHistory } from 'vue-router';
import LayoutView from '@/views/Layout/index.vue';
import LoginView from '@/views/Login/index.vue';
import AppointmentView from '@/views/Appointment/index.vue'
import SearchView from '@/views/Search/index.vue'
import HomeView from '@/views/Home/index.vue'
import UserView from '@/views/User/index.vue'
import UserAvatarView from '@/views/User/components/UserAvatar.vue';
import ResetPasswordView from '@/views/User/components/UserResetPassword.vue';
import UserInfoView from '@/views/User/components/UserInfo.vue';
import MyAppointmentView from '@/views/MyAppointment/index.vue';
import AppointmentConfirmView from '@/views/AppointmentConfirm/index.vue';



const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'layout',
      component: LayoutView,
      children: [
        {
          path: '',
          name: 'home',
          component: HomeView

        },
        {
          path: '/appointment',
          name: 'appointment',
          component: AppointmentView
        },
        {
          path: '/search',
          name: 'search',
          component: SearchView
        },
        {
          path: '/userInfo',
          name:'user',
          component: UserView,
          meta: { requiresAuth: true }, // 添加此行表示该路由需要验证
          children: [
            {
              path: 'avatar',
              name:'userAvatar',
              component: UserAvatarView,
            },
            {
              path: 'resetPassword',
              name:'userResetPassword',
              component: ResetPasswordView,
            },
            {
              path:'',
              name:'userInfo',
              component: UserInfoView,
            }
            
          ]
        },
        {
          path: '/myAppointment',
          name:'myAppointment',
          component: MyAppointmentView,
          meta: { requiresAuth: true }, // 添加此行表示该路由需要验证
        },
        {
          path: '/appointmentConfirm',
          name:'appointmentConfirm',
          component: AppointmentConfirmView,
          meta: { requiresAuth: true }, // 添加此行表示该路由需要验证
        }
        // {
        //   path: 'category/:id',
        //   name: 'category',
        //   component: CategoryView,
        // },

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