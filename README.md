# 🏆 实时黄金价格监控与可视化平台

<div align="center">

![Gold Price Monitor](https://img.shields.io/badge/Gold%20Price-Monitor-gold?style=for-the-badge&logo=chart-line)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=flat-square&logo=spring-boot)
![Vue.js](https://img.shields.io/badge/Vue.js-3.3.8-4FC08D?style=flat-square&logo=vue.js)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=flat-square&logo=docker)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

**专业的实时黄金价格监控与可视化解决方案**

[🚀 快速开始](#快速开始) • [📖 文档](#项目文档) • [🐳 部署](#部署指南) • [🤝 贡献](#贡献指南)

</div>

## 📋 项目简介

本项目是一个现代化的实时黄金价格监控与可视化平台，采用微服务架构和前后端分离设计。通过 WebSocket 技术实现价格数据的实时推送，使用 ECharts 提供专业的数据可视化，并通过 TailwindCSS 实现完美的响应式布局。

### ✨ 核心特性

- 🔄 **实时数据**: WebSocket 实时推送黄金价格变化
- 📊 **专业图表**: ECharts 动态折线图展示价格趋势
- 📱 **响应式设计**: 完美适配 PC、平板、手机等设备
- 🚀 **高性能**: Spring Boot + Vue3 现代化技术栈
- 🐳 **容器化**: Docker 一键部署，支持生产环境
- 🔒 **安全可靠**: HTTPS、CORS、安全头等安全措施
- 📈 **数据导出**: 支持历史数据 CSV 格式导出
- 🎨 **现代UI**: 基于 TailwindCSS 的美观界面

## 🛠 技术架构

### 后端技术栈
- **框架**: Spring Boot 3.2.0
- **实时通信**: WebSocket + STOMP
- **定时任务**: Spring Scheduler
- **HTTP客户端**: WebFlux
- **构建工具**: Maven
- **Java版本**: JDK 17+

### 前端技术栈
- **框架**: Vue 3.3.8 + Composition API
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **图表库**: ECharts 5.4.3
- **样式框架**: TailwindCSS 3.3.6
- **构建工具**: Vite 5.0
- **WebSocket**: STOMP.js

### 部署技术
- **容器化**: Docker + Docker Compose
- **反向代理**: Nginx
- **SSL/TLS**: 支持 HTTPS
- **监控**: 健康检查 + 日志管理

## 📁 项目结构

```
gold-price-monitor/
├── 📂 backend/                    # Spring Boot 后端服务
│   ├── 📂 src/main/java/com/goldmonitor/
│   │   ├── 📂 config/            # 配置类
│   │   ├── 📂 controller/        # REST 控制器
│   │   ├── 📂 model/             # 数据模型
│   │   ├── 📂 scheduler/         # 定时任务
│   │   ├── 📂 service/           # 业务服务
│   │   └── 📂 websocket/         # WebSocket 处理
│   ├── 📂 src/main/resources/    # 配置文件
│   ├── 📄 pom.xml               # Maven 配置
│   └── 📄 Dockerfile            # 后端容器配置
├── 📂 frontend/                   # Vue3 前端应用
│   ├── 📂 src/
│   │   ├── 📂 components/        # Vue 组件
│   │   ├── 📂 stores/            # Pinia 状态管理
│   │   ├── 📂 views/             # 页面组件
│   │   └── 📂 router/            # 路由配置
│   ├── 📄 package.json          # 依赖配置
│   ├── 📄 vite.config.js        # Vite 配置
│   ├── 📄 tailwind.config.js    # TailwindCSS 配置
│   └── 📄 Dockerfile            # 前端容器配置
├── 📂 docker/                     # Docker 配置
│   └── 📂 nginx/                 # Nginx 配置
├── 📂 scripts/                    # 构建和部署脚本
│   ├── 📄 build.sh              # 构建脚本
│   └── 📄 deploy.sh             # 部署脚本
├── 📄 docker-compose.yml        # Docker Compose 配置
├── 📄 .env.example              # 环境变量示例
└── 📄 README.md                 # 项目文档
```

## 🚀 快速开始

### 📋 环境要求

- **Java**: JDK 17 或更高版本
- **Node.js**: 16.0 或更高版本
- **Docker**: 20.10 或更高版本 (可选)
- **Docker Compose**: 2.0 或更高版本 (可选)

### 🔧 本地开发

#### 1. 克隆项目
```bash
git clone https://github.com/your-username/gold-price-monitor.git
cd gold-price-monitor
```

#### 2. 配置环境变量
```bash
cp .env.example .env
# 编辑 .env 文件，配置必要的环境变量
```

#### 3. 启动后端服务
```bash
cd backend
./mvnw spring-boot:run
```

#### 4. 启动前端服务
```bash
cd frontend
npm install
npm run dev
```

#### 5. 访问应用
- 🌐 前端地址: http://localhost:3000
- 🔗 后端API: http://localhost:8080/api
- 📊 WebSocket: ws://localhost:8080/ws/gold-price

### 🐳 Docker 部署

#### 快速部署
```bash
# 使用构建脚本
chmod +x scripts/build.sh
./scripts/build.sh

# 或直接使用 Docker Compose
docker-compose up -d
```

#### 生产环境部署
```bash
# 使用部署脚本
chmod +x scripts/deploy.sh
./scripts/deploy.sh --deploy
```

## 🧭 使用 GitLens 上传到 GitHub

> 如果您更习惯在 VS Code 内使用 GitLens 完成发布，请按以下步骤：

1) 安装与准备
- 在 VS Code 扩展市场安装 `GitLens`
- 打开项目根目录：`gold-price-monitor`

2) 初始化仓库（若尚未初始化）
- 左侧 `SOURCE CONTROL`（源代码管理）面板点击 `Initialize Repository`
- 或者使用 GitLens 命令面板：`GitLens: Initialize Repository`

3) 连接远程仓库
- 在 GitLens 侧边栏选择 `Remotes` > `Add Remote`
- 粘贴您的 GitHub 仓库地址（例如：`https://github.com/your-username/gold-price-monitor.git`）
- 也可使用 `Publish Repository` 功能直接发布到 GitHub（GitLens会引导创建远程）

