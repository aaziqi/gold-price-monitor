# API 文档

## 概述

黄金价格监控平台提供了一套完整的 RESTful API 和 WebSocket 接口，用于获取实时黄金价格数据和系统状态信息。

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **WebSocket URL**: `ws://localhost:8080/ws/gold-price`

## REST API 接口

### 1. 获取当前黄金价格

获取最新的黄金价格信息。

**请求**
```http
GET /api/gold/current
```

**响应**
```json
{
  "id": "1703123456789",
  "price": 2045.67,
  "currency": "USD",
  "unit": "oz",
  "change": 12.34,
  "changePercent": 0.61,
  "timestamp": "2024-01-01T12:00:00",
  "source": "API",
  "marketStatus": "OPEN"
}
```

**响应字段说明**

| 字段 | 类型 | 描述 |
|------|------|------|
| `id` | String | 价格记录唯一标识 |
| `price` | Number | 黄金价格 |
| `currency` | String | 货币单位 (USD) |
| `unit` | String | 重量单位 (oz) |
| `change` | Number | 价格变化金额 |
| `changePercent` | Number | 价格变化百分比 |
| `timestamp` | String | 数据时间戳 (ISO 8601) |
| `source` | String | 数据来源 |
| `marketStatus` | String | 市场状态 (OPEN/CLOSED) |

### 2. 手动刷新价格

触发手动价格更新。

**请求**
```http
POST /api/gold/refresh
```

**响应**
```json
{
  "success": true,
  "message": "价格刷新请求已提交",
  "timestamp": 1703123456789
}
```

### 3. 获取系统状态

获取系统运行状态和配置信息。

**请求**
```http
GET /api/gold/status
```

**响应**
```json
{
  "service": "Gold Price Monitor",
  "version": "1.0.0",
  "status": "running",
  "marketOpen": true,
  "updateInterval": 30,
  "timestamp": 1703123456789
}
```

### 4. 健康检查

检查服务健康状态。

**请求**
```http
GET /api/gold/health
```

**响应**
```json
{
  "status": "UP",
  "service": "gold-price-monitor"
}
```

## WebSocket 接口

### 连接建立

使用 STOMP 协议连接到 WebSocket 服务器。

**连接URL**
```
ws://localhost:8080/ws/gold-price
```

**JavaScript 示例**
```javascript
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const client = new Client({
  webSocketFactory: () => new SockJS('/ws/gold-price'),
  connectHeaders: {},
  debug: (str) => console.log(str),
  reconnectDelay: 5000,
  heartbeatIncoming: 4000,
  heartbeatOutgoing: 4000,
});

client.onConnect = (frame) => {
  console.log('Connected:', frame);
  
  // 订阅价格更新
  client.subscribe('/topic/gold-price', (message) => {
    const priceData = JSON.parse(message.body);
    console.log('Price update:', priceData);
  });
};

client.onStompError = (frame) => {
  console.error('Broker reported error:', frame.headers['message']);
  console.error('Additional details:', frame.body);
};

client.activate();
```

### 订阅主题

#### 1. 黄金价格更新

订阅实时价格更新推送。

**主题**: `/topic/gold-price`

**消息格式**
```json
{
  "id": "1703123456789",
  "price": 2045.67,
  "currency": "USD",
  "unit": "oz",
  "change": 12.34,
  "changePercent": 0.61,
  "timestamp": "2024-01-01T12:00:00",
  "source": "API",
  "marketStatus": "OPEN"
}
```

#### 2. 系统消息

接收系统通知和状态消息。

**主题**: `/topic/system`

**消息格式**
```json
{
  "type": "notification",
  "message": "系统维护通知",
  "timestamp": "2024-01-01T12:00:00",
  "level": "info"
}
```

### 发送消息

#### 1. 订阅价格更新

向服务器发送订阅请求。

**目标**: `/app/gold-price/subscribe`

**消息格式**
```json
{
  "action": "subscribe",
  "clientId": "client-123",
  "preferences": {
    "currency": "USD",
    "updateInterval": 30
  }
}
```

