// Vercel Serverless Function for Market Data API
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
    const { timeframe = '24h' } = req.query;
    
    // 生成模拟市场数据
    const generateMarketData = (hours) => {
      const data = [];
      const basePrice = 2000;
      const now = new Date();
      
      for (let i = hours - 1; i >= 0; i--) {
        const timestamp = new Date(now.getTime() - i * 60 * 60 * 1000);
        const price = basePrice + Math.sin(i * 0.1) * 50 + (Math.random() - 0.5) * 20;
        
        data.push({
          timestamp: timestamp.toISOString(),
          open: price + (Math.random() - 0.5) * 5,
          high: price + Math.random() * 10,
          low: price - Math.random() * 10,
          close: price,
          volume: Math.floor(Math.random() * 1000) + 500,
          change: (Math.random() - 0.5) * 4,
          changePercent: (Math.random() - 0.5) * 0.2
        });
      }
      
      return data;
    };

    let hours;
    switch (timeframe) {
      case '1h':
        hours = 1;
        break;
      case '6h':
        hours = 6;
        break;
      case '24h':
        hours = 24;
        break;
      case '7d':
        hours = 24 * 7;
        break;
      case '30d':
        hours = 24 * 30;
        break;
      default:
        hours = 24;
    }

    const marketData = generateMarketData(hours);
    
    // 计算统计数据
    const prices = marketData.map(d => d.close);
    const volumes = marketData.map(d => d.volume);
    
    const statistics = {
      high24h: Math.max(...prices),
      low24h: Math.min(...prices),
      volume24h: volumes.reduce((sum, vol) => sum + vol, 0),
      avgPrice: prices.reduce((sum, price) => sum + price, 0) / prices.length,
      priceChange: prices[prices.length - 1] - prices[0],
      priceChangePercent: ((prices[prices.length - 1] - prices[0]) / prices[0]) * 100
    };

    const response = {
      success: true,
      data: {
        timeframe,
        marketData,
        statistics,
        metadata: {
          lastUpdated: new Date().toISOString(),
          dataPoints: marketData.length,
          currency: 'USD',
          unit: 'oz'
        }
      }
    };

    res.status(200).json(response);
  } catch (error) {
    console.error('Market data API error:', error);
    res.status(500).json({
      success: false,
      error: 'Failed to fetch market data',
      message: error.message
    });
  }
}