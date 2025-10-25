# 从零搭建「实时黄金价格监控与可视化平台」：含生产部署与性能优化

> 原创 | 开源地址：https://github.com/aaziqi/gold-price-monitor
> 标签：Spring Boot、Vue3、WebSocket、Docker、ECharts、Nginx、金融数据、实时可视化

@[toc]

## 摘要
本文从实际业务需求出发，完整拆解一个可生产落地的「实时黄金价格监控与可视化平台」。内容涵盖：数据采集与清洗、WebSocket 实时推送、前端可视化、工程化与 Docker 部署、Nginx HTTPS 网关、监控运维与性能优化。文章配套开源代码与详细文档，可直接用于学习与企业内的 PoC。

- 实时数据：后端定时采集黄金价格，并通过 WebSocket 毫秒级推送到前端
- 可视化：Vue3 + Pinia + ECharts 动态渲染趋势图与状态提示
- 工程化：后端 Spring Boot，前端 Vite；提供 Docker、Nginx、SSL、监控方案
- 可拓展：多数据源融合、阈值告警、持久化与历史回放

## 开源地址与文档
- GitHub 仓库：https://github.com/aaziqi/gold-price-monitor
- 项目总览：`README.md`
- 技术总览：`docs/TECHNICAL_OVERVIEW.md`
- 发布稿（本文）：`docs/CSDN_POST.md`

## 架构与技术栈
- 后端：`Spring Boot`、`Scheduler`、`WebSocket`、`JDK 17+`、`Maven`
- 前端：`Vue 3`、`Pinia`、`ECharts`、`TailwindCSS`、`Vite`
- 部署：`Docker`、`Nginx`、`SSL/TLS`、`Prometheus/Grafana`

数据流：第三方接口（如 `GOLDPRICE.ORG`）→ 后端定时采集与清洗 → WebSocket 广播 → 前端订阅更新状态与图表 → Nginx/HTTPS 网关对外服务 → Prometheus/Grafana 监控。

## 快速体验
- 前端开发：`frontend` 目录运行 `npm run dev`（默认 `http://localhost:3000/`）
- 后端开发：`backend` 目录运行 `mvn spring-boot:run`
- Docker 一键：项目根目录执行 `docker-compose up -d`

## 后端核心实现
### 1. 定时采集与清洗
后端定时拉取外部行情接口，统一格式为：`price`、`timestamp`、`source`。

```java
// 示例：定时任务（伪代码简化）
@Component
public class GoldPriceScheduler {
    @Scheduled(fixedRate = 60_000)
    public void fetchAndBroadcast() {
        GoldTick tick = fetchFromApi(); // 包含 price、timestamp、source
        websocketGateway.broadcast(tick); // 推送到所有在线会话
    }
}
```

### 2. WebSocket 广播
通过后端维护会话列表，对每次采集的增量行情进行广播，保证低延迟与稳定性。

```java
// 示例：WebSocket 广播（伪代码简化）
@Component
public class WebsocketGateway {
    private final Set<Session> sessions = ConcurrentHashMap.newKeySet();
    public void broadcast(GoldTick tick) {
        String payload = toJson(tick); // {price, timestamp, source}
        sessions.forEach(s -> s.sendAsync(payload));
    }
}
```

## 前端核心实现
### 1. 状态与订阅
使用 Pinia 管理实时行情状态，初始化 WebSocket 连接，订阅后端推送的 JSON。

```ts
// 示例：store（简化）
import { defineStore } from 'pinia';
export const useGoldStore = defineStore('gold', {
  state: () => ({ price: 0, timestamp: '', source: '' }),
  actions: {
    connect() {
      const ws = new WebSocket(import.meta.env.VITE_WS_URL);
      ws.onmessage = (ev) => {
        const data = JSON.parse(ev.data);
        this.price = data.price;
        this.timestamp = data.timestamp;
        this.source = data.source;
      };
    }
  }
});
```

### 2. ECharts 可视化
将价格序列映射到折线/趋势图，按需批量更新，避免高频重绘带来的卡顿。

```ts
// 示例：ECharts 更新（简化）
chart.setOption({
  xAxis: { type: 'time' },
  yAxis: { type: 'value' },
  series: [{ type: 'line', data: seriesData }]
});
```

### 3. 数据源展示（动态）
在 `Dashboard.vue` 中使用响应式数据展示 `source` 字段（如 `GOLDPRICE.ORG`），替换硬编码 Mock。

```vue
<!-- 示例片段：动态展示数据源 -->
<template>
  <div class="text-sm text-gray-500">数据来源：{{ goldStore.source }}</div>
</template>
```

## API 与 WebSocket 协议
- REST 接口：提供健康检查与基础拉取（详见仓库 `README.md`）
- WebSocket 消息：`{ price: number, timestamp: string, source: string }`

## 部署指南
### 本地开发
- 后端：`mvn spring-boot:run`
- 前端：`npm run dev`

### Docker 一键
- 执行：`docker-compose up -d`
- 验证：前端页面、后端健康检查、WebSocket 通路

### Nginx + HTTPS
- 反向代理前后端服务，开启 HTTPS、配置静态资源与限流策略
- 生产建议：仅开放必要端口、启用 HSTS、配置请求头校验

## 监控与运维
- 接入 `Actuator` 与 `Prometheus/Grafana`，监控采集延迟、广播耗时、连接数
- 日志轮转与告警：防止日志膨胀与磁盘占满，阈值触发报警

## 性能优化要点
- 请求限频与本地缓存，降低第三方 API 压力
- 广播采用非阻塞策略，避免阻塞采集线程
- ECharts 批量更新与动画降采样，保障前端流畅度

## 常见问题（FAQ）
- Q：外部接口不稳定怎么办？
  - A：增加重试与回退策略，使用缓存兜底；必要时多源融合提升鲁棒性
- Q：WebSocket 连接数增加导致推送变慢？
  - A：分片广播、异步队列与背压控制；必要时采用消息中间件
- Q：前端图表卡顿？
  - A：限制刷新频率、窗口化数据、降采样、避免过度动画

## CSDN 发布建议（符合规则）
- 使用 `@[toc]` 自动生成目录，提升可读性
- 代码块标注语言（如 `java`、`ts`、`vue`）保证高亮与复制体验
- 插图与架构图建议使用 PNG/JPG 并设置可公开访问权限
- 外链指向 GitHub 与官方文档，规范化锚文本（如“开源地址：…）
- 建议添加标签：`Spring Boot`、`Vue3`、`WebSocket`、`Docker`、`ECharts`、`Nginx`、`实时数据`、`金融`
- 文章末尾添加“原创声明与引用说明”，尊重第三方数据源使用条款

## 原创声明与引用
本文为原创技术文章，配套开源仓库与文档可自由学习与参考；引用或二次创作请注明来源链接与作者。外部数据源（如 `GOLDPRICE.ORG`）的使用需遵循其服务条款与许可。

## 结语
本文与配套开源项目从「数据采集 → 实时推送 → 前端可视化 → 工程化部署 → 运维与优化」构建了完整闭环，适合学习、竞赛与企业 PoC 场景。欢迎 Star、Issue、PR 一起完善！