# 技术总览（Technical Overview）

本文档面向开发者和维护者，系统性说明黄金价格监控平台的架构、模块职责、数据流、关键技术实现与运维要点。

## 1. 架构概览

- 前端：`Vue 3 + Vite + Pinia + TailwindCSS + ECharts`
- 后端：`Spring Boot 3 + WebFlux + WebSocket(STOMP) + Scheduler`
- 数据源：`goldprice.org` 实时黄金价格（USD），按固定周期刷新
- 通讯：后端通过 WebSocket 推送价格到前端；前端订阅并更新展示
- 部署：Docker Compose（前后端 + Nginx 反向代理），支持HTTPS/监控

```
[goldprice.org] --HTTP--> [Spring Boot(Service)] --STOMP/WebSocket--> [Vue(Pinia+ECharts)]
                                      ^                                      |
                                      |---------- REST API -------------------|
```

## 2. 后端模块

- `controller`
  - `GoldPriceController`：REST接口（当前价格/刷新/状态/健康）
  - `HealthController`：健康检查接口
  - `DebugController`/`SimpleController`：调试与简单响应（非核心）
- `service`
  - `GoldPriceService`：
    - 调用 `gold.api.url`（`https://data-asg.goldprice.org/dbXRates/USD`）获取数据
    - 解析响应，提取 `price/currency/change/changePercent/marketStatus/source`
    - 错误时使用“降级”策略：记录日志并提供最近一次成功数据或安全兜底
- `scheduler`
  - `GoldPriceScheduler`：定时任务（默认 30 秒），拉取并广播价格
- `websocket`
  - `GoldPriceWebSocketHandler`：封装 STOMP 主题推送（`/topic/gold-price`）
- `model`
  - `GoldPrice`：价格数据结构（id、price、unit、currency、change、changePercent、timestamp、source、marketStatus）

### 配置项（`backend/src/main/resources/application.yml`）

```yaml
gold:
  api:
    url: https://data-asg.goldprice.org/dbXRates/USD
    key: demo_key
  scheduler:
    price-update-interval: 30
server:
  port: 8080
```

## 3. 前端模块

- `stores/goldPrice.js`
  - 状态：`currentPrice, priceHistory, isConnected, isLoading, error, lastUpdateTime`
  - 计算：`formattedPrice, priceChange, chartData, dataSource`
  - 功能：初始化 WebSocket、更新当前价格、维护历史数据
- `components/PriceCard.vue`
  - 显示价格、涨跌幅、数据源、市场状态、更新时间
- `components/GoldPriceChart.vue`
  - ECharts 折线图，渲染 `priceHistory`
- `views/Dashboard.vue`
  - 总览页：价格卡片 + 图表 + 市场信息 + 数据源（已动态显示 `source`）

## 4. 数据流与主题

- 后端广播主题：`/topic/gold-price`
- 前端订阅后，收到 JSON 数据（示例）：

```json
{
  "id": "1761378848071",
  "price": 4113.485,
  "currency": "USD",
  "unit": "oz",
  "change": -15.97,
  "changePercent": -0.3867,
  "timestamp": "2025-10-25 15:54:08",
  "source": "GOLDPRICE.ORG",
  "marketStatus": "CLOSED"
}
```

## 5. REST API

- `GET /gold/current`：获取当前黄金价格（后端已开启 CORS）
- `POST /gold/refresh`：触发一次刷新并广播
- `GET /gold/status`：系统状态（连接、最近更新时间）
- `GET /gold/health`：健康检查

> 注：README 中展示的是 `/api/gold/...` 代理路径；开发环境由 `vite.config.js` 代理到 `http://localhost:8080`。

## 6. 错误处理与容错

- 请求失败：记录日志、保留上次成功数据；无法获取时返回安全兜底（提示来源为 `MOCK` 或数据不可用）
- WebSocket断开：前端自动重连；Pinia中标记 `isConnected=false` 并提示
- 数据解析异常：严格校验字段（`price/currency/source`），日志记录并跳过损坏样本

## 7. 安全与跨域

- CORS：后端已允许 `http://localhost:3000`（开发环境），生产环境通过 Nginx 统一域名
- HTTPS：Nginx 终止TLS，后端服务走内网HTTP
- 安全头：Nginx 添加常见安全头（X-Content-Type-Options, X-Frame-Options, etc.）

## 8. 部署与运维

- Docker Compose：`frontend + backend + nginx`
- 监控：`docker-compose logs -f`；健康检查接口 `/gold/health`
- 备份与回滚：`scripts/deploy.sh --backup --rollback`

## 9. 测试与验证

- 后端本地运行：`mvn spring-boot:run`，观察日志（定时任务与广播）
- API验证（PowerShell）：
  - `Invoke-WebRequest -Uri "http://localhost:8080/gold/current" -Method GET`
- 前端运行：`npm run dev`，打开 `http://localhost:3000/`
- 终端观察：Vite HMR 与 WebSocket连接日志

## 10. 关键实现摘要

- 替换数据源：`application.yml` 使用 `goldprice.org`；`GoldPriceService.parseGoldApiResponse(...)` 适配其返回格式
- 修复编译问题：引入 `java.util.List` 等必要import，确保构建通过
- 动态数据源显示：前端移除“模拟数据”硬编码，改为 `goldPriceStore.dataSource`

## 11. 后续优化建议

- 增加多币种支持（XAU/CNY、XAU/EUR）与单位转换（oz ↔ g）
- 历史数据存储（SQLite/PostgreSQL）与查询接口
- 告警与通知（价格阈值触发）
- 更精细的错误分类与重试策略（指数退避）
- 引入端到端测试（Cypress/Playwright）