import request from '@/utils/request'
import { ServerDiagnosticTools } from '@/utils/debugTools'

export interface LoginParams {
    username: string // adminId, studentId, or teacherId
    password: string
    userType: string
}

export interface UserInfo {
    userId: string
    name: string
    role: string
    userType: string
    isActive: boolean
    email?: string
    departmentId?: string
    classId?: string
    majorId?: string
    gender?: string
    title?: string
}

export interface LoginResponse {
    token: string
    username: string
    role: string
    message: string
    loginData: {
        token: string
        role: string
    }
    userInfo: {
        id: string
        username: string
        name: string
        role: string
        classId?: string
        majorId?: string
    }
}

// 读取环境变量
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8080';
const API_TIMEOUT = parseInt(import.meta.env.VITE_API_TIMEOUT as string || '5000');
const API_DEBUG = import.meta.env.VITE_API_DEBUG === 'true';

// 登录API
export const login = async (data: LoginParams): Promise<LoginResponse> => {
    try {
        if (API_DEBUG) console.log('尝试登录: /api/user/login', data);

        const response = await request({
            url: '/api/user/login',
            method: 'post',
            data,
            timeout: API_TIMEOUT
        });

        if (API_DEBUG) console.log('登录成功:', response);
        return response as unknown as LoginResponse;
    } catch (error) {
        if (API_DEBUG) {
            console.error('登录失败:', error);
            if (error instanceof Error && error.message.includes('CORS')) {
                console.error('CORS错误，请检查服务器配置');
                throw new Error('服务器连接失败，请检查网络设置');
            }
        }
        throw error;
    }
}

// 备选登录API（使用不同的端点）
export const loginAlternative = async (data: LoginParams): Promise<LoginResponse> => {
    try {
        if (API_DEBUG) console.log('尝试备选登录: /api/user/login', data);

        const response = await request({
            url: '/api/user/login',
            method: 'post',
            data,
            timeout: API_TIMEOUT
        });

        if (API_DEBUG) console.log('备选登录成功:', response);
        return response as unknown as LoginResponse;
    } catch (error) {
        if (API_DEBUG) {
            console.error('备选登录失败:', error);
            if (error instanceof Error && error.message.includes('CORS')) {
                console.error('CORS错误，请检查服务器配置');
                throw new Error('服务器连接失败，请检查网络设置');
            }
        }
        throw error;
    }
}

// 直接URL登录API（使用URL参数）
export const loginAlternative2 = async (data: LoginParams): Promise<LoginResponse> => {
    try {
        if (API_DEBUG) console.log('尝试直接URL登录', data);

        const params = new URLSearchParams({
            username: data.username,
            password: data.password,
            userType: data.userType
        });

        const response = await request({
            url: `/api/auth/login/direct?${params.toString()}`,
            method: 'get',
            timeout: API_TIMEOUT
        });

        if (API_DEBUG) console.log('直接URL登录成功:', response);
        return response as unknown as LoginResponse;
    } catch (error) {
        if (API_DEBUG) {
            console.error('直接URL登录失败:', error);
            if (error instanceof Error && error.message.includes('CORS')) {
                console.error('CORS错误，请检查服务器配置');
                throw new Error('服务器连接失败，请检查网络设置');
            }
        }
        throw error;
    }
}

// 检查服务器连接
export const checkServerConnection = async (): Promise<boolean> => {
    try {
        // 使用更高级的服务器诊断工具
        const result = await ServerDiagnosticTools.checkConnection(API_BASE_URL, API_TIMEOUT);
        if (API_DEBUG) {
            console.log('服务器连接检查结果:', result);
        }
        return result.isOnline;
    } catch (error) {
        console.error('服务器连接检查失败:', error);
        return false;
    }
}

// 获取完整服务器诊断报告
export interface DiagnosticReport {
    baseUrlStatus: {
        isOnline: boolean;
        latency?: number;
        message: string;
        details?: unknown;
    };
    apiEndpointsStatus: Record<string, {
        isOnline: boolean;
        latency?: number;
        message: string;
    }>;
    corsStatus: {
        optionsSupported: boolean;
        corsHeadersPresent: boolean;
        message: string;
        details?: unknown;
    };
    environmentVariables: {
        apiBaseUrl: string;
        apiTimeout: string;
        apiRetryCount: string;
        debug: boolean | string;
    };
    browserInfo: {
        userAgent: string;
        language: string;
        cookiesEnabled: boolean;
        onLine: boolean;
        platform: string;
    };
}

export const getServerDiagnostics = async (): Promise<DiagnosticReport> => {
    return ServerDiagnosticTools.getFullDiagnosticReport() as Promise<DiagnosticReport>;
}
