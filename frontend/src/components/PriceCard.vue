<template>
  <div class="card card-hover">
    <div class="flex items-center justify-between">
      <!-- 价格信息 -->
      <div class="flex-1">
        <div class="flex items-center space-x-2 mb-2">
          <h3 class="text-lg font-semibold text-gray-900">当前金价</h3>
          <span 
            :class="[
              'status-indicator',
              isConnected ? 'status-online' : 'status-offline'
            ]"
          >
            {{ isConnected ? '实时' : '离线' }}
          </span>
        </div>
        
        <!-- 主要价格显示 -->
        <div class="flex items-baseline space-x-3">
          <span class="price-display">
            {{ formattedPrice }}
          </span>
          <span class="text-sm text-gray-500">/盎司</span>
        </div>
        
        <!-- 价格变化 -->
        <div v-if="priceChange" class="flex items-center space-x-2 mt-2">
          <div 
            :class="[
              'flex items-center space-x-1 text-sm font-medium',
              priceChange.isPositive ? 'price-change-positive' : 'price-change-negative'
            ]"
          >
            <!-- 箭头图标 -->
            <svg 
              :class="[
                'w-4 h-4',
                priceChange.isPositive ? 'transform rotate-0' : 'transform rotate-180'
              ]" 
              fill="currentColor" 
              viewBox="0 0 20 20"
            >
              <path fill-rule="evenodd" d="M5.293 7.707a1 1 0 010-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 01-1.414 1.414L11 5.414V17a1 1 0 11-2 0V5.414L6.707 7.707a1 1 0 01-1.414 0z" clip-rule="evenodd" />
            </svg>
            
            <!-- 变化金额 -->
            <span>
              ${{ Math.abs(priceChange.value).toFixed(2) }}
            </span>
            
            <!-- 变化百分比 -->
            <span class="text-xs">
              ({{ priceChange.isPositive ? '+' : '-' }}{{ Math.abs(priceChange.percent).toFixed(2) }}%)
            </span>
          </div>
        </div>
        
        <!-- 最后更新时间 -->
        <div v-if="lastUpdateTime" class="mt-3 text-xs text-gray-500">
          最后更新: {{ formatUpdateTime(lastUpdateTime) }}
        </div>
      </div>
      
      <!-- 黄金图标 -->
      <div class="flex-shrink-0 ml-4">
        <div class="w-16 h-16 bg-gradient-gold rounded-full flex items-center justify-center shadow-gold">
          <svg class="w-8 h-8 text-white" fill="currentColor" viewBox="0 0 20 20">
            <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="isLoading" class="absolute inset-0 bg-white bg-opacity-75 flex items-center justify-center rounded-lg">
      <div class="flex items-center space-x-2">
        <div class="loading-spinner"></div>
        <span class="text-sm text-gray-600">获取价格中...</span>
      </div>
    </div>
    
    <!-- 错误状态 -->
    <div v-if="error" class="mt-3 p-3 bg-red-50 border border-red-200 rounded-lg">
      <div class="flex items-center">
        <svg class="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
        </svg>
        <span class="text-sm text-red-700">{{ error }}</span>
        <button 
          @click="clearError"
          class="ml-auto text-red-400 hover:text-red-600"
        >
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useGoldPriceStore } from '@/stores/goldPrice'
import { storeToRefs } from 'pinia'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

// 配置 dayjs
dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

// 状态管理
const goldPriceStore = useGoldPriceStore()
const { 
  formattedPrice, 
  priceChange, 
  isConnected, 
  isLoading, 
  error, 
  lastUpdateTime 
} = storeToRefs(goldPriceStore)

// 方法
const clearError = () => {
  goldPriceStore.clearError()
}

const formatUpdateTime = (time) => {
  if (!time) return ''
  return dayjs(time).fromNow()
}
</script>

<style scoped>
.card {
  position: relative;
  min-height: 160px;
}

.price-display {
  @apply text-4xl font-bold text-gold-600;
}

@media (max-width: 640px) {
  .price-display {
    @apply text-3xl;
  }
}
</style>