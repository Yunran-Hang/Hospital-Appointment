import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
// ElementPlus按需导入配置
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      // 配置elementPlus采用sass样式配色系统
      resolvers: [ElementPlusResolver({ importStyle: "sass"})],
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
    extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue'],
  },
  css: {
    preprocessorOptions: {
      scss: {
        // 自动导入定制化样式文件进行样式覆盖
        additionalData: `
          @use "@/styles/element/index.scss" as *;
          @use "@/styles/var.scss" as *;
        `,
      }
    }
  },
  server:{
    proxy:{
      '/api':{//获取路径中包含了/api的请求
          target:'http://localhost:8080',//后台服务所在的源
          changeOrigin:true,//修改源
          rewrite:(path)=>path.replace(/^\/api/,'')///api替换为''
      }
    }
  },
  // 生产环境构建配置
  build: {
    // 启用代码压缩
    minify: 'terser',
    // Terser压缩选项
    terserOptions: {
      compress: {
        // 移除console.log
        drop_console: true,
        // 移除debugger
        drop_debugger: true,
        // 移除无用代码
        dead_code: true,
      },
      mangle: {
        // 混淆顶级作用域
        toplevel: true,
      },
    },
    // 代码分割配置
    rollupOptions: {
      output: {
        // 手动分割代码块
        manualChunks: {
          // 将Vue相关库打包到vendor块
          vendor: ['vue', 'vue-router', 'pinia'],
          // 将Element Plus单独打包
          elementui: ['element-plus'],
          // 将工具库单独打包
          utils: ['axios', '@vueuse/core'],
        },
        // 文件名混淆
        chunkFileNames: 'js/[name]-[hash].js',
        entryFileNames: 'js/[name]-[hash].js',
        assetFileNames: 'assets/[name]-[hash].[ext]',
      },
    },
    // 构建输出目录
    outDir: 'dist',
    // 静态资源目录
    assetsDir: 'assets',
    // 启用CSS代码分割
    cssCodeSplit: true,
  },
})
