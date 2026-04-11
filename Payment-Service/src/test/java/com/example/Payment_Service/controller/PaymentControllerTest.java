package com.example.Payment_Service.controller;

import com.example.Payment_Service.dto.PaymentVerificationDTO;
import com.example.Payment_Service.entity.Payment;
import com.example.Payment_Service.enums.PaymentStatus;
import com.example.Payment_Service.exception.PaymentNotFoundException;
import com.example.Payment_Service.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    void getPaymentsByUserReturnsOk() throws Exception {
        Payment p = new Payment();
        p.setPaymentId("p1");
        p.setUserId("u1");
        p.setCourseId("c1");
        p.setAmount(99.0);
        p.setStatus(PaymentStatus.SUCCESS);

        when(paymentService.getPaymentsByUser("u1")).thenReturn(List.of(p));

        ResponseEntity<?> response = paymentController.getPaymentsByUser("u1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentService).getPaymentsByUser(eq("u1"));
    }

    @Test
    void getPaymentsByUserPropagatesNotFound() throws Exception {
        when(paymentService.getPaymentsByUser("u404")).thenThrow(new PaymentNotFoundException("none"));

        assertThrows(PaymentNotFoundException.class, () -> paymentController.getPaymentsByUser("u404"));
    }

    @Test
    void verifyPaymentReturnsOk() throws Exception {
        Payment p = new Payment();
        p.setPaymentId("p1");
        p.setStatus(PaymentStatus.SUCCESS);
        when(paymentService.verifyPayment(any(PaymentVerificationDTO.class))).thenReturn(p);

        PaymentVerificationDTO dto = new PaymentVerificationDTO();
        dto.setPaymentId("p1");
        dto.setCode("123456");

        ResponseEntity<?> response = paymentController.verify(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
