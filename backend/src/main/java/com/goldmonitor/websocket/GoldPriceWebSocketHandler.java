package com.goldmonitor.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 黄金价格WebSocket处理器
 * 处理WebSocket连接和消息传递
 * 
 * @author Gold Monitor Team
 * @version 1.0.0
 */
@Slf4j
@Controller
public class GoldPriceWebSocketHandler {

    /**
     * 处理客户端订阅黄金价格主题
     * 
     * @return 欢迎消息
     */
    @SubscribeMapping("/topic/gold-price")
    public Map<String, Object> handleSubscription() {
        log.info("新客户端订阅黄金价格主题");
        
        Map<String, Object> welcomeMessage = new HashMap<>();
        welcomeMessage.put("type", "welcome");
        welcomeMessage.put("message", "欢迎订阅黄金价格实时数据");
        welcomeMessage.put("timestamp", LocalDateTime.now());
        welcomeMessage.put("status", "connected");
        
        return welcomeMessage;
    }

    /**
     * 处理客户端发送的消息
     * 
     * @param message 客户端消息
     * @return 响应消息
     */
    @MessageMapping("/gold-price/subscribe")
    @SendTo("/topic/gold-price")
    public Map<String, Object> handleClientMessage(Map<String, Object> message) {
        log.info("收到客户端消息: {}", message);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "response");
        response.put("message", "已收到您的订阅请求");
        response.put("clientMessage", message);
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * 处理心跳检测
     * 
     * @return 心跳响应
     */
    @MessageMapping("/gold-price/ping")
    @SendTo("/topic/gold-price/pong")
    public Map<String, Object> handlePing() {
        log.debug("收到心跳检测");
        
        Map<String, Object> pong = new HashMap<>();
        pong.put("type", "pong");
        pong.put("timestamp", LocalDateTime.now());
        pong.put("server", "gold-price-monitor");
        
        return pong;
    }
}