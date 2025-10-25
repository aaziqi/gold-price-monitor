package com.goldmonitor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 用于验证基本的REST API功能
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> hello() {
        try {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Hello World");
            response.put("status", "OK");
            response.put("timestamp", String.valueOf(System.currentTimeMillis()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "ERROR");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/simple")
    public ResponseEntity<String> simple() {
        try {
            String response = "Simple test response at " + System.currentTimeMillis();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("service", "gold-price-monitor");
            response.put("status", "running");
            response.put("timestamp", System.currentTimeMillis());
            response.put("java_version", System.getProperty("java.version"));
            response.put("spring_profiles", System.getProperty("spring.profiles.active", "default"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "ERROR");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}