package com.example.security;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.util.JwtUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/health",
            "/api/user/login",
            "/api/user/register",
            "/api/teachers/search",
            "/api/schedule",
            "/api/course",
            "/api/majors",
            "/api/classes",
            "/api/health",
            "/api/debug");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String path = request.getRequestURI();
            logger.debug("Processing request for path: {}", path);

            // 检查是否是公开路径
            if (isPublicPath(path)) {
                logger.debug("Public path detected: {}", path);
                // 为公开路径设置匿名认证
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                        "anonymousUser", 
                        null, 
                        new ArrayList<>()
                    )
                );
                filterChain.doFilter(request, response);
                return;
            }

            String token = getTokenFromRequest(request);
            if (token != null) {
                logger.debug("Found token: {}", token);
                try {
                    if (jwtUtil.validateToken(token)) {
                        // 从token中提取用户信息和角色
                        var claims = jwtUtil.extractClaims(token);
                        String userId = claims.get("userId", String.class);
                        String role = claims.get("role", String.class);
                        
                        logger.info("Token claims - userId: {}, role: {}", userId, role);
                        
                        // 创建包含角色的认证对象
                        var authorities = new ArrayList<SimpleGrantedAuthority>();
                        // 确保角色名称前有ROLE_前缀，并且统一使用大写形式
                        String normalizedRole = role.toUpperCase();
                        logger.info("Normalized role: {}", normalizedRole);
                        
                        String roleWithPrefix = normalizedRole.startsWith("ROLE_") ? normalizedRole : "ROLE_" + normalizedRole;
                        logger.info("Final role with prefix: {}", roleWithPrefix);
                        
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleWithPrefix);
                        logger.info("Created authority: {}", authority.getAuthority());
                        authorities.add(authority);
                        
                        var authentication = new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            authorities
                        );
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("Authentication set in SecurityContext - Principal: {}, Authorities: {}", 
                            authentication.getPrincipal(), 
                            authentication.getAuthorities().stream()
                                .map(auth -> auth.getAuthority())
                                .collect(Collectors.joining(", "))
                        );
                    } else {
                        logger.warn("Token validation failed");
                    }
                } catch (Exception e) {
                    logger.error("Error validating token: {}", e.getMessage());
                }
            } else {
                logger.warn("No token found in request");
            }
        } catch (Exception e) {
            logger.error("Could not process request: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        logger.warn("No Bearer token found in Authorization header");
        return null;
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(publicPath -> path.startsWith(publicPath));
    }
}