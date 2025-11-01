# Vercel 部署指南

## 项目概述

这是一个实时黄金价格监控平台，使用 Vue.js 前端和 Vercel Serverless Functions 后端。

## 部署步骤

### 1. 准备工作

确保您已经：
- 拥有 Vercel 账户：https://vercel.com/aoqi-lis-projects
- 安装了 Git
- 项目代码已推送到 GitHub

### 2. 本地测试

```bash
# 安装依赖
npm run install-all

# 本地开发
cd frontend
npm run dev

# 构建测试
npm run build
```

### 3. Vercel 部署

#### 方法一：通过 Vercel CLI

```bash
# 安装 Vercel CLI
npm i -g vercel

# 登录 Vercel
vercel login

# 部署
vercel --prod
```

#### 方法二：通过 GitHub 集成

1. 访问 https://vercel.com/aoqi-lis-projects
2. 点击 "New Project"
3. 选择您的 GitHub 仓库
4. Vercel 会自动检测配置并部署

### 4. 环境变量配置

在 Vercel 控制台中设置以下环境变量：

```
NODE_ENV=production
VITE_API_BASE_URL=/api
VITE_WS_URL=/api/websocket
VITE_APP_TITLE=黄金价格监控平台
VITE_APP_VERSION=1.0.0
```

### 5. 自定义域名（可选）

1. 在 Vercel 项目设置中
2. 添加自定义域名
3. 配置 DNS 记录

## API 端点

部署后，以下 API 端点将可用：

- `GET /api/gold-price` - 获取当前黄金价格
- `GET /api/market-data?timeframe=24h` - 获取市场数据
- `GET /api/websocket?type=price` - WebSocket 模拟（轮询）

## 项目结构

```
gold-price-monitor/
├── api/                    # Vercel Serverless Functions
│   ├── gold-price.js      # 黄金价格 API
│   ├── market-data.js     # 市场数据 API
│   └── websocket.js       # WebSocket 模拟 API
├── frontend/              # Vue.js 前端应用
│   ├── src/
│   ├── dist/              # 构建输出
│   └── package.json
├── vercel.json            # Vercel 配置
├── package.json           # 根项目配置
└── VERCEL_DEPLOYMENT.md   # 本文档
```

## 技术栈

- **前端**: Vue.js 3, Vite, Tailwind CSS, ECharts
- **后端**: Vercel Serverless Functions (Node.js)
- **部署**: Vercel
- **API**: RESTful API + 轮询模拟 WebSocket

## 注意事项

1. **WebSocket 限制**: Vercel 不支持持久 WebSocket 连接，使用轮询替代
2. **冷启动**: Serverless Functions 可能有冷启动延迟
3. **请求限制**: 免费版有请求数量限制
4. **数据源**: 当前使用模拟数据，可替换为真实 API

## 故障排除

### 构建失败
- 检查 Node.js 版本 (>=18.0.0)
- 确保所有依赖已正确安装
- 查看 Vercel 构建日志

### API 错误
- 检查 Serverless Functions 日志
- 验证环境变量配置
- 确认 API 路由配置

### 前端问题
- 检查环境变量是否正确
- 验证 API 端点是否可访问
- 查看浏览器控制台错误

## 监控和维护

1. 使用 Vercel Analytics 监控性能
2. 设置错误报告和日志记录
3. 定期更新依赖包
4. 监控 API 使用量和限制

## 联系支持

如有问题，请：
1. 查看 Vercel 文档
2. 检查项目 Issues
3. 联系项目维护者