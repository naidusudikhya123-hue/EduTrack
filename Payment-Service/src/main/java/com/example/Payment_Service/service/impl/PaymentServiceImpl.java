package com.example.Payment_Service.service.impl;

import com.example.Payment_Service.client.CourseClient;
import com.example.Payment_Service.client.EnrollmentClient;
import com.example.Payment_Service.client.UserClient;
import com.example.Payment_Service.dto.CourseDetailsDTO;
import com.example.Payment_Service.dto.EnrollmentCreateFeignDTO;
import com.example.Payment_Service.dto.PaymentCreateRequestDTO;
import com.example.Payment_Service.dto.PaymentVerificationDTO;
import com.example.Payment_Service.entity.Payment;
import com.example.Payment_Service.enums.PaymentStatus;
import com.example.Payment_Service.exception.ExternalServiceException;
import com.example.Payment_Service.exception.InvalidPaymentAmountException;
import com.example.Payment_Service.exception.InvalidVerificationCodeException;
import com.example.Payment_Service.exception.PaymentNotFoundException;
import com.example.Payment_Service.exception.PaymentNotCompletedException;
import com.example.Payment_Service.exception.UserNotFoundException;
import com.example.Payment_Service.mapper.PaymentMapper;
import com.example.Payment_Service.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import com.example.Payment_Service.service.PaymentService;
import feign.FeignException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CourseClient courseClient;
    private final UserClient userClient;
    private final EnrollmentClient enrollmentClient;
    private final EmailService emailService;
    private final KafkaProducerService kafkaProducerService;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            CourseClient courseClient,
            UserClient userClient,
            EnrollmentClient enrollmentClient,
            EmailService emailService,
            KafkaProducerService kafkaProducerService
    ) {
        this.paymentRepository = paymentRepository;
        this.courseClient = courseClient;
        this.userClient = userClient;
        this.enrollmentClient = enrollmentClient;
        this.emailService = emailService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public Payment createPayment(PaymentCreateRequestDTO dto)
            throws InvalidPaymentAmountException, UserNotFoundException {

        if (dto == null) {
            throw new InvalidPaymentAmountException("PAYMENT REQUEST CANNOT BE NULL");
        }

        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new InvalidPaymentAmountException("PAYMENT AMOUNT MUST BE GREATER THAN ZERO");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new InvalidPaymentAmountException("Email cannot be null or empty");
        }

        ensureUserExists(dto.getUserId());

        CourseDetailsDTO course = courseClient.getCourseById(dto.getCourseId());
        if (course.isFree()) {
            throw new InvalidPaymentAmountException("No payment required for this course");
        }

        BigDecimal sentAmount = BigDecimal.valueOf(dto.getAmount());
        BigDecimal coursePrice = BigDecimal.valueOf(course.getPrice());
        if (sentAmount.compareTo(coursePrice) != 0) {
            throw new InvalidPaymentAmountException("Invalid payment amount");
        }

        String paymentId = generatePaymentId();
        Payment payment = PaymentMapper.toEntity(dto, paymentId);

        String otp = generateVerificationCode();
        payment.setVerificationCode(otp);
        payment.setCodeExpiryTime(LocalDateTime.now().plusMinutes(5));
        payment.setVerified(false);
        payment.setStatus(PaymentStatus.PENDING);

        Payment saved = paymentRepository.save(payment);

        // Direct email delivery (no Kafka dependency)
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
    public void enrollUser(String userId, String courseId) {
        CourseDetailsDTO course = courseClient.getCourseById(courseId);
        if (course.isFree()) {
            return;
        }

        boolean hasSuccessfulPayment = paymentRepository.existsByUserIdAndCourseIdAndStatus(
                userId,
                courseId,
                PaymentStatus.SUCCESS
        );

        if (!hasSuccessfulPayment) {
            throw new PaymentNotCompletedException("Payment not completed for this course");
        }
    }

    @Override
    public Payment verifyPayment(PaymentVerificationDTO dto) throws PaymentNotFoundException {
        Payment payment = paymentRepository.findById(dto.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if (payment.getCodeExpiryTime().isBefore(LocalDateTime.now())) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new InvalidVerificationCodeException("OTP expired");
        }

        if (!payment.getVerificationCode().equals(dto.getCode())) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new InvalidVerificationCodeException("Invalid OTP");
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setVerified(true);
        Payment saved = paymentRepository.save(payment);
        syncEnrollmentAfterSuccessfulPayment(saved);
        return saved;
    }

    private void ensureUserExists(String userId) throws UserNotFoundException {
        try {
            userClient.getUserById(userId);
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new UserNotFoundException("User not found: " + userId);
            }
            throw new ExternalServiceException("User service error", e);
        }
    }

    /**
     * Creates enrollment in enrollments service after OTP verification (idempotent if already enrolled).
     */
    private void syncEnrollmentAfterSuccessfulPayment(Payment payment) {
        EnrollmentCreateFeignDTO body = new EnrollmentCreateFeignDTO();
        body.setUserId(payment.getUserId());
        body.setCourseId(payment.getCourseId());
        try {
            enrollmentClient.createEnrollment(body);
        } catch (FeignException e) {
            if (e.status() == 400) {
                return;
            }
            throw new ExternalServiceException("Enrollment could not be created after successful payment verification", e);
        }
    }

    private String generatePaymentId() {
        return "p" + (paymentRepository.count() + 1);
    }

    private String generateVerificationCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

}