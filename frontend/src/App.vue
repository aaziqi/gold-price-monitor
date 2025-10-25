<template>
  <div id="app" class="min-h-screen bg-gray-50">
    <!-- 导航栏 -->
    <nav class="bg-white shadow-sm border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <!-- Logo -->
          <div class="flex items-center">
            <div class="flex-shrink-0 flex items-center">
              <svg class="h-8 w-8 text-gold-500" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10 2L3 7v11h4v-6h6v6h4V7l-7-5z"/>
              </svg>
              <h1 class="ml-2 text-xl font-bold text-gradient-gold">
                黄金价格监控
              </h1>
            </div>
          </div>
          
          <!-- 连接状态 -->
          <div class="flex items-center space-x-4">
            <div class="flex items-center">
              <div 
                :class="[
                  'w-2 h-2 rounded-full mr-2',
                  connectionStatus === 'connected' ? 'bg-green-500' : 'bg-red-500'
                ]"
              ></div>
              <span class="text-sm text-gray-600">
                {{ connectionStatus === 'connected' ? '已连接' : '未连接' }}
              </span>
            </div>
            
            <!-- 刷新按钮 -->
            <button 
              @click="refreshPrice"
              :disabled="isRefreshing"
              class="btn btn-secondary text-sm"
            >
              <svg 
                :class="['w-4 h-4 mr-1', isRefreshing ? 'animate-spin' : '']" 
                fill="none" 
                stroke="currentColor" 
                viewBox="0 0 24 24"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              刷新
            </button>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主要内容 -->
    <main class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="bg-white border-t border-gray-200 mt-12">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div class="text-center text-sm text-gray-500">
          <p>&copy; 2024 黄金价格监控平台. 所有权利保留.</p>
          <p class="mt-1">
            数据仅供参考，投资有风险，入市需谨慎
          </p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useGoldPriceStore } from '@/stores/goldPrice'

// 状态管理
const goldPriceStore = useGoldPriceStore()
const connectionStatus = ref('disconnected')
const isRefreshing = ref(false)

// 刷新价格
const refreshPrice = async () => {
  if (isRefreshing.value) return
  
  isRefreshing.value = true
  try {
    await goldPriceStore.refreshPrice()
  } catch (error) {
    console.error('刷新价格失败:', error)
  } finally {
    isRefreshing.value = false
  }
}

// 监听连接状态
const updateConnectionStatus = (status) => {
  connectionStatus.value = status
}

// 生命周期
onMounted(() => {
  // 初始化WebSocket连接
  goldPriceStore.initWebSocket()
  
  // 监听连接状态变化
  goldPriceStore.$subscribe((mutation, state) => {
    if (mutation.events?.key === 'isConnected') {
      connectionStatus.value = state.isConnected ? 'connected' : 'disconnected'
    }
  })
})

onUnmounted(() => {
  // 清理WebSocket连接
  goldPriceStore.disconnectWebSocket()
})
</script>

<style scoped>
/* 组件特定样式 */
</style>