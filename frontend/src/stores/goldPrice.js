import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import axios from 'axios'
import dayjs from 'dayjs'

export const useGoldPriceStore = defineStore('goldPrice', () => {
  // 状态
  const currentPrice = ref(null)
  const priceHistory = ref([])
  const isConnected = ref(false)
  const isLoading = ref(false)
  const error = ref(null)
  const lastUpdateTime = ref(null)
  
  // WebSocket 客户端
  let stompClient = null
  
  // 计算属性
  const formattedPrice = computed(() => {
    if (!currentPrice.value) return '--'
    return `$${currentPrice.value.price?.toFixed(2) || '--'}`
  })
  
  const priceChange = computed(() => {
    if (!currentPrice.value?.change) return null
    return {
      value: currentPrice.value.change,
      percent: currentPrice.value.changePercent,
      isPositive: currentPrice.value.change >= 0
    }
  })
  
  const chartData = computed(() => {
    return priceHistory.value.map(item => ({
      time: dayjs(item.timestamp).format('HH:mm:ss'),
      price: item.price,
      timestamp: item.timestamp
    }))
  })

  const dataSource = computed(() => {
    if (!currentPrice.value?.source) return '未知'
    return currentPrice.value.source
  })
  
  // 方法
  const initWebSocket = () => {
    try {
      // 创建 STOMP 客户端
      stompClient = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8080/ws/gold-price'),
        connectHeaders: {},
        debug: (str) => {
          if (import.meta.env.DEV) {
            console.log('STOMP Debug:', str)
          }
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      })
      
      // 连接成功回调
      stompClient.onConnect = (frame) => {
        console.log('WebSocket 连接成功:', frame)
        isConnected.value = true
        error.value = null
        
        // 订阅黄金价格主题
        stompClient.subscribe('/topic/gold-price', (message) => {
          try {
            const priceData = JSON.parse(message.body)
            console.log('收到价格数据:', priceData)
            
            // 更新当前价格
            if (priceData.price) {
              updateCurrentPrice(priceData)
            }
          } catch (err) {
            console.error('解析价格数据失败:', err)
          }
        })
      }
      
      // 连接错误回调
      stompClient.onStompError = (frame) => {
        console.error('STOMP 错误:', frame.headers['message'])
        console.error('详细信息:', frame.body)
        isConnected.value = false
        error.value = 'WebSocket 连接错误'
      }
      
      // 连接断开回调
      stompClient.onDisconnect = () => {
        console.log('WebSocket 连接断开')
        isConnected.value = false
      }
      
      // 激活连接
      stompClient.activate()
      
    } catch (err) {
      console.error('初始化 WebSocket 失败:', err)
      error.value = 'WebSocket 初始化失败'
    }
  }
  
  const disconnectWebSocket = () => {
    if (stompClient) {
      stompClient.deactivate()
      isConnected.value = false
    }
  }
  
  const updateCurrentPrice = (priceData) => {
    currentPrice.value = priceData
    lastUpdateTime.value = new Date()
    
    // 添加到历史记录
    priceHistory.value.push({
      ...priceData,
      timestamp: priceData.timestamp || new Date().toISOString()
    })
    
    // 保持历史记录在合理范围内（最多100条）
    if (priceHistory.value.length > 100) {
      priceHistory.value = priceHistory.value.slice(-100)
    }
  }
  
  const fetchCurrentPrice = async () => {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await axios.get('/api/gold/current')
      const priceData = response.data
      
      updateCurrentPrice(priceData)
      return priceData
    } catch (err) {
      console.error('获取当前价格失败:', err)
      error.value = '获取价格数据失败'
      throw err
    } finally {
      isLoading.value = false
    }
  }
  
  const refreshPrice = async () => {
    try {
      const response = await axios.post('/api/gold/refresh')
      console.log('刷新价格响应:', response.data)
      return response.data
    } catch (err) {
      console.error('刷新价格失败:', err)
      error.value = '刷新价格失败'
      throw err
    }
  }
  
  const clearError = () => {
    error.value = null
  }
  
  const clearHistory = () => {
    priceHistory.value = []
  }
  
  // 返回状态和方法
  return {
    // 状态
    currentPrice,
    priceHistory,
    isConnected,
    isLoading,
    error,
    lastUpdateTime,
    
    // 计算属性
    formattedPrice,
    priceChange,
    chartData,
    dataSource,
    
    // 方法
    initWebSocket,
    disconnectWebSocket,
    updateCurrentPrice,
    fetchCurrentPrice,
    refreshPrice,
    clearError,
    clearHistory
  }
})