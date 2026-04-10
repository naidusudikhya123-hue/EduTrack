package com.edutrack.userService.exception;

public class EntityNotFoundException extends Exception{
    public EntityNotFoundException(String msg)
    {
        super(msg);
    }
}