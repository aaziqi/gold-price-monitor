<template>
  <div class="card card-hover">
    <div class="flex justify-between items-center mb-4">
      <h3 class="text-lg font-semibold text-gray-900">价格走势图</h3>
      <div class="flex items-center space-x-2">
        <button 
          @click="toggleTimeRange('1h')"
          :class="[
            'px-3 py-1 text-sm rounded-md transition-colors',
            timeRange === '1h' ? 'bg-gold-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          ]"
        >
          1小时
        </button>
        <button 
          @click="toggleTimeRange('1d')"
          :class="[
            'px-3 py-1 text-sm rounded-md transition-colors',
            timeRange === '1d' ? 'bg-gold-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          ]"
        >
          1天
        </button>
        <button 
          @click="clearChart"
          class="px-3 py-1 text-sm rounded-md bg-gray-100 text-gray-600 hover:bg-gray-200 transition-colors"
        >
          清空
        </button>
      </div>
    </div>
    
    <!-- 图表容器 -->
    <div 
      ref="chartContainer" 
      class="w-full h-80 min-h-[320px]"
      :class="{ 'opacity-50': isLoading }"
    ></div>
    
    <!-- 加载状态 -->
    <div v-if="isLoading" class="absolute inset-0 flex items-center justify-center bg-white bg-opacity-75">
      <div class="loading-spinner"></div>
    </div>
    
    <!-- 无数据状态 -->
    <div v-if="!isLoading && chartData.length === 0" class="absolute inset-0 flex items-center justify-center">
      <div class="text-center text-gray-500">
        <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
        </svg>
        <p class="mt-2">暂无数据</p>
        <p class="text-sm">等待价格数据更新...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import { useGoldPriceStore } from '@/stores/goldPrice'
import { storeToRefs } from 'pinia'

// Props
const props = defineProps({
  height: {
    type: String,
    default: '320px'
  }
})

// 状态管理
const goldPriceStore = useGoldPriceStore()
const { chartData, isLoading } = storeToRefs(goldPriceStore)

// 本地状态
const chartContainer = ref(null)
const timeRange = ref('1h')
let chartInstance = null

// 图表配置
const getChartOption = (data) => {
  const times = data.map(item => item.time)
  const prices = data.map(item => item.price)
  
  return {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: {
        color: '#374151'
      },
      formatter: function (params) {
        const data = params[0]
        return `
          <div class="p-2">
            <div class="font-medium">${data.name}</div>
            <div class="text-gold-600 font-bold">$${data.value?.toFixed(2) || '--'}</div>
          </div>
        `
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '5%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: times,
      axisLine: {
        lineStyle: {
          color: '#e5e7eb'
        }
      },
      axisLabel: {
        color: '#6b7280',
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      scale: true,
      axisLine: {
        lineStyle: {
          color: '#e5e7eb'
        }
      },
      axisLabel: {
        color: '#6b7280',
        fontSize: 12,
        formatter: '${value}'
      },
      splitLine: {
        lineStyle: {
          color: '#f3f4f6',
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: '黄金价格',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          color: '#f59e0b',
          width: 2
        },
        itemStyle: {
          color: '#f59e0b',
          borderColor: '#fff',
          borderWidth: 2
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: 'rgba(245, 158, 11, 0.3)'
              },
              {
                offset: 1,
                color: 'rgba(245, 158, 11, 0.05)'
              }
            ]
          }
        },
        data: prices,
        emphasis: {
          focus: 'series',
          itemStyle: {
            color: '#d97706',
            borderColor: '#fff',
            borderWidth: 3,
            shadowBlur: 10,
            shadowColor: 'rgba(245, 158, 11, 0.5)'
          }
        }
      }
    ],
    animation: true,
    animationDuration: 1000,
    animationEasing: 'cubicOut'
  }
}

// 初始化图表
const initChart = () => {
  if (!chartContainer.value) return
  
  chartInstance = echarts.init(chartContainer.value, null, {
    renderer: 'canvas',
    useDirtyRect: false
  })
  
  // 设置初始配置
  updateChart()
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
}

// 更新图表
const updateChart = () => {
  if (!chartInstance) return
  
  const option = getChartOption(chartData.value)
  chartInstance.setOption(option, true)
}

// 处理窗口大小变化
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 切换时间范围
const toggleTimeRange = (range) => {
  timeRange.value = range
  // 这里可以根据时间范围过滤数据
  updateChart()
}

// 清空图表
const clearChart = () => {
  goldPriceStore.clearHistory()
  updateChart()
}

// 监听数据变化
watch(chartData, () => {
  nextTick(() => {
    updateChart()
  })
}, { deep: true })

// 生命周期
onMounted(() => {
  nextTick(() => {
    initChart()
  })
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.card {
  position: relative;
}
</style>