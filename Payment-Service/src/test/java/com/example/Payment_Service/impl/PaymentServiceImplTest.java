package com.example.Payment_Service.impl;

import com.example.Payment_Service.client.CourseClient;
import com.example.Payment_Service.client.EnrollmentClient;
import com.example.Payment_Service.client.UserClient;
import com.example.Payment_Service.dto.CourseDetailsDTO;
import com.example.Payment_Service.dto.UserResponseDTO;
import com.example.Payment_Service.dto.PaymentCreateRequestDTO;
import com.example.Payment_Service.dto.PaymentVerificationDTO;
import com.example.Payment_Service.entity.Payment;
import com.example.Payment_Service.enums.PaymentStatus;
import com.example.Payment_Service.exception.InvalidPaymentAmountException;
import com.example.Payment_Service.exception.InvalidVerificationCodeException;
import com.example.Payment_Service.exception.PaymentNotCompletedException;
import com.example.Payment_Service.repository.PaymentRepository;
import com.example.Payment_Service.service.impl.PaymentServiceImpl;
import com.example.Payment_Service.service.impl.EmailService;
import com.example.Payment_Service.service.impl.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CourseClient courseClient;

    @Mock
    private UserClient userClient;

    @Mock
    private EnrollmentClient enrollmentClient;

    @Mock
    private EmailService emailService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void createPaymentValidAmountSuccess() throws Exception {
        PaymentCreateRequestDTO dto = new PaymentCreateRequestDTO();
        dto.setUserId("u1");
        dto.setCourseId("c1");
        dto.setAmount(999.0);
        dto.setEmail("test@example.com");

        CourseDetailsDTO course = new CourseDetailsDTO();
        course.setCourseId("c1");
        course.setPrice(999.0);
        course.setFree(false);

        when(paymentRepository.count()).thenReturn(0L);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(courseClient.getCourseById("c1")).thenReturn(course);
        UserResponseDTO user = new UserResponseDTO();
        user.setUserId("u1");
        when(userClient.getUserById("u1")).thenReturn(user);

        Payment saved = paymentService.createPayment(dto);

        assertEquals("p1", saved.getPaymentId());
        assertEquals(PaymentStatus.PENDING, saved.getStatus());
        assertNotNull(saved.getVerificationCode());
        assertEquals(6, saved.getVerificationCode().length());
        assertNotNull(saved.getCodeExpiryTime());
        verify(emailService, times(1)).sendOtp(eq("test@example.com"), anyString(), eq("p1"));
    }

    @Test
    void createPaymentInvalidAmountThrows() {
        PaymentCreateRequestDTO dto = new PaymentCreateRequestDTO();
        dto.setUserId("u1");
        dto.setCourseId("c1");
        dto.setAmount(100.0);
        dto.setEmail("test@example.com");

        CourseDetailsDTO course = new CourseDetailsDTO();
        course.setCourseId("c1");
        course.setPrice(999.0);
        course.setFree(false);

        when(courseClient.getCourseById("c1")).thenReturn(course);
        UserResponseDTO user = new UserResponseDTO();
        user.setUserId("u1");
        when(userClient.getUserById("u1")).thenReturn(user);

        assertThrows(InvalidPaymentAmountException.class, () -> paymentService.createPayment(dto));
    }

    @Test
    void enrollFreeCourseSuccess() {
        CourseDetailsDTO course = new CourseDetailsDTO();
        course.setCourseId("c1");
        course.setPrice(0.0);
        course.setFree(true);

        when(courseClient.getCourseById("c1")).thenReturn(course);

        assertDoesNotThrow(() -> paymentService.enrollUser("u1", "c1"));
    }

    @Test
    void enrollPaidCourseWithPaymentSuccess() {
        CourseDetailsDTO course = new CourseDetailsDTO();
        course.setCourseId("c1");
        course.setPrice(999.0);
        course.setFree(false);

        when(courseClient.getCourseById("c1")).thenReturn(course);
        when(paymentRepository.existsByUserIdAndCourseIdAndStatus("u1", "c1", PaymentStatus.SUCCESS)).thenReturn(true);

        assertDoesNotThrow(() -> paymentService.enrollUser("u1", "c1"));
    }

    @Test
    void enrollPaidCourseWithoutPaymentThrows() {
        CourseDetailsDTO course = new CourseDetailsDTO();
        course.setCourseId("c1");
        course.setPrice(999.0);
        course.setFree(false);

        when(courseClient.getCourseById("c1")).thenReturn(course);
        when(paymentRepository.existsByUserIdAndCourseIdAndStatus("u1", "c1", PaymentStatus.SUCCESS)).thenReturn(false);

        assertThrows(PaymentNotCompletedException.class, () -> paymentService.enrollUser("u1", "c1"));
    }

    @Test
    void verifyPaymentMarksSuccessWhenOtpMatches() throws Exception {
        Payment payment = new Payment();
        payment.setPaymentId("p1");
        payment.setVerificationCode("123456");
        payment.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        payment.setVerified(false);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUserId("u1");
        payment.setCourseId("c1");

        when(paymentRepository.findById("p1")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(enrollmentClient).createEnrollment(any());

        PaymentVerificationDTO dto = new PaymentVerificationDTO();
        dto.setPaymentId("p1");
        dto.setCode("123456");

        Payment verified = paymentService.verifyPayment(dto);

        assertEquals(PaymentStatus.SUCCESS, verified.getStatus());
        assertTrue(verified.getVerified());
        verify(enrollmentClient, times(1)).createEnrollment(any());
    }

    @Test
    void verifyPaymentThrowsWhenOtpExpired() {
        Payment payment = new Payment();
        payment.setPaymentId("p1");
        payment.setVerificationCode("123456");
        payment.setCodeExpiryTime(LocalDateTime.now().minusMinutes(1));
        payment.setVerified(false);
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById("p1")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentVerificationDTO dto = new PaymentVerificationDTO();
        dto.setPaymentId("p1");
        dto.setCode("123456");

        assertThrows(InvalidVerificationCodeException.class, () -> paymentService.verifyPayment(dto));
        assertEquals(PaymentStatus.FAILED, payment.getStatus());
    }
}
