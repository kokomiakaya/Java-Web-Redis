package com.itheima.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component // 将当前类交给 Spring IOC 容器管理
@Aspect //当前类为切面类
@Slf4j
public class RecordTimeAspect {

//    切面（Aspect）：RecordTimeAspect 这个类。
//    通知（Advice）：recordTime() 方法中定义的增强逻辑。
//    切入点（Pointcut）：execution(...) 指定的方法匹配规则。
//    连接点（Join Point）：被增强的方法执行位置。
//    ② @Around()：环绕通知（重点）
//    这句话实际上决定了：哪些方法需要进行耗时统计。

//    @Around("execution(* com.itheima.service.impl.DeptServiceImpl.*(..))")
//    public Object recordTime(ProceedingJoinPoint pjp) throws Throwable {
//        //记录方法执行开始时间
//        long begin = System.currentTimeMillis();
//
////        ProceedingJoinPoint 是环绕通知中非常重要的一个接口。
////        你可以把它理解成：当前被拦截方法的执行上下文。
//
//        //执行原始方法
//        Object result = pjp.proceed();
//

    /// /        可以获取被增强的方法名。
//        String name = pjp.getSignature().getName();
//        log.info("执行的方法: {}",name);
//
//
//        //记录方法执行结束时间
//        long end = System.currentTimeMillis();
//
//        //计算方法执行耗时
//        log.info("方法执行耗时: {}毫秒",end-begin);
//        return result;
//    }
    @Around("execution(* com.itheima.service.impl..*.*(..))")
    public Object recordTime(ProceedingJoinPoint pjp) throws Throwable {

        // 输出当前拦截到的完整方法信息
        log.info("AOP拦截到的方法：{}",
                pjp.getSignature().toLongString());

        long begin = System.currentTimeMillis();

        try {
            // 执行原始业务方法
            return pjp.proceed();

        } finally {
            long end = System.currentTimeMillis();

            log.info("方法执行耗时：{}毫秒", end - begin);
        }
    }

}