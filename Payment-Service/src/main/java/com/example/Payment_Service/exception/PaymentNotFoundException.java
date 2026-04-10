package com.example.Payment_Service.exception;

public class PaymentNotFoundException extends Exception {
    public PaymentNotFoundException(String msg) {
        super(msg);
    }
}