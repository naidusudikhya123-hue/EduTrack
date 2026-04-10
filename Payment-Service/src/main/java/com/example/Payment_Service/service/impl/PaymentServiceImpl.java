package com.example.Payment_Service.service.impl;

import com.example.Payment_Service.dto.PaymentCreateRequestDTO;
import com.example.Payment_Service.entity.Payment;
import com.example.Payment_Service.enums.PaymentStatus;
import com.example.Payment_Service.exception.InvalidPaymentAmountException;
import com.example.Payment_Service.exception.InvalidVerificationCodeException;
import com.example.Payment_Service.exception.PaymentNotFoundException;
import com.example.Payment_Service.mapper.PaymentMapper;
import com.example.Payment_Service.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import com.example.Payment_Service.service.PaymentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,EmailService emailService) {
        this.paymentRepository = paymentRepository;
        this.emailService=emailService;
    }

    @Override
    public Payment createPayment(PaymentCreateRequestDTO dto)
            throws InvalidPaymentAmountException {

        if (dto == null) {
            throw new InvalidPaymentAmountException("PAYMENT REQUEST CANNOT BE NULL");
        }

        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new InvalidPaymentAmountException("PAYMENT AMOUNT MUST BE GREATER THAN ZERO");
        }

        String paymentId = generatePaymentId();
        Payment payment = PaymentMapper.toEntity(dto, paymentId);

        String otp = generateVerificationCode();
        payment.setVerificationCode(otp);
        payment.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        payment.setPaymentStatus(PaymentStatus.PENDING);

        Payment saved = paymentRepository.save(payment);

        emailService.sendOtp(dto.getEmail(), otp, paymentId);
        return saved;
    }

    @Override
    public List<Payment> getPaymentsByUser(String userId)
            throws InvalidPaymentAmountException, PaymentNotFoundException {

        if (userId == null || userId.isBlank()) {
            throw new InvalidPaymentAmountException("UserId cannot be null or empty");
        }

        List<Payment> payments = paymentRepository.findByUserId(userId);

        if (payments.isEmpty()) {
            throw new PaymentNotFoundException("NO PAYMENTS FOUND FOR USER: " + userId);
        }

        return payments;
    }

    @Override
    public Payment verifyPayment(String paymentId, String code)
            throws PaymentNotFoundException {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not founf"));

        if (payment.getCodeExpiryTime().isBefore(LocalDateTime.now())) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new InvalidVerificationCodeException("OTP expired");
        }

        if (!payment.getVerificationCode().equals(code)) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new InvalidVerificationCodeException("Invalid OTP");
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setVerified(true);

        return paymentRepository.save(payment);
    }

    private String generatePaymentId() {
        return "p" + (paymentRepository.count() + 1);
    }

    private String generateVerificationCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}