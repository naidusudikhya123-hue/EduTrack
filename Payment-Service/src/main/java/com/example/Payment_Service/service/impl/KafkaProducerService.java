package com.example.Payment_Service.service.impl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String,String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOtp(String email,String otp,String paymentId)
    {
        String message=email+","+otp+","+paymentId;
        kafkaTemplate.send("otp-topic",message);
    }
}
