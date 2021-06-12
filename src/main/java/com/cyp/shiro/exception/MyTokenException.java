package com.cyp.shiro.exception;


public class MyTokenException extends RuntimeException{

    public MyTokenException() {
        super("Token已过期");
    }
}
