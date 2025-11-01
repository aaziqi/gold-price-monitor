// Vercel Serverless Function for WebSocket simulation
// 注意：Vercel不支持真正的WebSocket，这里提供轮询替代方案

export default async function handler(req, res) {
  // 设置CORS头
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    res.status(200).end();
    return;
  }

  try {
    // 模拟实时数据推送
    const generateRealtimeData = () => {
      const basePrice = 2000;
      const currentPrice = basePrice + (Math.random() - 0.5) * 100;
      const change = (Math.random() - 0.5) * 5;
      
      return {
        type: 'price_update',
        data: {
          price: currentPrice,
          change: change,
          changePercent: (change / currentPrice) * 100,
          volume: Math.floor(Math.random() * 1000) + 500,
          timestamp: new Date().toISOString(),
          bid: currentPrice - 0.5,
          ask: currentPrice + 0.5,
          spread: 1.0
        }
      };
    };

    // 生成市场事件
    const generateMarketEvent = () => {
      const events = [
        'price_alert',
        'volume_spike',
        'market_news',
        'technical_indicator'
      ];
      
      const eventType = events[Math.floor(Math.random() * events.length)];
      
      return {
        type: 'market_event',
        eventType,
        data: {
          message: `Market event: ${eventType}`,
          severity: Math.random() > 0.7 ? 'high' : 'normal',
          timestamp: new Date().toISOString()
        }
      };
    };

    // 根据请求类型返回不同数据
    const { type = 'price' } = req.query;
    
    let responseData;
    
    switch (type) {
      case 'price':
        responseData = generateRealtimeData();
        break;
      case 'event':
        responseData = generateMarketEvent();
        break;
      case 'heartbeat':
        responseData = {
          type: 'heartbeat',
          data: {
            status: 'connected',
            timestamp: new Date().toISOString(),
            server: 'vercel-serverless'
          }
        };
        break;
      default:
        responseData = generateRealtimeData();
    }

    const response = {
      success: true,
      ...responseData,
      metadata: {
        endpoint: 'websocket-simulation',
        timestamp: new Date().toISOString(),
        note: 'This is a polling-based WebSocket simulation for Vercel deployment'
      }
    };

    res.status(200).json(response);
  } catch (error) {
    console.error('WebSocket simulation error:', error);
    res.status(500).json({
      success: false,
      error: 'Failed to generate realtime data',
      message: error.message
    });
  }
}