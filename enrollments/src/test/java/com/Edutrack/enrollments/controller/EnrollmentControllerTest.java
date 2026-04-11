package com.Edutrack.enrollments.controller;

import com.Edutrack.enrollments.dto.EnrollmentCreateRequestDTO;
import com.Edutrack.enrollments.dto.EnrollmentResponseDTO;
import com.Edutrack.enrollments.enums.EnrollmentStatus;
import com.Edutrack.enrollments.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentControllerTest {

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentController enrollmentController;

    @Test
    void enrollReturnsCreated() throws Exception {
        EnrollmentResponseDTO dto = new EnrollmentResponseDTO();
        dto.setEnrollmentId("e1");
        dto.setUserId("u1");
        dto.setCourseId("c1");
        dto.setStatus(EnrollmentStatus.ACTIVE);

        when(enrollmentService.enrollAndReturnResponse(any(EnrollmentCreateRequestDTO.class))).thenReturn(dto);

        EnrollmentCreateRequestDTO body = new EnrollmentCreateRequestDTO("u1", "c1");
        ResponseEntity<EnrollmentResponseDTO> response = enrollmentController.enroll(body);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("e1", response.getBody().getEnrollmentId());
        verify(enrollmentService).enrollAndReturnResponse(any(EnrollmentCreateRequestDTO.class));
    }
}
