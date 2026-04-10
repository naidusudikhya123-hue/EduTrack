package com.Edutrack.enrollments.service;

import com.Edutrack.enrollments.dto.*;
import com.Edutrack.enrollments.entity.Enrollment;
import com.Edutrack.enrollments.exception.*;

import java.util.List;

public interface EnrollmentService {

    Enrollment enrollUser(EnrollmentCreateRequestDTO dto) throws EnrollmentAlreadyExistsException;

    Enrollment getEnrollment(String enrollmentId) throws EnrollmentNotFoundException;


    Enrollment cancelEnrollment(String enrollmentId) throws EnrollmentNotFoundException;

    List<UserEnrollmentDTO> getEnrollmentsOfUser(String userId);

    List<EnrollmentResponseDTO> getEnrollmentsByCourse(String courseId);

    EnrollmentResponseDTO enrollAndReturnResponse(EnrollmentCreateRequestDTO dto)
            throws EnrollmentAlreadyExistsException;
}