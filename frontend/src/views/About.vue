<template>
  <div class="max-w-4xl mx-auto space-y-8">
    <!-- 页面标题 -->
    <div class="text-center">
      <h1 class="text-3xl font-bold text-gradient-gold mb-4">
        关于黄金价格监控平台
      </h1>
      <p class="text-lg text-gray-600">
        专业、实时、可靠的黄金价格监控解决方案
      </p>
    </div>
    
    <!-- 平台介绍 -->
    <div class="card">
      <h2 class="text-2xl font-semibold text-gray-900 mb-4">平台简介</h2>
      <div class="prose prose-lg text-gray-700">
        <p>
          黄金价格监控平台是一个基于现代Web技术构建的实时黄金价格监控与可视化系统。
          我们致力于为投资者、交易员和黄金爱好者提供准确、及时的黄金价格信息。
        </p>
        <p>
          平台采用 <strong>Spring Boot + Vue3 + ECharts + WebSocket + TailwindCSS</strong> 
          技术栈，实现了后端实时数据获取、前端动态展示以及多端自适应布局。
        </p>
      </div>
    </div>
    
    <!-- 核心功能 -->
    <div class="card">
      <h2 class="text-2xl font-semibold text-gray-900 mb-6">核心功能</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="flex items-start space-x-3">
          <div class="flex-shrink-0 w-8 h-8 bg-gold-100 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-gold-600" fill="currentColor" viewBox="0 0 20 20">
              <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <div>
            <h3 class="font-semibold text-gray-900">实时价格监控</h3>
            <p class="text-sm text-gray-600">
              通过WebSocket技术实现黄金价格的实时推送，确保数据的及时性和准确性
            </p>
          </div>
        </div>
        
        <div class="flex items-start space-x-3">
          <div class="flex-shrink-0 w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
              <path d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
            </svg>
          </div>
          <div>
            <h3 class="font-semibold text-gray-900">动态图表展示</h3>
            <p class="text-sm text-gray-600">
              基于ECharts的专业图表组件，直观展示黄金价格的变化趋势
            </p>
          </div>
        </div>
        
        <div class="flex items-start space-x-3">
          <div class="flex-shrink-0 w-8 h-8 bg-green-100 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-green-600" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M3 4a1 1 0 011-1h4a1 1 0 010 2H6.414l2.293 2.293a1 1 0 01-1.414 1.414L5 6.414V8a1 1 0 01-2 0V4zm9 1a1 1 0 010-2h4a1 1 0 011 1v4a1 1 0 01-2 0V6.414l-2.293 2.293a1 1 0 11-1.414-1.414L13.586 5H12zm-9 7a1 1 0 012 0v1.586l2.293-2.293a1 1 0 111.414 1.414L6.414 15H8a1 1 0 010 2H4a1 1 0 01-1-1v-4zm13-1a1 1 0 011 1v4a1 1 0 01-1 1h-4a1 1 0 010-2h1.586l-2.293-2.293a1 1 0 111.414-1.414L15 13.586V12a1 1 0 011-1z" clip-rule="evenodd"/>
            </svg>
          </div>
          <div>
            <h3 class="font-semibold text-gray-900">响应式设计</h3>
            <p class="text-sm text-gray-600">
              基于TailwindCSS的响应式布局，完美适配PC、平板、手机等各种设备
            </p>
          </div>
        </div>
        
        <div class="flex items-start space-x-3">
          <div class="flex-shrink-0 w-8 h-8 bg-purple-100 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-purple-600" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M3 17a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm3.293-7.707a1 1 0 011.414 0L9 10.586V3a1 1 0 112 0v7.586l1.293-1.293a1 1 0 111.414 1.414l-3 3a1 1 0 01-1.414 0l-3-3a1 1 0 010-1.414z" clip-rule="evenodd"/>
            </svg>
          </div>
          <div>
            <h3 class="font-semibold text-gray-900">数据导出</h3>
            <p class="text-sm text-gray-600">
              支持将历史价格数据导出为CSV格式，便于进一步分析和处理
            </p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 技术架构 -->
    <div class="card">
      <h2 class="text-2xl font-semibold text-gray-900 mb-6">技术架构</h2>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                层级
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                技术
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                功能
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                前端
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                Vue3 + ECharts + TailwindCSS
              </td>
              <td class="px-6 py-4 text-sm text-gray-500">
                响应式界面 + 实时数据可视化
              </td>
            </tr>
            <tr>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                后端
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                Spring Boot + WebSocket + Scheduler
              </td>
              <td class="px-6 py-4 text-sm text-gray-500">
                定时拉取行情并推送
              </td>
            </tr>
            <tr>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                数据源
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                Metals API / GoldAPI.io
              </td>
              <td class="px-6 py-4 text-sm text-gray-500">
                黄金实时数据接口
              </td>
            </tr>
            <tr>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                部署
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                Docker + Nginx
              </td>
              <td class="px-6 py-4 text-sm text-gray-500">
                快速容器化部署
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    
    <!-- 免责声明 -->
    <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-6">
      <div class="flex items-start">
        <svg class="w-6 h-6 text-yellow-400 mt-1 mr-3" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
        </svg>
        <div>
          <h3 class="text-lg font-semibold text-yellow-800 mb-2">免责声明</h3>
          <div class="text-sm text-yellow-700 space-y-2">
            <p>
              1. 本平台提供的黄金价格数据仅供参考，不构成任何投资建议。
            </p>
            <p>
              2. 实际交易价格可能与显示价格存在差异，请以官方报价为准。
            </p>
            <p>
              3. 投资有风险，入市需谨慎。请根据自身风险承受能力做出投资决策。
            </p>
            <p>
              4. 本平台不对因使用本服务而产生的任何损失承担责任。
            </p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 联系信息 -->
    <div class="card text-center">
      <h2 class="text-2xl font-semibold text-gray-900 mb-4">联系我们</h2>
      <p class="text-gray-600 mb-6">
        如有任何问题或建议，欢迎与我们联系
      </p>
      <div class="flex justify-center space-x-6">
        <a href="mailto:contact@goldmonitor.com" class="flex items-center text-gold-600 hover:text-gold-700">
          <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
            <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
            <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
          </svg>
          contact@goldmonitor.com
        </a>
        <a href="https://github.com/goldmonitor" class="flex items-center text-gold-600 hover:text-gold-700">
          <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 0C4.477 0 0 4.484 0 10.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0110 4.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.203 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.942.359.31.678.921.678 1.856 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0020 10.017C20 4.484 15.522 0 10 0z" clip-rule="evenodd"/>
          </svg>
          GitHub
        </a>
      </div>
    </div>
  </div>
</template>

<script setup>
// 页面逻辑
</script>

<style scoped>
.prose {
  max-width: none;
}
</style>