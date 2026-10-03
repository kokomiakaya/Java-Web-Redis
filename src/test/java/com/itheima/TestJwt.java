package com.itheima;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class TestJwt {

    // JWT令牌的构成
// 1.header 头,记录令牌类型、签名算法
// 2.payload 有效载荷，携带一些自定义信息、默认信息等
// 3.signature 签名，防止token被篡改、确保安全性
//  利用密钥对前两个部分进行签名，帮助服务器判断 Token 是否被篡改。
    @Test
    public void testGenJwt() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 10);
        claims.put("username", "itheima");

//        Jwts.builder()：创建 JWT 构建器
        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, "aXRjYXN0")
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 12 * 3600 * 1000))
                .compact();
        // compact()：生成 JWT
        System.out.println(jwt);
    }

    @Test
    public void testParseJwt() {
//        Claims 本质上是一种特殊的 Map，它继承了 Map<String, Object>，除了可以存储和获取键值对，还提供了获取 JWT 标准声明的方法。
        Claims claims = Jwts.parser().setSigningKey("aXRjYXN0")
                .parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MTAsInVzZXJuYW1lIjoiaXRoZWltYSIsImV4cCI6MTc5MTA2OTQxNn0.0hKhwkHFK_cPBmMoxonYrNBK6DEqUPmfISTa4YXSEAI")
                .getBody();

        System.out.println(claims);
    }

    @Test
    public void genJwt(){
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 10);
        claims.put("username", "itheima");

        String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS256, "aXRjYXN0")
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 1000)) //有效期60s
                .compact();
        System.out.println(jwt);
        //输出结果：eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MTAsInVzZXJuYW1lIjoiaXRoZWltYSIsImV4cCI6MTc5MTAyNjc2Mn0.3l3XwfoFUzVwY8TLEE0_9FMD1NxEA1b4ecGslVAod7g
    }

//    @Test
//    public void parseJwt(){
//        Claims claims = Jwts.parser()
//                .setSigningKey("aXRjYXN0")//指定签名密钥
//                .parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MTAsInVzZXJuYW1lIjoiaXRoZWltYSIsImV4cCI6MTc5MTAyNjc2Mn0.3l3XwfoFUzVwY8TLEE0_9FMD1NxEA1b4ecGslVAod7g")
//                .getBody();
//
//        System.out.println(claims);
//    }


}

