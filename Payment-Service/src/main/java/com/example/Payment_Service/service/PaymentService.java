package com.example.Payment_Service.service;

import com.example.Payment_Service.dto.PaymentCreateRequestDTO;
import com.example.Payment_Service.entity.Payment;
import com.example.Payment_Service.exception.InvalidPaymentAmountException;
import com.example.Payment_Service.exception.PaymentNotFoundException;

import java.util.List;

public interface PaymentService {
    Payment createPayment(PaymentCreateRequestDTO dto) throws InvalidPaymentAmountException;
    Payment verifyPayment(String paymentId, String code) throws PaymentNotFoundException;
    List<Payment> getPaymentsByUser(String userId)
            throws InvalidPaymentAmountException, PaymentNotFoundException;
}