# 从零搭建「实时黄金价格监控与可视化平台」：架构、实现与生产部署全解析

> 仓库地址： https://github.com/aaziqi/gold-price-monitor

## 项目背景
全球金融市场瞬息万变，黄金价格作为重要的避险指标，具有高频波动和强实时性。本项目旨在构建一套可生产部署的「实时黄金价格监控与可视化平台」，支持分钟级采集、毫秒级推送、图表化展示、告警通知与稳定运维。

## 目标与能力
- 实时数据：后端定时采集黄金价格，并通过 WebSocket 推送到前端。
- 可视化展示：基于 ECharts 构建实时曲线、K 线、数据源标注与状态提示。
- 稳健架构：后端 Spring Boot + Scheduler + WebSocket；前端 Vue3 + Pinia + Vite。
- 工程化与可部署：提供 Docker 化、Nginx 反向代理、SSL 部署、监控与日志方案。
- 可拓展：支持多数据源、阈值告警、持久化与历史回放。

## 技术栈选择
- 后端：`Spring Boot`、`WebSocket`、`Scheduler`、`JDK 17+`、`Maven`
- 前端：`Vue 3`、`Pinia`、`ECharts`、`TailwindCSS`、`Vite`
- 部署：`Docker`、`Nginx`、`SSL/TLS`、`Prometheus/Grafana`

## 架构总览
- 数据采集：定时任务抓取第三方接口（如 `GOLDPRICE.ORG`），清洗与标准化。
- 推送通道：后端维护 WebSocket 会话，增量推送最新行情与数据源信息。
- 前端订阅：建立 WebSocket 连接，更新状态、绘制图表、展示数据来源。
- 配置管理：`application.yml`、`.env`、`docker-compose.yml` 支持环境化配置。
- 监控与告警：通过 `Actuator` 与 `Prometheus` 对关键指标进行采集与告警。

## 核心实现
### 1. 实时推送
- 定时器从 API 拉取 `price`、`timestamp`、`source` 字段。
- 通过 WebSocket 向所有在线会话广播消息，前端订阅并更新视图。

### 2. 前端可视化
- 使用 Pinia 管理行情状态，ECharts 渲染折线/趋势图。
- `Dashboard.vue` 动态展示数据源（如 `GOLDPRICE.ORG`），替换原硬编码 Mock。

### 3. 工程化与部署
- 本地快速启动：`backend` 与 `frontend` 目录分别运行开发服务器。
- Docker 化：`docker-compose up -d` 一键启动后端/前端与 Nginx 网关。
- 生产部署：Nginx 反向代理、HTTPS、健康检查、日志轮转、监控集成。

## 关键代码片段
> 详细代码与接口说明见仓库 `README.md` 与 `docs/TECHNICAL_OVERVIEW.md`

- WebSocket 推送：后端将最新行情打包为 JSON 并广播到会话列表。
- 数据源标注：后端响应包含 `source` 字段，前端直接读取展示，实时同步。

## 部署实战
- 准备环境：`JDK 17+`、`Node 18+`、`Docker`、`docker-compose`、`Nginx`
- 本地开发：后端 `mvn spring-boot:run`，前端 `pnpm dev` 或 `npm run dev`
- 一键部署：`docker-compose up -d`，访问网关域名检查前后端健康状态。
- 生产加固：仅开放必要端口、开启 HTTPS、设置速率限制与请求头校验。

## 性能优化
- 请求限频与缓存：减少第三方 API 调用次数，使用本地缓存。
- 异步推送：WebSocket 广播采用非阻塞策略，避免阻塞采集线程。
- 前端优化：ECharts 数据批量更新、虚拟滚动、动画降采样。

## 未来规划
- 多数据源融合与一致性处理
- 历史数据持久化与回放
- 用户订阅与告警策略中心
- 更全面的监控与可观测性方案

## 总结
该平台从「数据采集→实时推送→前端可视化→工程化部署」形成闭环，具备生产落地能力与良好扩展性。欢迎在 GitHub 提交 Issue 或 PR 参与共建。

—

- 仓库地址： https://github.com/aaziqi/gold-price-monitor
- 文档索引：参见项目 `README.md` 与 `docs/TECHNICAL_OVERVIEW.md`