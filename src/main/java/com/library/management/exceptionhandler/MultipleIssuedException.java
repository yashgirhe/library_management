package com.library.management.exceptionhandler;

public class MultipleIssuedException extends RuntimeException{
    public MultipleIssuedException(String message){
        super(message);
    }
}
