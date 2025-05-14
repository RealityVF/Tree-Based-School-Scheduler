import axios from 'axios'
import type { AxiosInstance, AxiosResponse, AxiosError, InternalAxiosRequestConfig } from 'axios'
import { useUserStore } from '@/stores/counter'
import router from '@/router'
import { ElMessage } from 'element-plus'

// 读取环境变量
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8080'
const API_TIMEOUT = parseInt(import.meta.env.VITE_API_TIMEOUT as string || '5000')
const API_DEBUG = import.meta.env.VITE_API_DEBUG === 'true'

// 获取基础URL
function getBaseURL(): string {
    return API_BASE_URL
}

const service: AxiosInstance = axios.create({
    baseURL: getBaseURL(),
    timeout: API_TIMEOUT,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
    },
    withCredentials: true
})

// 请求拦截器
service.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        // 强制开启调试日志
        console.log('=== 请求开始 ===')
        console.log('请求URL:', `${config.baseURL}${config.url}`)
        console.log('请求方法:', config.method)
        console.log('请求头:', config.headers)
        console.log('请求参数:', config.params)
        console.log('请求数据:', config.data)

        // 从 localStorage 获取 token
        const token = localStorage.getItem('token')
        const userInfo = localStorage.getItem('userInfo')
        console.log('当前用户信息:', userInfo)

        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
            console.log('添加认证头:', `Bearer ${token}`)
            try {
                const parsedUserInfo = JSON.parse(userInfo || '{}')
                console.log('当前用户详细信息:', {
                    role: parsedUserInfo.role,
                    permissions: parsedUserInfo.permissions,
                    username: parsedUserInfo.username,
                    userId: parsedUserInfo.userId
                })
            } catch (e) {
                console.error('解析用户信息失败:', e)
            }
        } else {
            console.warn('No token found in localStorage')
            // 如果是需要认证的请求，且没有token，直接跳转到登录页
            if (!config.url?.startsWith('/api/auth/') && config.url !== '/api/user/login') {
                router.push('/login')
                throw new Error('请先登录')
            }
        }

        return config
    },
    (error: AxiosError) => {
        console.error('请求错误:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    (response: AxiosResponse) => {
        console.log('API响应原始数据:', response.data);
        const res = response.data;

        // Log response details
        console.log('=== 响应开始 ===');
        console.log('响应状态码:', response.status);
        console.log('响应头:', response.headers);
        console.log('响应数据:', res);
        console.log('请求配置:', response.config);

        if (Array.isArray(res)) {
            console.log('处理数组响应:', res);
            return {
                success: true,
                message: '获取成功',
                data: res
            };
        }

        if (typeof res === 'object' && res !== null) {
            console.log('处理对象响应:', res);
            if ('success' in res) {
                return res;
            }
            
            if ('list' in res && 'total' in res) {
                const result = {
                    success: true,
                    message: '获取成功',
                    data: {
                        list: res.list,
                        total: res.total,
                        page: res.page,
                        size: res.size
                    }
                };
                console.log('处理分页响应:', result);
                return result;
            }
        }

        console.log('返回原始响应:', res);
        return res;
    },
    (error: AxiosError<{message?: string}>) => {
        console.error('API请求错误:', error);
        if (API_DEBUG) {
            console.error('响应错误:', error)

            if (error.response) {
                console.error('错误状态码:', error.response.status)
                console.error('错误响应头:', error.response.headers)
                console.error('错误响应数据:', error.response.data)
            } else if (error.request) {
                console.error('请求已发送但未收到响应')
                console.error('请求详情:', error.request)
            } else {
                console.error('请求配置错误:', error.message)
            }
        }

        // 处理401错误
        if (error.response?.status === 401) {
            const userStore = useUserStore()
            userStore.logout() // 使用 store 的 logout 方法
            router.push('/login')
            return Promise.reject(new Error('登录已过期，请重新登录'))
        }

        // 处理403错误
        if (error.response?.status === 403) {
            // 检查是否是因为没有token导致的
            const token = localStorage.getItem('token')
            if (!token) {
                router.push('/login')
                return Promise.reject(new Error('请先登录后再访问此功能'))
            }
            return Promise.reject(new Error('您没有权限访问此功能'))
        }

        // 处理404错误
        if (error.response?.status === 404) {
            return Promise.reject(new Error('请求的功能暂未实现，请稍后再试'))
        }

        // 处理500错误
        if (error.response?.status && error.response.status >= 500) {
            return Promise.reject(new Error('服务器出现错误，请稍后再试或联系管理员'))
        }

        // 处理网络错误
        if (error.code === 'ERR_NETWORK') {
            return Promise.reject(new Error('无法连接到服务器，请检查您的网络连接'))
        }

        // 处理超时错误
        if (error.code === 'ECONNABORTED') {
            return Promise.reject(new Error('服务器响应超时，请稍后再试'))
        }

        // 如果是后端返回的错误消息，直接使用
        if (error.response?.data?.message) {
            return Promise.reject(new Error(error.response.data.message))
        }

        // 其他错误使用通用错误消息
        return Promise.reject(new Error('操作失败，请稍后重试'))
    }
)

// 提供修改基础URL的方法
export const setBaseURL = (url: string) => {
    service.defaults.baseURL = url
}

// 导出 request 实例
export const request = service

// 添加默认导出
export default service
