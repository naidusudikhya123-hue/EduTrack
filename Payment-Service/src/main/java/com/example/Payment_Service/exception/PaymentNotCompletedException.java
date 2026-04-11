package com.example.Payment_Service.exception;

public class PaymentNotCompletedException extends RuntimeException {
    public PaymentNotCompletedException(String message) {
        super(message);
    }
}
