package com.example.Payment_Service.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String toEmail, String otp, String paymentId) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Payment OTP Verification");

        message.setText(
                "Your OTP for payment ID " + paymentId + " is: " + otp +
                        "\n\nThis OTP will expire in 5 minutes."
        );

        mailSender.send(message);
    }
}