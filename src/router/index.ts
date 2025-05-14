import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import { useUserStore } from '@/stores/counter'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/server-test',
      name: 'serverTest',
      component: () => import('@/views/ServerTestView.vue')
    },
    {
      path: '/',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/HomeView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'timetable',
          name: 'timetable',
          component: () => import('@/views/TimetableView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/profile/UserProfile.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'account/create',
          name: 'createAccount',
          component: () => import('@/views/account/CreateAccount.vue'),
          meta: { requiresAuth: true, requiresAdmin: true }
        },
        {
          path: 'account/list',
          name: 'listAccounts',
          component: () => import('@/views/account/ListAccounts.vue'),
          meta: { requiresAuth: true, requiresAdmin: true }
        },
        {
          path: 'account/edit/:id',
          name: 'editAccount',
          component: () => import('@/views/account/CreateAccount.vue'), // 复用创建组件
          meta: { requiresAuth: true, requiresAdmin: true },
          props: true
        },
        // 学生账号管理相关路由
        {
          path: 'account/student-create',
          name: 'createStudentAccount',
          component: () => import('@/views/account/CreateStudentAccount.vue'),
          meta: { requiresAuth: true, requiresAdmin: true }
        },
        {
          path: 'account/student-list',
          name: 'listStudentAccounts',
          component: () => import('@/views/account/ListStudentAccounts.vue'),
          meta: { requiresAuth: true, requiresAdmin: true }
        },
        {
          path: 'account/student-edit/:id',
          name: 'editStudentAccount',
          component: () => import('@/views/account/CreateStudentAccount.vue'), // 复用创建组件
          meta: { requiresAuth: true, requiresAdmin: true },
          props: true
        },
        // 课程资源管理相关路由
        {
          path: 'resources',
          name: 'resources',
          component: () => import('@/views/resources/ResourceList.vue'),
          meta: { requiresAuth: true }
        },
        // 调课申请相关路由
        {
          path: 'schedule/change',
          name: 'teacherScheduleChange',
          component: () => import('@/views/schedule/TeacherScheduleChange.vue'),
          meta: { requiresAuth: true, requiresTeacher: true }
        },
        // 调课管理相关路由
        {
          path: 'schedule/management',
          name: 'adminScheduleChange',
          component: () => import('@/views/schedule/AdminScheduleChange.vue'),
          meta: { requiresAuth: true, requiresAdmin: true }
        },
        // 学校管理相关路由
        {
          path: 'school/building',
          name: 'School',
          component: () => import('@/views/school/BuildingManagement.vue'),
          meta: { title: '校区管理', requiresAuth: true }
        },
        {
          path: 'school/class',
          name: 'ClassManagement',
          component: () => import('@/views/school/ClassManagement.vue'),
          meta: { title: '班级管理', requiresAuth: true }
        },
        {
          path: 'school/course',
          name: 'CourseManagement',
          component: () => import('@/views/school/CourseManagement.vue'),
          meta: { title: '排课管理', requiresAuth: true }
        },
        {
          path: 'school/major',
          name: 'MajorManagement',
          component: () => import('@/views/school/MajorManagement.vue'),
          meta: { title: '专业管理', requiresAuth: true }
        },
        {
          path: 'school/major-direction',
          name: 'MajorDirectionManagement',
          component: () => import('@/views/school/MajorDirectionManagement.vue'),
          meta: { title: '专业方向管理', requiresAuth: true }
        },
        {
          path: 'school/course-library',
          name: 'CourseLibrary',
          component: () => import('@/views/school/CourseLibrary.vue'),
          meta: { title: '课程库', requiresAuth: true }
        }
      ]
    }
  ]
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 初始化用户存储
  const userStore = useUserStore()
  userStore.loadUserFromStorage()

  const isLoggedIn = userStore.isLoggedIn

  // 如果需要认证且没有登录
  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
  }
  // 如果已登录且要去登录页
  else if (isLoggedIn && to.path === '/login') {
    next('/')
  }
  else {
    next()
  }
})

export default router
