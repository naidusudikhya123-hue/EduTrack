package com.edutrack.userService.exception;

public class InvalidUserDataException extends Exception{
    public InvalidUserDataException(String message) {
        super(message);
    }
}