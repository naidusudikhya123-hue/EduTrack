package com.Edutrack.enrollments.controller;

import com.Edutrack.enrollments.dto.EnrollmentCreateRequestDTO;
import com.Edutrack.enrollments.dto.EnrollmentResponseDTO;
import com.Edutrack.enrollments.dto.UserEnrollmentDTO;
import com.Edutrack.enrollments.entity.Enrollment;
import com.Edutrack.enrollments.exception.EnrollmentAlreadyExistsException;
import com.Edutrack.enrollments.exception.EnrollmentNotFoundException;
import com.Edutrack.enrollments.mapper.EnrollmentMapper;
import com.Edutrack.enrollments.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> enroll(
            @Valid @RequestBody EnrollmentCreateRequestDTO dto)
            throws EnrollmentAlreadyExistsException {

        EnrollmentResponseDTO response =
                enrollmentService.enrollAndReturnResponse(dto);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDTO> getById(
            @PathVariable String id)
            throws EnrollmentNotFoundException {

        Enrollment enrollment = enrollmentService.getEnrollment(id);
        return ResponseEntity.ok(
                EnrollmentMapper.toResponseDto(enrollment));
    }

    @GetMapping("/users/{userId}/details")
    public ResponseEntity<List<UserEnrollmentDTO>> getEnrollmentsOfUser(
            @PathVariable String userId) {

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentsOfUser(userId)
        );
    }


    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponseDTO>> getByCourse(
            @PathVariable String courseId) {

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentsByCourse(courseId)
        );
    }


    @PutMapping("/{id}/cancel")
    public ResponseEntity<EnrollmentResponseDTO> cancel(
            @PathVariable String id)
            throws EnrollmentNotFoundException {

        Enrollment enrollment = enrollmentService.cancelEnrollment(id);
        return ResponseEntity.ok(
                EnrollmentMapper.toResponseDto(enrollment));
    }
}