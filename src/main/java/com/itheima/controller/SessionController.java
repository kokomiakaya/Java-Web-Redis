package com.itheima.controller;

import com.itheima.pojo.Result;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SessionController {
    // 设置Cookie
    @GetMapping("/c1")
    public com.itheima.pojo.Result cookie1(HttpServletResponse response){
//        通过HTTP响应头将其发送给客户端
        response.addCookie(new Cookie("login_username","itheima")); // 响应Cookie
        return Result.success();
    }

    // 获取Cookie
    @GetMapping("/c2")
    public Result cookie2(HttpServletRequest request){
//        当浏览器再次向符合 Cookie 作用域的地址发送请求时，会自动将 Cookie 放入请求头
//        随后，服务器通过：
        Cookie[] cookies = request.getCookies();
//        获取客户端携带的 Cookie 数组，再遍历找到指定名称的 Cookie
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("login_username")){
                    System.out.println("login_username: " + cookie.getValue());
                    return Result.success();
                }
            }
        }

        return Result.error("登陆失败");
    }
//    HttpServletResponse 用于操作服务器发给客户端的响应
//    而 HttpServletRequest 用于读取客户端发送给服务器的请求

    //    ① GET /s1 → ② 创建 Session → ③ 存储 loginUser = tom → ④ 返回 JSESSIONID
    @GetMapping("/s1")
    public Result session1(HttpSession session){
        log.info("HttpSession-s1:{}",session.hashCode());
        // 打印真正的 Session ID
        log.info("Session ID：{}", session.getId());

        // 判断是不是新创建的 Session
        log.info("是否为新 Session：{}", session.isNew());

        // 往session中存储数据
        session.setAttribute("loginUser","tom");
        return Result.success();
    }

//⑤ GET /s2，携带 JSESSIONID → ⑥ 匹配已有 Session → ⑦ 读取 loginUser = tom
    @GetMapping("/s2")
    public Result session2(HttpServletRequest request){
        HttpSession session = request.getSession();
        log.info("HttpSession-s2: {}",session.hashCode());

        // 打印真正的 Session ID
        log.info("Session ID：{}", session.getId());

        Object loginUser = session.getAttribute("loginUser");
        log.info("loginUser: {}",loginUser);
        return Result.success(loginUser);
    }

}
