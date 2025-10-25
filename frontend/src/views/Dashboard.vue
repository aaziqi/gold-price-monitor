<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="text-center">
      <h1 class="text-3xl font-bold text-gradient-gold mb-2">
        实时黄金价格监控
      </h1>
      <p class="text-gray-600">
        专业的黄金价格实时监控与分析平台
      </p>
    </div>
    
    <!-- 主要内容区域 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 价格卡片 -->
      <div class="lg:col-span-1">
        <PriceCard />
      </div>
      
      <!-- 图表区域 -->
      <div class="lg:col-span-2">
        <GoldPriceChart />
      </div>
    </div>
    
    <!-- 统计信息 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <!-- 今日最高价 -->
      <div class="card text-center">
        <div class="text-2xl font-bold text-green-600 mb-1">
          ${{ todayStats.high?.toFixed(2) || '--' }}
        </div>
        <div class="text-sm text-gray-500">今日最高</div>
      </div>
      
      <!-- 今日最低价 -->
      <div class="card text-center">
        <div class="text-2xl font-bold text-red-600 mb-1">
          ${{ todayStats.low?.toFixed(2) || '--' }}
        </div>
        <div class="text-sm text-gray-500">今日最低</div>
      </div>
      
      <!-- 平均价格 -->
      <div class="card text-center">
        <div class="text-2xl font-bold text-blue-600 mb-1">
          ${{ todayStats.average?.toFixed(2) || '--' }}
        </div>
        <div class="text-sm text-gray-500">平均价格</div>
      </div>
      
      <!-- 数据点数 -->
      <div class="card text-center">
        <div class="text-2xl font-bold text-purple-600 mb-1">
          {{ priceHistory.length }}
        </div>
        <div class="text-sm text-gray-500">数据点数</div>
      </div>
    </div>
    
    <!-- 市场信息 -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <!-- 市场状态 -->
      <div class="card">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">市场状态</h3>
        <div class="space-y-3">
          <div class="flex justify-between items-center">
            <span class="text-gray-600">连接状态</span>
            <span 
              :class="[
                'status-indicator',
                isConnected ? 'status-online' : 'status-offline'
              ]"
            >
              {{ isConnected ? '已连接' : '未连接' }}
            </span>
          </div>
          
          <div class="flex justify-between items-center">
            <span class="text-gray-600">市场状态</span>
            <span class="status-indicator status-online">
              开市中
            </span>
          </div>
          
          <div class="flex justify-between items-center">
            <span class="text-gray-600">数据源</span>
            <span class="text-sm text-gray-500">{{ goldPriceStore.dataSource }}</span>
          </div>
          
          <div class="flex justify-between items-center">
            <span class="text-gray-600">更新频率</span>
            <span class="text-sm text-gray-500">30秒</span>
          </div>
        </div>
      </div>
      
      <!-- 快速操作 -->
      <div class="card">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">快速操作</h3>
        <div class="space-y-3">
          <button 
            @click="refreshPrice"
            :disabled="isRefreshing"
            class="w-full btn btn-primary"
          >
            <svg 
              :class="['w-4 h-4 mr-2', isRefreshing ? 'animate-spin' : '']" 
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            {{ isRefreshing ? '刷新中...' : '手动刷新' }}
          </button>
          
          <button 
            @click="clearHistory"
            class="w-full btn btn-secondary"
          >
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
            </svg>
            清空历史
          </button>
          
          <button 
            @click="exportData"
            class="w-full btn btn-secondary"
          >
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            导出数据
          </button>
        </div>
      </div>
    </div>
    
    <!-- 提示信息 -->
    <div class="bg-blue-50 border border-blue-200 rounded-lg p-4">
      <div class="flex items-start">
        <svg class="w-5 h-5 text-blue-400 mt-0.5 mr-3" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
        </svg>
        <div>
          <h4 class="text-sm font-medium text-blue-800 mb-1">温馨提示</h4>
          <p class="text-sm text-blue-700">
            本平台提供的黄金价格数据仅供参考，不构成投资建议。实际交易请以官方报价为准。投资有风险，入市需谨慎。
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useGoldPriceStore } from '@/stores/goldPrice'
import { storeToRefs } from 'pinia'
import PriceCard from '@/components/PriceCard.vue'
import GoldPriceChart from '@/components/GoldPriceChart.vue'

// 状态管理
const goldPriceStore = useGoldPriceStore()
const { priceHistory, isConnected } = storeToRefs(goldPriceStore)

// 本地状态
const isRefreshing = ref(false)

// 计算属性
const todayStats = computed(() => {
  if (priceHistory.value.length === 0) {
    return {
      high: null,
      low: null,
      average: null
    }
  }
  
  const prices = priceHistory.value.map(item => item.price).filter(Boolean)
  
  if (prices.length === 0) {
    return {
      high: null,
      low: null,
      average: null
    }
  }
  
  return {
    high: Math.max(...prices),
    low: Math.min(...prices),
    average: prices.reduce((sum, price) => sum + price, 0) / prices.length
  }
})

// 方法
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

const clearHistory = () => {
  if (confirm('确定要清空所有历史数据吗？')) {
    goldPriceStore.clearHistory()
  }
}

const exportData = () => {
  if (priceHistory.value.length === 0) {
    alert('暂无数据可导出')
    return
  }
  
  // 生成CSV数据
  const csvContent = [
    ['时间', '价格', '货币', '单位', '变化', '变化百分比'],
    ...priceHistory.value.map(item => [
      item.timestamp,
      item.price,
      item.currency || 'USD',
      item.unit || 'oz',
      item.change || '',
      item.changePercent || ''
    ])
  ].map(row => row.join(',')).join('\n')
  
  // 创建下载链接
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  
  link.setAttribute('href', url)
  link.setAttribute('download', `gold-price-data-${new Date().toISOString().split('T')[0]}.csv`)
  link.style.visibility = 'hidden'
  
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 生命周期
onMounted(() => {
  // 获取初始数据
  goldPriceStore.fetchCurrentPrice().catch(console.error)
})
</script>

<style scoped>
/* 组件特定样式 */
</style>