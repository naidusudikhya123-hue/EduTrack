package com.example.Payment_Service.mapper;

import com.example.Payment_Service.dto.PaymentCreateRequestDTO;
import com.example.Payment_Service.dto.PaymentResponseDTO;
import com.example.Payment_Service.entity.Payment;

public class PaymentMapper {
    public static Payment toEntity(PaymentCreateRequestDTO dto,String paymentId)
    {
        Payment payment=new Payment();
        payment.setPaymentId(paymentId);
        payment.setUserId(dto.getUserId());
        payment.setCourseId(dto.getCourseId());
        payment.setEnrollmentId(dto.getEnrollmentId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        return payment;

    }

    public static PaymentResponseDTO toResponseDTO(Payment payment)
    {
        PaymentResponseDTO dto=new PaymentResponseDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setUserId(payment.getUserId());
        dto.setCourseId(payment.getCourseId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getPaymentStatus());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }
}
