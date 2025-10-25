package com.goldmonitor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 配置类
 * 用于配置 STOMP 协议的 WebSocket 连接
 * 
 * @author Gold Monitor Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 配置消息代理
     * 
     * @param config 消息代理注册器
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单的消息代理，并设置消息代理的前缀
        config.enableSimpleBroker("/topic", "/queue");
        
        // 设置应用程序的目标前缀
        config.setApplicationDestinationPrefixes("/app");
        
        // 设置用户目标前缀
        config.setUserDestinationPrefix("/user");
    }

    /**
     * 注册 STOMP 端点
     * 
     * @param registry STOMP 端点注册器
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册 WebSocket 端点
        registry.addEndpoint("/ws/gold-price")
                .setAllowedOriginPatterns("*")  // 允许跨域
                .withSockJS();  // 启用 SockJS 支持
        
        // 注册原生 WebSocket 端点（不使用 SockJS）
        registry.addEndpoint("/ws/gold-price")
                .setAllowedOriginPatterns("*");
    }
}