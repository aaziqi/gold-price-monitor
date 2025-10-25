package com.goldmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 黄金价格监控平台主启动类
 * 
 * @author Gold Monitor Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class GoldPriceMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoldPriceMonitorApplication.class, args);
        System.out.println("🚀 黄金价格监控平台启动成功！");
        System.out.println("📊 访问地址: http://localhost:8080");
        System.out.println("🔗 WebSocket: ws://localhost:8080/ws/gold-price");
    }
}