import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
    plugins: [react()],
    root: './src/main/resources/static',
    server: {
        port: 5173,
        proxy: {
            '/api': 'http://localhost:8080'  // 백엔드 서버 주소
        }
    },
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src')
        }
    },
    build: {
        outDir: '../static/assets',
        emptyOutDir: true,
        rollupOptions: {
            input: {
                main: path.resolve(__dirname, 'src/main/resources/static/main.jsx')
            }
        }
    }
})