package com.example.Payment_Service.controller;
import com.example.Payment_Service.dto.PaymentCreateRequestDTO;
import com.example.Payment_Service.dto.PaymentResponseDTO;
import com.example.Payment_Service.dto.PaymentVerificationDTO;
import com.example.Payment_Service.entity.Payment;
import com.example.Payment_Service.exception.InvalidPaymentAmountException;
import com.example.Payment_Service.exception.PaymentNotFoundException;
import com.example.Payment_Service.mapper.PaymentMapper;
import com.example.Payment_Service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/addPayment")
    public ResponseEntity<PaymentResponseDTO> pay(@RequestBody @Valid PaymentCreateRequestDTO dto)
            throws InvalidPaymentAmountException {

        Payment payment = paymentService.createPayment(dto);
        PaymentResponseDTO paymentResponse = PaymentMapper.toResponseDTO(payment);
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByUser(@PathVariable String userId)
            throws PaymentNotFoundException, InvalidPaymentAmountException {

        return ResponseEntity.ok(
                paymentService.getPaymentsByUser(userId)
                        .stream()
                        .map(p -> PaymentMapper.toResponseDTO(p))
                        .toList()
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentResponseDTO> verify(@RequestBody PaymentVerificationDTO dto)
            throws PaymentNotFoundException {

        Payment payment = paymentService.verifyPayment(
                dto.getPaymentId(),
                dto.getCode()
        );

        return ResponseEntity.ok(
                PaymentMapper.toResponseDTO(payment)
        );
    }
}