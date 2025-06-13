// 管理用户数据相关
import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore('user', () => {
    // 定义管理用户数据state
    const userInfo = ref({})

    const setUserInfo = (newInfo) => {
      userInfo.value = newInfo
    }

    const removeInfo = () => {
      userInfo.value = {}
    }

    return {
      userInfo,
      setUserInfo,
      removeInfo
    }
  
  },{
    persist: true
  })