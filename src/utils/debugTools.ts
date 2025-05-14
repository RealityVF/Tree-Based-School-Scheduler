import axios from 'axios';
import type { DiagnosticReport } from '@/api/auth';

/**
 * 服务器连接诊断工具
 */
export class ServerDiagnosticTools {
    /**
     * 检查服务器连接状态
     * @param url 要检查的URL
     * @param timeout 超时时间（毫秒）
     * @returns 连接诊断结果
     */
    static async checkConnection(baseUrl: string, timeout: number): Promise<{ isOnline: boolean; latency?: number }> {
        const startTime = Date.now();
        try {
            const response = await fetch(`${baseUrl}/api/auth/health`, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                },
                mode: 'cors',
                credentials: 'include',
                signal: AbortSignal.timeout(timeout)
            });
            const latency = Date.now() - startTime;
            return { isOnline: response.ok, latency };
        } catch (error) {
            console.error('Connection check failed:', error);
            return { isOnline: false };
        }
    }

    /**
     * 检查多个API端点的可访问性
     * @param baseUrl 基础URL
     * @param endpoints 要检查的端点数组
     * @returns 各个端点的诊断结果
     */
    static async checkApiEndpoints(
        baseUrl: string,
        endpoints: string[]
    ): Promise<{ [endpoint: string]: { isOnline: boolean; latency?: number; message: string } }> {
        const results: { [endpoint: string]: { isOnline: boolean; latency?: number; message: string } } = {};

        for (const endpoint of endpoints) {
            const url = endpoint.startsWith('http') ? endpoint : `${baseUrl}${endpoint}`;
            results[endpoint] = await this.checkConnection(url, 5000);
        }

        return results;
    }

    /**
     * 返回服务器诊断信息的详细报告
     */
    static async getFullDiagnosticReport(): Promise<DiagnosticReport> {
        const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
        const timeout = parseInt(import.meta.env.VITE_API_TIMEOUT as string || '5000');

        const baseUrlStatus = await this.checkConnection(baseUrl, timeout);

        return {
            baseUrlStatus: {
                isOnline: baseUrlStatus.isOnline,
                latency: baseUrlStatus.latency,
                message: baseUrlStatus.isOnline ? 'Server is online' : 'Server is offline',
            },
            apiEndpointsStatus: {
                auth: await this.checkEndpoint('/api/auth/health'),
                schedule: await this.checkEndpoint('/api/schedule/health'),
            },
            corsStatus: await this.checkCorsSupport(),
            environmentVariables: {
                apiBaseUrl: baseUrl,
                apiTimeout: String(timeout),
                apiRetryCount: import.meta.env.VITE_API_RETRY_COUNT || '3',
                debug: import.meta.env.VITE_API_DEBUG || false,
            },
            browserInfo: {
                userAgent: navigator.userAgent,
                language: navigator.language,
                cookiesEnabled: navigator.cookieEnabled,
                onLine: navigator.onLine,
                platform: navigator.platform,
            },
        };
    }

    private static async checkEndpoint(path: string): Promise<{
        isOnline: boolean;
        latency?: number;
        message: string;
    }> {
        const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
        const startTime = Date.now();
        try {
            const response = await fetch(`${baseUrl}${path}`, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                },
                mode: 'cors',
                credentials: 'include'
            });
            const latency = Date.now() - startTime;
            return {
                isOnline: response.ok,
                latency,
                message: response.ok ? 'Endpoint is available' : `Endpoint returned status ${response.status}`
            };
        } catch (error) {
            return {
                isOnline: false,
                message: error instanceof Error ? error.message : 'Unknown error occurred'
            };
        }
    }

    private static async checkCorsSupport(): Promise<{
        optionsSupported: boolean;
        corsHeadersPresent: boolean;
        message: string;
        details?: unknown;
    }> {
        const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
        try {
            const response = await fetch(`${baseUrl}/api/auth/health`, {
                method: 'OPTIONS',
                headers: {
                    'Accept': 'application/json',
                    'Access-Control-Request-Method': 'GET',
                    'Access-Control-Request-Headers': 'authorization'
                },
                mode: 'cors',
                credentials: 'include'
            });

            const corsHeaders = {
                allowOrigin: response.headers.get('access-control-allow-origin'),
                allowMethods: response.headers.get('access-control-allow-methods'),
                allowHeaders: response.headers.get('access-control-allow-headers'),
                allowCredentials: response.headers.get('access-control-allow-credentials')
            };

            return {
                optionsSupported: response.ok,
                corsHeadersPresent: Object.values(corsHeaders).some(Boolean),
                message: response.ok ? 'CORS is properly configured' : 'CORS might not be properly configured',
                details: corsHeaders
            };
        } catch (error) {
            return {
                optionsSupported: false,
                corsHeadersPresent: false,
                message: error instanceof Error ? error.message : 'CORS check failed',
            };
        }
    }

    static checkAuthStatus(): {
        isAuthenticated: boolean;
        token: string | null;
        userInfo: any | null;
        tokenExpiry: number | null;
        message: string;
    } {
        const token = localStorage.getItem('token');
        const userInfo = localStorage.getItem('userInfo');
        let tokenExpiry: number | null = null;
        let isAuthenticated = false;
        let message = '';

        try {
            if (token) {
                // 解析 JWT token
                const base64Url = token.split('.')[1];
                const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
                const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
                    return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
                }).join(''));

                const payload = JSON.parse(jsonPayload);
                tokenExpiry = payload.exp * 1000; // 转换为毫秒
                const now = Date.now();

                if (tokenExpiry > now) {
                    isAuthenticated = true;
                    message = '认证有效';
                } else {
                    message = 'Token 已过期';
                }
            } else {
                message = '未找到 token';
            }
        } catch (error) {
            message = 'Token 格式无效';
        }

        return {
            isAuthenticated,
            token,
            userInfo: userInfo ? JSON.parse(userInfo) : null,
            tokenExpiry,
            message
        };
    }

    static printAuthStatus(): void {
        const status = this.checkAuthStatus();
        console.group('认证状态诊断');
        console.log('认证状态:', status.isAuthenticated ? '已认证' : '未认证');
        console.log('认证消息:', status.message);
        if (status.token) {
            console.log('Token:', status.token);
            if (status.tokenExpiry) {
                console.log('Token 过期时间:', new Date(status.tokenExpiry).toLocaleString());
                console.log('是否过期:', Date.now() > status.tokenExpiry ? '是' : '否');
            }
        }
        if (status.userInfo) {
            console.log('用户信息:', status.userInfo);
        }
        console.groupEnd();
    }
}

/**
 * 导出一个简单的函数用于快速检查服务器状态
 */
export const checkServerStatus = async (url?: string): Promise<boolean> => {
    const baseUrl = url || import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8080';
    const result = await ServerDiagnosticTools.checkConnection(baseUrl, 5000);
    return result.isOnline;
};
