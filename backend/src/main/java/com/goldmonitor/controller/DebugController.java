package com.goldmonitor.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> debugTest(HttpServletRequest request) {
        log.info("=== 调试控制器被调用 ===");
        log.info("请求URI: {}", request.getRequestURI());
        log.info("请求方法: {}", request.getMethod());
        log.info("请求参数: {}", request.getQueryString());
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "调试控制器正常工作");
            response.put("timestamp", System.currentTimeMillis());
            response.put("requestUri", request.getRequestURI());
            
            log.info("调试控制器响应: {}", response);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("调试控制器异常: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("exception", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/simple")
    public String simpleDebug() {
        log.info("=== 简单调试接口被调用 ===");
        return "DEBUG_OK";
    }
}