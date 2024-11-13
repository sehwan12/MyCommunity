package com.example.myCommunity.Exception;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String msg){
        super(msg);
    }
}
