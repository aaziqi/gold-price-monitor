package com.goldmonitor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 最简单的控制器
 * 不依赖任何服务，用于测试基本的Spring MVC功能
 */
@RestController
@RequestMapping("/simple")
public class SimpleController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Simple controller test successful");
    }

    @GetMapping("/info")
    public ResponseEntity<Object> info() {
        return ResponseEntity.ok(new Object() {
            public final String message = "Simple controller info";
            public final long timestamp = System.currentTimeMillis();
            public final String status = "OK";
        });
    }
}