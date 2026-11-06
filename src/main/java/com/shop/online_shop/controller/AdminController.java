package com.shop.online_shop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/test")
    public Map<String, String> testAdmin() {
        return Map.of(
            "message", "Bạn có quyền ADMIN!",
            "status", "SUCCESS"
        );
    }
}