package com.example.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 调试用控制器
 */
@RestController
@RequestMapping("/api/debug")
public class DebugController {

    /**
     * 获取当前用户的认证信息
     */
    @GetMapping("/auth-info")
    public Map<String, Object> getCurrentAuthInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> info = new HashMap<>();
        
        if (authentication != null) {
            info.put("name", authentication.getName());
            info.put("principal", authentication.getPrincipal().toString());
            info.put("authenticated", authentication.isAuthenticated());
            info.put("authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            info.put("details", authentication.getDetails() != null ? 
                    authentication.getDetails().toString() : null);
        } else {
            info.put("error", "No authentication found in SecurityContext");
        }
        
        return info;
    }
} 