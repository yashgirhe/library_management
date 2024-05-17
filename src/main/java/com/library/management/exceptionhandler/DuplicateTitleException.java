package com.library.management.exceptionhandler;

public class DuplicateTitleException extends RuntimeException{
    public DuplicateTitleException(String message){
        super(message);
    }
}
