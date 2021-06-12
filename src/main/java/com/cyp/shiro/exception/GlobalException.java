package com.cyp.shiro.exception;


import com.cyp.shiro.base.BaseResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = NullPointerException.class)//指定拦截的异常
    public BaseResponse errorHandler(NullPointerException e)  {
        //打印异常信息
        BaseResponse baseResponse = new BaseResponse(12345,"空指针异常",e.toString());
        return baseResponse;
    }
    @ExceptionHandler(MyTokenException.class)//指定拦截的异常
    public BaseResponse errorHandler2(MyTokenException e)  {
        //打印异常信息
        BaseResponse baseResponse = new BaseResponse(11111,"Token过期",e.toString());
        System.out.println("1111111111111111111111");
        return baseResponse;
    }
}
