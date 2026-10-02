package com.itheima.exception;

import com.itheima.pojo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理异常
    @ExceptionHandler // 指定可以捕获哪种类型的异常
    public Result ex(Exception e){
        e.printStackTrace();
        return Result.error(e.getMessage());
    }
}
