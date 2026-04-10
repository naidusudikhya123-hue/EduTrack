package com.example.Payment_Service.exception;

public class InvalidPaymentAmountException extends Exception {
    public InvalidPaymentAmountException(String msg) {
        super(msg);
    }
}