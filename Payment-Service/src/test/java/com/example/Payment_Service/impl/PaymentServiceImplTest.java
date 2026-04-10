package com.example.Payment_Service.impl;

import com.example.Payment_Service.dto.PaymentCreateRequestDTO;
import com.example.Payment_Service.entity.Payment;
import com.example.Payment_Service.enums.PaymentMethod;
import com.example.Payment_Service.enums.PaymentStatus;
import com.example.Payment_Service.exception.InvalidPaymentAmountException;
import com.example.Payment_Service.exception.InvalidVerificationCodeException;
import com.example.Payment_Service.repository.PaymentRepository;
import com.example.Payment_Service.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void createPaymentCreatesPendingPaymentWithOtp() throws Exception {
        PaymentCreateRequestDTO dto = new PaymentCreateRequestDTO();
        dto.setUserId("u1");
        dto.setCourseId("c1");
        dto.setEnrollmentId("e1");
        dto.setAmount(999.0);
        dto.setPaymentMethod(PaymentMethod.UPI);

        when(paymentRepository.count()).thenReturn(0L);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment saved = paymentService.createPayment(dto);

        assertEquals("p1", saved.getPaymentId());
        assertEquals(PaymentStatus.PENDING, saved.getPaymentStatus());
        assertNotNull(saved.getVerificationCode());
        assertEquals(6, saved.getVerificationCode().length());
    }

    @Test
    void getPaymentsByUserThrowsWhenUserIdIsBlank() {
        assertThrows(InvalidPaymentAmountException.class, () -> paymentService.getPaymentsByUser(" "));
    }

    @Test
    void verifyPaymentMarksFailedWhenCodeExpired() {
        Payment payment = new Payment();
        payment.setPaymentId("p1");
        payment.setVerificationCode("123456");
        payment.setCodeExpiryTime(LocalDateTime.now().minusMinutes(1));

        when(paymentRepository.findById("p1")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(InvalidVerificationCodeException.class, () -> paymentService.verifyPayment("p1", "123456"));
        assertEquals(PaymentStatus.FAILED, payment.getPaymentStatus());
    }

    @Test
    void verifyPaymentMarksSuccessWhenOtpMatches() throws Exception {
        Payment payment = new Payment();
        payment.setPaymentId("p1");
        payment.setVerificationCode("123456");
        payment.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        payment.setVerified(false);

        when(paymentRepository.findById("p1")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment verified = paymentService.verifyPayment("p1", "123456");

        assertEquals(PaymentStatus.SUCCESS, verified.getPaymentStatus());
        assertTrue(verified.getVerified());
    }
}
