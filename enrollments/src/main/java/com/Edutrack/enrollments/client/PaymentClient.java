package com.Edutrack.enrollments.client;

import com.Edutrack.enrollments.dto.PaymentResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "Payment-Service")
public interface PaymentClient {

    @GetMapping("/payments/user/{userId}")
    List<PaymentResponseDTO> getPaymentsByUser(@PathVariable String userId);
}