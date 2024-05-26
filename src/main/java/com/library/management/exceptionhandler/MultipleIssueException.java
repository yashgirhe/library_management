package com.library.management.exceptionhandler;

public class MultipleIssueException extends RuntimeException{
    public MultipleIssueException(String message){
        super(message);
    }
}
