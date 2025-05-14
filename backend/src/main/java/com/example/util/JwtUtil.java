package com.example.util;

import com.example.entity.Admin;
import com.example.entity.Student;
import com.example.entity.StudentAccount;
import com.example.entity.Teacher;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.Base64;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        // 直接使用密钥的字节数组，不进行Base64解码
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // 确保密钥长度至少为256位（32字节）
        byte[] paddedKey = new byte[32];
        System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
        return Keys.hmacShaKeyFor(paddedKey);
    }

    public String generateToken(String userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        return createToken(claims);
    }

    public String generateToken(Admin admin, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", admin.getId());
        claims.put("role", role);
        return createToken(claims);
    }

    /**
     * 为学生实体生成令牌 - 适用于老版本
     */
    public String generateToken(Student student, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", student.getId());
        claims.put("username", student.getId());
        claims.put("role", role);
        return createToken(claims);
    }
    
    /**
     * 为学生账号实体生成令牌 - 适用于新版本
     */
    public String generateToken(StudentAccount studentAccount, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", studentAccount.getStudentId());
        claims.put("username", studentAccount.getUsername());
        claims.put("role", role);
        return createToken(claims);
    }

    public String generateToken(Teacher teacher, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", teacher.getId());
        claims.put("username", teacher.getUsername());
        claims.put("role", role);
        return createToken(claims);
    }

    private String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("\n================== JWT令牌验证失败 ==================");
            System.out.println("错误类型：令牌已过期");
            System.out.println("----------------------------------------");
            System.out.println("过期时间：" + e.getClaims().getExpiration());
            System.out.println("当前时间：" + new Date());
            long diffInDays = (new Date().getTime() - e.getClaims().getExpiration().getTime()) / (1000 * 60 * 60 * 24);
            long diffInHours = (new Date().getTime() - e.getClaims().getExpiration().getTime()) / (1000 * 60 * 60) % 24;
            long diffInMinutes = (new Date().getTime() - e.getClaims().getExpiration().getTime()) / (1000 * 60) % 60;
            System.out.println("过期时长：" + diffInDays + "天" + diffInHours + "小时" + diffInMinutes + "分钟");
            System.out.println("----------------------------------------");
            System.out.println("令牌信息：");
            System.out.println("- 用户ID：" + e.getClaims().get("userId"));
            System.out.println("- 用户角色：" + e.getClaims().get("role"));
            System.out.println("- 签发时间：" + new Date(((Long) e.getClaims().get("iat")) * 1000));
            System.out.println("================================================\n");
            e.printStackTrace();
            return false;
        } catch (JwtException e) {
            System.out.println("\n================== JWT令牌验证失败 ==================");
            System.out.println("错误类型：" + e.getMessage());
            System.out.println("----------------------------------------");
            System.out.println("令牌内容：" + token);
            System.out.println("================================================\n");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 刷新令牌
     * @param oldToken 原令牌
     * @return 新令牌
     */
    public String refreshToken(String oldToken) {
        try {
            // 解析旧令牌
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(oldToken)
                    .getBody();
            
            // 创建新的令牌，保持原有的用户信息
            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put("userId", claims.get("userId"));
            newClaims.put("role", claims.get("role"));
            if (claims.get("username") != null) {
                newClaims.put("username", claims.get("username"));
            }
            
            // 返回新令牌
            return createToken(newClaims);
        } catch (ExpiredJwtException e) {
            // 即使令牌过期，仍然可以从中提取信息
            Claims claims = e.getClaims();
            
            // 创建新的令牌
            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put("userId", claims.get("userId"));
            newClaims.put("role", claims.get("role"));
            if (claims.get("username") != null) {
                newClaims.put("username", claims.get("username"));
            }
            
            return createToken(newClaims);
        } catch (JwtException e) {
            System.out.println("\n================== JWT令牌刷新失败 ==================");
            System.out.println("错误类型：" + e.getMessage());
            System.out.println("----------------------------------------");
            System.out.println("原令牌：" + oldToken);
            System.out.println("================================================\n");
            throw new RuntimeException("无法刷新令牌：" + e.getMessage());
        }
    }

    /**
     * 检查令牌是否需要刷新
     * @param token 令牌
     * @return 如果令牌将在30分钟内过期，返回true
     */
    public boolean shouldRefreshToken(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expirationDate = claims.getExpiration();
            Date now = new Date();
            
            // 如果令牌将在30分钟内过期，返回true
            long thirtyMinutesInMillis = 30 * 60 * 1000;
            return (expirationDate.getTime() - now.getTime()) < thirtyMinutesInMillis;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}