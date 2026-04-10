package com.Edutrack.enrollments.mapper;

import com.Edutrack.enrollments.dto.*;
import com.Edutrack.enrollments.entity.Enrollment;

public class EnrollmentMapper {

    public static Enrollment toEntity(EnrollmentCreateRequestDTO dto, String enrollmentId) {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentId);
        enrollment.setUserId(dto.getUserId());
        enrollment.setCourseId(dto.getCourseId());
        return enrollment;
    }

    public static EnrollmentResponseDTO toResponseDto(Enrollment enrollment) {
        EnrollmentResponseDTO dto = new EnrollmentResponseDTO();
        dto.setEnrollmentId(enrollment.getEnrollmentId());
        dto.setUserId(enrollment.getUserId());
        dto.setStatus(enrollment.getStatus());
        dto.setCourseId(enrollment.getCourseId());
        dto.setCreatedAt(enrollment.getCreatedAt());
        dto.setUpdatedAt(enrollment.getUpdatedAt());
        return dto;
    }
}