package com.goldmonitor.service;

import com.goldmonitor.model.GoldPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 黄金价格服务类
 * 负责从外部API获取黄金价格数据
 * 
 * @author Gold Monitor Team
 * @version 1.0.0
 */
@Slf4j
@Service
public class GoldPriceService {

    private final WebClient webClient;
    private final String apiUrl;
    private final String apiKey;
    private final Random random = new Random();

    public GoldPriceService(WebClient.Builder webClientBuilder,
                           @Value("${gold.api.url}") String apiUrl,
                           @Value("${gold.api.key}") String apiKey) {
        this.webClient = webClientBuilder.build();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    /**
     * 获取当前黄金价格
     * 
     * @return 黄金价格对象
     */
    public Mono<GoldPrice> getCurrentGoldPrice() {
        log.debug("开始获取黄金价格数据...");
        
        // 如果是演示模式，生成模拟数据
        if ("demo_key".equals(apiKey)) {
            return generateMockGoldPrice();
        }
        
        // 从真实API获取数据
        return fetchFromApi()
                .doOnSuccess(price -> log.info("成功获取黄金价格: ${}", price.getPrice()))
                .doOnError(error -> log.error("获取黄金价格失败: {}", error.getMessage()))
                .onErrorResume(throwable -> {
                    log.warn("API调用失败，使用模拟数据作为降级方案: {}", throwable.getMessage());
                    return generateMockGoldPrice();
                });
    }

    /**
     * 从外部API获取黄金价格
     * 
     * @return 黄金价格对象
     */
    private Mono<GoldPrice> fetchFromApi() {
        return webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::parseGoldApiResponse)
                .timeout(java.time.Duration.ofSeconds(10));
    }

    /**
     * 解析goldprice.org的响应数据
     * 
     * @param response API响应
     * @return 黄金价格对象
     */
    private GoldPrice parseGoldApiResponse(Map<String, Object> response) {
        try {
            // goldprice.org 返回格式: {"items": [{"xauPrice": 2050.30, "xagPrice": 25.40, "curr": "USD", "chgXau": 5.20, "chgXag": -0.30, "pcXau": 0.25, "pcXag": -1.18, "xauClose": 2045.10, "xagClose": 25.70, "timestamp": 1698765432}]}
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
            
            if (items == null || items.isEmpty()) {
                throw new RuntimeException("API响应中没有找到价格数据");
            }
            
            Map<String, Object> priceData = items.get(0);
            BigDecimal price = new BigDecimal(priceData.get("xauPrice").toString());
            String currency = priceData.getOrDefault("curr", "USD").toString();
            
            // 获取价格变化数据
            BigDecimal change = new BigDecimal(priceData.getOrDefault("chgXau", "0").toString());
            BigDecimal changePercent = new BigDecimal(priceData.getOrDefault("pcXau", "0").toString());
            
            return GoldPrice.builder()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .price(price)
                    .currency(currency)
                    .unit("oz")
                    .change(change)
                    .changePercent(changePercent)
                    .timestamp(LocalDateTime.now())
                    .source("GOLDPRICE.ORG")
                    .marketStatus(isMarketOpen() ? "OPEN" : "CLOSED")
                    .build();
                    
        } catch (Exception e) {
            log.error("解析API响应失败: {}", e.getMessage());
            throw new RuntimeException("解析黄金价格数据失败", e);
        }
    }
    
    /**
     * 计算价格变化（简单模拟，因为免费API可能不提供历史对比）
     * 
     * @param currentPrice 当前价格
     * @return 价格变化
     */
    private BigDecimal calculatePriceChange(BigDecimal currentPrice) {
        // 生成-1%到+1%的随机变化
        double changePercent = (random.nextDouble() - 0.5) * 2; // -1 到 1
        return currentPrice.multiply(BigDecimal.valueOf(changePercent / 100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 生成模拟黄金价格数据
     * 用于演示和测试
     * 
     * @return 模拟的黄金价格对象
     */
    private Mono<GoldPrice> generateMockGoldPrice() {
        // 基础价格：2000美元/盎司左右
        double basePrice = 2000.0;
        
        // 生成±50美元的随机波动
        double variation = (random.nextDouble() - 0.5) * 100;
        BigDecimal currentPrice = BigDecimal.valueOf(basePrice + variation)
                .setScale(2, RoundingMode.HALF_UP);
        
        // 计算价格变化（模拟）
        BigDecimal change = BigDecimal.valueOf((random.nextDouble() - 0.5) * 20)
                .setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal changePercent = change.divide(currentPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        
        GoldPrice goldPrice = GoldPrice.builder()
                .id(String.valueOf(System.currentTimeMillis()))
                .price(currentPrice)
                .currency("USD")
                .unit("oz")
                .change(change)
                .changePercent(changePercent)
                .timestamp(LocalDateTime.now())
                .source("MOCK")
                .marketStatus("OPEN")
                .build();
        
        log.debug("生成模拟黄金价格: ${}/oz", currentPrice);
        return Mono.just(goldPrice);
    }

    /**
     * 检查市场状态
     * 
     * @return 市场是否开放
     */
    public boolean isMarketOpen() {
        // 简单的市场开放时间检查
        // 实际应用中应该考虑时区和节假日
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int dayOfWeek = now.getDayOfWeek().getValue();
        
        // 周一到周五，早8点到晚8点
        return dayOfWeek >= 1 && dayOfWeek <= 5 && hour >= 8 && hour <= 20;
    }
}