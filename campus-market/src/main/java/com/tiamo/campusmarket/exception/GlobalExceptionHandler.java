package com.tiamo.campusmarket.exception;

import com.tiamo.campusmarket.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handle(Exception e) {
        e.printStackTrace();
        return Result.error("服务器异常：" + e.getMessage());
    }
}