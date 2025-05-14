import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  // 添加开发服务器代理，解决CORS问题
  server: {
    proxy: {
      // 将所有/api开头的请求代理到后端服务器
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        // 移除rewrite，保留/api前缀
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.error('代理错误:', err);
          });
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('发送代理请求:', req.method, req.url);
          });
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('收到代理响应:', proxyRes.statusCode, req.url);
            if (proxyRes.statusCode >= 400) {
              console.error(`请求错误: ${req.url} 状态码: ${proxyRes.statusCode}`);
            }
          });
        }
      }
    }
  }
})
