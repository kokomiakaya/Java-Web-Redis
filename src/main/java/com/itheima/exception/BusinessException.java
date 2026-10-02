package com.itheima.exception;

public class BusinessException extends RuntimeException {
    // 自定义异常，处理删除班级时，班级有学员的情况
    public BusinessException(String message) {
        super(message);
    }
}
