package com.example.Payment_Service.repository;

import com.example.Payment_Service.entity.Payment;
import com.example.Payment_Service.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByUserId(String userId);
    List<Payment> findByCourseId(String courseId);

    boolean existsByUserIdAndCourseIdAndStatus(String userId, String courseId, PaymentStatus status);
}