4) 暂存与提交
- 在 `SOURCE CONTROL` 面板点击 `+`（Stage All Changes）
- 填写提交信息（如：`feat: 初始化黄金价格监控平台项目`），点击 `Commit`

5) 推送到 GitHub
- 在 `SOURCE CONTROL` 或 GitLens 中点击 `Push`
- 选择上游远程（`origin`）与主分支（`main`/`master`），完成推送

6) 验证
- 打开您的 GitHub 仓库页面，确认文件与README均已上传

> 备用：命令行方式请参考 `GITHUB_SETUP.md`。

---

## 📖 项目文档

- 技术总览：`docs/TECHNICAL_OVERVIEW.md`
- API 文档：`docs/API.md`
- 部署指南：`docs/DEPLOYMENT.md`
- GitHub 上传（命令行）：`GITHUB_SETUP.md`


### 🔌 API 接口

#### 获取当前黄金价格
```http
GET /api/gold/current
```

#### 手动刷新价格
```http
POST /api/gold/refresh
```

#### 系统状态
```http
GET /api/gold/status
```

#### 健康检查
```http
GET /api/gold/health
```

### 🔄 WebSocket 连接

```javascript
// 连接 WebSocket
const client = new Client({
  webSocketFactory: () => new SockJS('/ws/gold-price'),
  onConnect: () => {
    // 订阅价格更新
    client.subscribe('/topic/gold-price', (message) => {
      const priceData = JSON.parse(message.body);
      console.log('收到价格更新:', priceData);
    });
  }
});
client.activate();
```

### 🎨 组件使用

#### 价格卡片组件
```vue
<template>
  <PriceCard />
</template>

<script setup>
import PriceCard from '@/components/PriceCard.vue'
</script>
```

#### 价格图表组件
```vue
<template>
  <GoldPriceChart />
</template>

<script setup>
import GoldPriceChart from '@/components/GoldPriceChart.vue'
</script>
```

## 🔧 配置说明

