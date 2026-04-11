package com.example.Payment_Service.service;

import com.example.Payment_Service.dto.PaymentCreateRequestDTO;
import com.example.Payment_Service.dto.PaymentVerificationDTO;
import com.example.Payment_Service.entity.Payment;
import com.example.Payment_Service.exception.InvalidPaymentAmountException;
import com.example.Payment_Service.exception.PaymentNotFoundException;
import com.example.Payment_Service.exception.UserNotFoundException;

import java.util.List;

public interface PaymentService {
    Payment createPayment(PaymentCreateRequestDTO dto) throws InvalidPaymentAmountException, UserNotFoundException;
    List<Payment> getPaymentsByUser(String userId)
            throws InvalidPaymentAmountException, PaymentNotFoundException;

    void enrollUser(String userId, String courseId);

    Payment verifyPayment(PaymentVerificationDTO dto) throws PaymentNotFoundException;
}