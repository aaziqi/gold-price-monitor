package com.goldmonitor.scheduler;

import com.goldmonitor.model.GoldPrice;
import com.goldmonitor.service.GoldPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 黄金价格定时调度器
 * 定期获取黄金价格并通过WebSocket推送给前端
 * 
 * @author Gold Monitor Team
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoldPriceScheduler {

    private final GoldPriceService goldPriceService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Value("${gold.scheduler.price-update-interval:30}")
    private int updateInterval;
    
    @Value("${gold.scheduler.force-enabled:false}")
    private boolean forceEnabled;

    /**
     * 定时获取黄金价格
     * 每30秒执行一次（可配置）
     */
    @Scheduled(fixedDelayString = "${gold.scheduler.price-update-interval:30}000",
               initialDelayString = "${gold.scheduler.initial-delay:5}000")
    public void fetchAndBroadcastGoldPrice() {
        log.debug("开始执行定时任务：获取黄金价格");
        
        try {
            // 检查市场状态（演示模式下强制启用）
            if (!forceEnabled && !goldPriceService.isMarketOpen()) {
                log.debug("市场已关闭，跳过价格更新");
                return;
            }
            
            // 获取黄金价格
            goldPriceService.getCurrentGoldPrice()
                    .subscribe(
                            this::broadcastGoldPrice,
                            error -> log.error("获取黄金价格失败: {}", error.getMessage())
                    );
                    
        } catch (Exception e) {
            log.error("定时任务执行异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 广播黄金价格到所有连接的客户端
     * 
     * @param goldPrice 黄金价格对象
     */
    private void broadcastGoldPrice(GoldPrice goldPrice) {
        try {
            // 发送到主题，所有订阅的客户端都会收到
            messagingTemplate.convertAndSend("/topic/gold-price", goldPrice);
            
            log.info("成功广播黄金价格: ${}/oz (变化: {}%)", 
                    goldPrice.getPrice(), 
                    goldPrice.getChangePercent());
                    
        } catch (Exception e) {
            log.error("广播黄金价格失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 手动触发价格更新
     * 可用于测试或手动刷新
     */
    public void manualUpdate() {
        log.info("手动触发价格更新");
        fetchAndBroadcastGoldPrice();
    }

    /**
     * 获取更新间隔
     * 
     * @return 更新间隔（秒）
     */
    public int getUpdateInterval() {
        return updateInterval;
    }
}