#### 2. 心跳检测

发送心跳消息保持连接。

**目标**: `/app/gold-price/ping`

**消息格式**
```json
{
  "timestamp": 1703123456789
}
```

## 错误处理

### HTTP 错误码

| 状态码 | 描述 | 示例 |
|--------|------|------|
| 200 | 成功 | 请求成功处理 |
| 400 | 请求错误 | 参数格式错误 |
| 404 | 资源不存在 | 接口不存在 |
| 500 | 服务器错误 | 内部服务错误 |
| 503 | 服务不可用 | 服务维护中 |

### 错误响应格式

```json
{
  "error": {
    "code": "PRICE_FETCH_ERROR",
    "message": "获取价格数据失败",
    "details": "API服务暂时不可用",
    "timestamp": "2024-01-01T12:00:00"
  }
}
```

### WebSocket 错误

WebSocket 连接错误会通过 `onStompError` 回调处理：

```javascript
client.onStompError = (frame) => {
  console.error('STOMP Error:', frame.headers['message']);
  console.error('Details:', frame.body);
  
  // 处理特定错误
  switch (frame.headers['message']) {
    case 'Authentication failed':
      // 处理认证失败
      break;
    case 'Subscription limit exceeded':
      // 处理订阅限制
      break;
    default:
      // 处理其他错误
      break;
  }
};
```

## 限流和配额

### API 限流

- **频率限制**: 每分钟最多 60 次请求
- **并发限制**: 每个 IP 最多 10 个并发连接
- **数据限制**: 单次响应最大 1MB

### WebSocket 限制

- **连接限制**: 每个 IP 最多 5 个 WebSocket 连接
- **消息频率**: 每秒最多 10 条消息
- **订阅限制**: 每个连接最多订阅 10 个主题

## 认证和授权

当前版本为开放访问，未来版本将支持：

- **API Key 认证**: 通过 `X-API-Key` 头部认证
- **JWT Token**: 基于 JWT 的用户认证
- **OAuth 2.0**: 第三方应用授权

## 示例代码

### JavaScript/Node.js

```javascript
// REST API 调用
async function getCurrentPrice() {
  try {
    const response = await fetch('/api/gold/current');
    const data = await response.json();
    console.log('Current price:', data.price);
    return data;
  } catch (error) {
    console.error('Error fetching price:', error);
  }
}

// WebSocket 连接
function connectWebSocket() {
  const client = new Client({
    webSocketFactory: () => new SockJS('/ws/gold-price'),
    onConnect: () => {
      client.subscribe('/topic/gold-price', (message) => {
        const price = JSON.parse(message.body);
        updateUI(price);
      });
    }
  });
  
  client.activate();
  return client;
}
```

### Python

```python
import requests
import websocket
import json

# REST API 调用
def get_current_price():
    try:
        response = requests.get('http://localhost:8080/api/gold/current')
        response.raise_for_status()
        return response.json()
    except requests.RequestException as e:
        print(f'Error: {e}')
        return None

# WebSocket 连接
def on_message(ws, message):
    data = json.loads(message)
    print(f"Price update: {data['price']}")

def on_error(ws, error):
    print(f"WebSocket error: {error}")

def connect_websocket():
    ws = websocket.WebSocketApp(
        "ws://localhost:8080/ws/gold-price",
        on_message=on_message,
        on_error=on_error
    )
    ws.run_forever()
```

### cURL

```bash
# 获取当前价格
curl -X GET http://localhost:8080/api/gold/current

# 手动刷新价格
curl -X POST http://localhost:8080/api/gold/refresh

# 健康检查
curl -X GET http://localhost:8080/api/gold/health
```

## 版本历史

### v1.0.0 (2024-01-01)
- 初始版本发布
- 基础 REST API 接口
- WebSocket 实时推送
- 价格数据获取和展示

### 未来版本计划

- **v1.1.0**: 添加用户认证和授权
- **v1.2.0**: 支持多货币和多商品
- **v1.3.0**: 添加价格预警功能
- **v2.0.0**: 重构 API，支持 GraphQL