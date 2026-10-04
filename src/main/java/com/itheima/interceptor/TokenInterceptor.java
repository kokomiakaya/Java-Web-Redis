package com.itheima.interceptor;

import com.itheima.utils.CurrentHolder;
import com.itheima.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        log.info("TokenInterceptor preHandle：{}",
                request.getRequestURI());

        String jwt = request.getHeader("token");

        // token不存在
        if (!StringUtils.hasLength(jwt)) {
            log.info("JWT为空");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            Claims claims = JwtUtils.parseJwt(jwt);

            Integer empId =
                    Integer.valueOf(claims.get("id").toString());

            CurrentHolder.setCurrentId(empId);

            // 强烈建议你现在加这一句调试
            log.info("存入ThreadLocal的empId：{}", empId);

        } catch (Exception e) {
            log.info("JWT解析失败");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        log.info("令牌合法，放行");

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {

        log.info("清除前ThreadLocal中的empId：{}",
                CurrentHolder.getCurrentId());

        CurrentHolder.remove();

        log.info("ThreadLocal清理完成");
    }
}