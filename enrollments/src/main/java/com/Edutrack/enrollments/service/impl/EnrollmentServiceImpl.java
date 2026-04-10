package com.Edutrack.enrollments.service.impl;

import com.Edutrack.enrollments.enums.PaymentStatus;
import com.Edutrack.enrollments.client.CourseClient;
import com.Edutrack.enrollments.client.PaymentClient;
import com.Edutrack.enrollments.client.UserClient;
import com.Edutrack.enrollments.dto.*;
import com.Edutrack.enrollments.entity.Enrollment;
import com.Edutrack.enrollments.enums.EnrollmentStatus;
import com.Edutrack.enrollments.exception.*;
import com.Edutrack.enrollments.mapper.EnrollmentMapper;
import com.Edutrack.enrollments.repository.EnrollmentRepository;
import com.Edutrack.enrollments.service.EnrollmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserClient userClient;
    private final CourseClient courseClient;
    private final PaymentClient paymentClient;
    private PaymentStatus status;

    public EnrollmentServiceImpl(UserClient userClient, EnrollmentRepository enrollmentRepository, CourseClient courseClient, PaymentClient paymentClient) {
        this.userClient = userClient;
        this.enrollmentRepository = enrollmentRepository;
        this.courseClient = courseClient;
        this.paymentClient = paymentClient;
    }

    @Override
    public Enrollment enrollUser(EnrollmentCreateRequestDTO dto)
            throws EnrollmentAlreadyExistsException {

        CourseDTO course = courseClient.getCourseById(dto.getCourseId());

        if (course == null) {
            throw new RuntimeException("Course not found");
        }

        if (enrollmentRepository.existsByUserIdAndCourseId(
                dto.getUserId(), dto.getCourseId())) {

            throw new EnrollmentAlreadyExistsException(
                    "User already enrolled in this course"
            );
        }

        List<PaymentResponseDTO> payments =
                paymentClient.getPaymentsByUser(dto.getUserId());
        System.out.println("payments"+payments);


        boolean isPaid = payments.stream()
                .anyMatch(p ->
                        p.getCourseId().equals(dto.getCourseId()) &&
                                p.getStatus() != null &&
                                p.getStatus().toString().equalsIgnoreCase("SUCCESS")
                );

        if (!isPaid) {
            throw new RuntimeException("Payment not completed for this course");
        }
        System.out.println("is paid? "+isPaid);

        String enrollmentId = "e" + (enrollmentRepository.count() + 1);

        Enrollment enrollment = EnrollmentMapper.toEntity(dto, enrollmentId);

        return enrollmentRepository.save(enrollment);
    }

    @Override
    public Enrollment getEnrollment(String enrollmentId) throws EnrollmentNotFoundException {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment not found"));
    }

    @Override
    public Enrollment cancelEnrollment(String enrollmentId) throws EnrollmentNotFoundException {
        Enrollment enrollment = getEnrollment(enrollmentId);
        enrollment.setStatus(EnrollmentStatus.CANCELED);
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public List<UserEnrollmentDTO> getEnrollmentsOfUser(String userId) {

        UserResponseDTO user = userClient.getUserById(userId);

        return enrollmentRepository.findByUserId(userId)
                .stream()
                .map(enrollment -> {
                    UserEnrollmentDTO dto = new UserEnrollmentDTO();
                    dto.setEnrollmentId(enrollment.getEnrollmentId());
                    dto.setUserId(user.getUserId());
                    dto.setUserName(user.getUserName());
                    dto.setEmailId(user.getEmailId());
                    dto.setCourseId(enrollment.getCourseId());
                    dto.setStatus(enrollment.getStatus().name());
                    dto.setEnrolledAt(enrollment.getCreatedAt());
                    return dto;
                }).toList();
    }


    @Override
    public List<EnrollmentResponseDTO> getEnrollmentsByCourse(String courseId) {

        CourseDTO course = courseClient.getCourseById(courseId);

        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(enrollment -> {

                    // ✅ Use mapper to get all fields
                    EnrollmentResponseDTO dto =
                            EnrollmentMapper.toResponseDto(enrollment);

                    // ✅ Add course details
                    dto.setCourseTitle(course.getTitle());
                    dto.setCourseDescription(course.getDescription());

                    return dto;
                }).toList();
    }

    @Override
    public EnrollmentResponseDTO enrollAndReturnResponse(EnrollmentCreateRequestDTO dto)
            throws EnrollmentAlreadyExistsException {

        // ✅ Validate course
        CourseDTO course = courseClient.getCourseById(dto.getCourseId());

        if (course == null) {
            throw new RuntimeException("Course not found");
        }

        if (enrollmentRepository.existsByUserIdAndCourseId(dto.getUserId(), dto.getCourseId())) {
            throw new EnrollmentAlreadyExistsException("User already enrolled in this course");
        }

        String enrollmentId = "e" + (enrollmentRepository.count() + 1);

        Enrollment enrollment = EnrollmentMapper.toEntity(dto, enrollmentId);
        Enrollment saved = enrollmentRepository.save(enrollment);

        // ✅ Convert to response
        EnrollmentResponseDTO response = EnrollmentMapper.toResponseDto(saved);

        // ✅ ADD COURSE DETAILS (THIS WAS MISSING)
        response.setCourseTitle(course.getTitle());
        response.setCourseDescription(course.getDescription());

        return response;
    }

}