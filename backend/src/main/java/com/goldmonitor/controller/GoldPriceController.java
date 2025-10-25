package com.goldmonitor.controller;

import com.goldmonitor.model.GoldPrice;
import com.goldmonitor.scheduler.GoldPriceScheduler;
import com.goldmonitor.service.GoldPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 黄金价格控制器
 * 提供黄金价格相关的REST API接口
 * 
 * @author Gold Monitor Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/gold")
public class GoldPriceController {

    @Autowired
    private GoldPriceService goldPriceService;
    
    @Autowired
    private GoldPriceScheduler goldPriceScheduler;

    /**
     * 获取当前黄金价格
     * 
     * @return 当前黄金价格
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentPrice() {
        try {
            GoldPrice goldPrice = goldPriceService.getCurrentGoldPrice().block();
            
            Map<String, Object> response = new HashMap<>();
            if (goldPrice != null) {
                response.put("success", true);
                response.put("data", goldPrice);
            } else {
                response.put("success", false);
                response.put("message", "无法获取黄金价格数据");
            }
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取当前价格失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 手动刷新黄金价格
     * 
     * @return 刷新结果
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshPrice() {
        try {
            goldPriceScheduler.manualUpdate();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "价格刷新成功");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "手动刷新价格失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取系统状态
     * 
     * @return 系统状态信息
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            
            status.put("service", "Gold Price Monitor");
            status.put("version", "1.0.0");
            status.put("status", "running");
            status.put("marketOpen", goldPriceService.isMarketOpen());
            status.put("updateInterval", goldPriceScheduler.getUpdateInterval() + " seconds");
            status.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取系统状态失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 健康检查接口
     * 
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Gold Price Monitor");
        return ResponseEntity.ok(health);
    }
}