### 环境变量配置

| 变量名 | 描述 | 默认值 | 必需 |
|--------|------|--------|------|
| `GOLD_API_KEY` | 黄金价格API密钥 | `demo_key` | 否 |
| `GOLD_API_URL` | API接口地址 | `https://api.metals.live/v1/spot/gold` | 否 |
| `PRICE_UPDATE_INTERVAL` | 价格更新间隔(秒) | `30` | 否 |
| `SERVER_PORT` | 后端服务端口 | `8080` | 否 |
| `FRONTEND_PORT` | 前端服务端口 | `3000` | 否 |

### 应用配置

#### 后端配置 (application.yml)
```yaml
gold:
  api:
    url: ${GOLD_API_URL:https://api.metals.live/v1/spot/gold}
    key: ${GOLD_API_KEY:demo_key}
  scheduler:
    price-update-interval: ${PRICE_UPDATE_INTERVAL:30}
```

#### 前端配置 (vite.config.js)
```javascript
export default defineConfig({
  server: {
    port: 3000,
    proxy: {
      '/api': 'http://localhost:8080',
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true
      }
    }
  }
})
```

## 🚀 部署指南

### 🐳 Docker 部署

#### 1. 准备环境
```bash
# 复制环境变量文件
cp .env.example .env

# 编辑配置
vim .env
```

#### 2. 构建和启动
```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 查看状态
docker-compose ps
```

#### 3. 访问应用
- 🌐 HTTP: http://localhost:80
- 🔒 HTTPS: https://localhost:443

### 🔧 生产环境部署

#### 1. 服务器准备
```bash
# 安装 Docker 和 Docker Compose
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

#### 2. 部署应用
```bash
# 克隆代码
git clone https://github.com/your-username/gold-price-monitor.git
cd gold-price-monitor

# 配置环境
cp .env.example .env
vim .env

# 执行部署
chmod +x scripts/deploy.sh
./scripts/deploy.sh --deploy
```

#### 3. 配置域名和SSL
```bash
# 生成SSL证书 (Let's Encrypt)
sudo apt install certbot
sudo certbot certonly --standalone -d your-domain.com

# 更新 Nginx 配置
vim docker/nginx/nginx.conf
```

### 📊 监控和维护

#### 查看日志
```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
```

#### 健康检查
```bash
# 检查服务状态
./scripts/deploy.sh --health

# 手动健康检查
curl http://localhost:8080/api/gold/health
curl http://localhost:80/health
```

#### 备份和恢复
```bash
# 创建备份
./scripts/deploy.sh --backup

# 回滚部署
./scripts/deploy.sh --rollback
```

## 🤝 贡献指南

我们欢迎所有形式的贡献！请遵循以下步骤：

### 🔀 提交代码

1. **Fork 项目**
2. **创建特性分支**: `git checkout -b feature/AmazingFeature`
3. **提交更改**: `git commit -m 'Add some AmazingFeature'`
4. **推送分支**: `git push origin feature/AmazingFeature`
5. **创建 Pull Request**

### 📝 代码规范

- **后端**: 遵循 Spring Boot 最佳实践
- **前端**: 使用 ESLint + Prettier
- **提交信息**: 使用 Conventional Commits 规范

### 🐛 报告问题

请使用 [GitHub Issues](https://github.com/your-username/gold-price-monitor/issues) 报告问题，包含以下信息：

- 问题描述
- 复现步骤
- 期望行为
- 环境信息

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot) - 强大的Java框架
- [Vue.js](https://vuejs.org/) - 渐进式JavaScript框架
- [ECharts](https://echarts.apache.org/) - 专业的数据可视化库
- [TailwindCSS](https://tailwindcss.com/) - 实用优先的CSS框架

## 📞 联系我们

- 📧 邮箱: omglaq@gmail.com  
- 🐙 GitHub: [GitHub主页](https://github.com/aaziqi)  
- 📖 文档: [项目文档](https://blog.csdn.net/weixin_73376427/article/details/153877652)


---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给我们一个星标！**

Made with ❤️ by Gold Monitor Team

</div>
