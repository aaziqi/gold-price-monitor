package com.goldmonitor.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 黄金价格数据模型
 * 
 * @author Gold Monitor Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldPrice {
    
    /**
     * 价格ID
     */
    private String id;
    
    /**
     * 黄金价格（美元/盎司）
     */
    private BigDecimal price;
    
    /**
     * 货币单位
     */
    private String currency;
    
    /**
     * 重量单位
     */
    private String unit;
    
    /**
     * 价格变化
     */
    private BigDecimal change;
    
    /**
     * 价格变化百分比
     */
    private BigDecimal changePercent;
    
    /**
     * 数据时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * 数据来源
     */
    private String source;
    
    /**
     * 市场状态（开市/闭市）
     */
    private String marketStatus;
    
    /**
     * 创建当前时间戳的黄金价格对象
     */
    public static GoldPrice createWithCurrentTime(BigDecimal price, String currency, String unit) {
        return GoldPrice.builder()
                .price(price)
                .currency(currency)
                .unit(unit)
                .timestamp(LocalDateTime.now())
                .source("API")
                .marketStatus("OPEN")
                .build();
    }
}