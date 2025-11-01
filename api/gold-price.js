// Vercel Serverless Function for Gold Price API
import fetch from 'node-fetch';

// 模拟黄金价格数据源
const GOLD_PRICE_SOURCES = {
  // 使用免费的金融API
  'metals-api': 'https://api.metals.live/v1/spot/gold',
  // 备用数据源
  'fallback': {
    price: 2000 + Math.random() * 100, // 模拟价格波动
    currency: 'USD',
    unit: 'oz'
  }
};

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
    let goldPrice;
    
    try {
      // 尝试从真实API获取数据
      const response = await fetch(GOLD_PRICE_SOURCES['metals-api'], {
        timeout: 5000
      });
      
      if (response.ok) {
        const data = await response.json();
        goldPrice = {
          price: data.price || data.gold || data.value,
          currency: 'USD',
          unit: 'oz',
          timestamp: new Date().toISOString(),
          source: 'metals-api'
        };
      } else {
        throw new Error('API response not ok');
      }
    } catch (error) {
      // 使用备用数据
      console.log('Using fallback data:', error.message);
      goldPrice = {
        ...GOLD_PRICE_SOURCES.fallback,
        timestamp: new Date().toISOString(),
        source: 'fallback',
        price: 2000 + Math.random() * 100 // 模拟实时价格
      };
    }

    // 添加一些历史数据模拟
    const historicalData = [];
    const now = new Date();
    for (let i = 23; i >= 0; i--) {
      const time = new Date(now.getTime() - i * 60 * 60 * 1000);
      historicalData.push({
        timestamp: time.toISOString(),
        price: goldPrice.price + (Math.random() - 0.5) * 50,
        volume: Math.floor(Math.random() * 1000) + 500
      });
    }

    const response = {
      success: true,
      data: {
        current: goldPrice,
        historical: historicalData,
        metadata: {
          lastUpdated: new Date().toISOString(),
          source: goldPrice.source,
          currency: 'USD',
          unit: 'oz'
        }
      }
    };

    res.status(200).json(response);
  } catch (error) {
    console.error('Gold price API error:', error);
    res.status(500).json({
      success: false,
      error: 'Failed to fetch gold price data',
      message: error.message
    });
  }
}