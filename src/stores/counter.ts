import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export interface UserInfo {
  userId: string
  name: string
  role: string // 'TEACHER', 'ADMIN', 'SUPER_ADMIN'
  userType: string // 'ADMIN', 'TEACHER', 'STUDENT'
  isActive: boolean
  email?: string
  departmentId?: string
  classId?: string
  majorId?: string
  gender?: string
  title?: string
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const token = ref<string | null>(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN' || userInfo.value?.role === 'SUPER_ADMIN')
  const isTeacher = computed(() => userInfo.value?.role === 'TEACHER')
  const isStudent = computed(() => userInfo.value?.role === 'STUDENT')
  const userName = computed(() => userInfo.value?.name || '')
  const isSuperAdmin = computed(() => userInfo.value?.role === 'SUPER_ADMIN')

  // 加载本地存储的用户信息
  function loadUserFromStorage() {
    console.log('开始从本地存储加载用户信息');
    const storedToken = localStorage.getItem('token')
    const storedUserInfo = localStorage.getItem('userInfo')

    if (storedToken) {
      token.value = storedToken
      console.log('从本地存储加载到token');
    }

    if (storedUserInfo) {
      try {
        const parsedUserInfo = JSON.parse(storedUserInfo)
        console.log('从本地存储加载到的用户信息:', parsedUserInfo);
        userInfo.value = parsedUserInfo
      } catch (error) {
        console.error('解析用户信息失败', error)
      }
    }
  }

  // 设置用户信息
  function setUserInfo(user: UserInfo, userToken: string) {
    console.log('设置新的用户信息:', user);
    userInfo.value = user
    token.value = userToken

    // 保存到本地存储
    localStorage.setItem('token', userToken)
    localStorage.setItem('userInfo', JSON.stringify(user))
    console.log('用户信息已保存到本地存储');
  }

  // 退出登录
  function logout() {
    userInfo.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    userInfo,
    token,
    isLoggedIn,
    isAdmin,
    isTeacher,
    isStudent,
    userName,
    isSuperAdmin,
    loadUserFromStorage,
    setUserInfo,
    logout
  }
})
