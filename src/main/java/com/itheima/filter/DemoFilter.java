package com.itheima.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
//@WebFilter(urlPatterns = "/*") //配置过滤器要拦截的请求路径（ /* 表示拦截浏览器的所有请求 ）
public class DemoFilter implements Filter {
    // 初始化方法，web服务器启动，创建Filter实例时调用，只调用一次
    public void init(FilterConfig filterConfig) {
        log.info("init ... ");
    }

    // 拦截到请求时，调用该方法，可以调用多次
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        log.info("拦截到了请求 ... ");
        // 放行，让请求继续向后执行
        log.info("放行请求");
        chain.doFilter(servletRequest, servletResponse);
    }

    // 销毁方法,web服务器关闭时调用，只调用一次
    public void destroy() {
        System.out.println("destroy ... ");
    }

}
