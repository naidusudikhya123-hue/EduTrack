package com.edutrack.userService.exception;

public class UserNotFoundException extends Exception{
    public UserNotFoundException(String msg)
    {
        super(msg);
    }
}
