package com.Edutrack.enrollments.repository;

import com.Edutrack.enrollments.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface EnrollmentRepository extends JpaRepository<Enrollment,String> {

    boolean existsByUserIdAndCourseId(String userId,String courseId);

    Optional<Enrollment> findByUserIdAndCourseId(String userId, String courseId);

    List<Enrollment> findByUserId(String userId);

    List<Enrollment> findByCourseId(String courseId);
}