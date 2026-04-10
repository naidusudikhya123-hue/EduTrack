package com.course.exception;

public class VideoAlreadyExistsException extends Exception{
    public VideoAlreadyExistsException(String message) {
        super(message);
    }
}