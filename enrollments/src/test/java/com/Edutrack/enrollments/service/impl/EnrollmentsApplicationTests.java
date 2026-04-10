package com.Edutrack.enrollments.service.impl;
import com.Edutrack.enrollments.client.UserClient;
import com.Edutrack.enrollments.dto.EnrollmentCreateRequestDTO;
import com.Edutrack.enrollments.dto.UserEnrollmentDTO;
import com.Edutrack.enrollments.dto.UserResponseDTO;
import com.Edutrack.enrollments.entity.Enrollment;
import com.Edutrack.enrollments.enums.EnrollmentStatus;
import com.Edutrack.enrollments.exception.EnrollmentAlreadyExistsException;
import com.Edutrack.enrollments.exception.EnrollmentNotFoundException;
import com.Edutrack.enrollments.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private UserClient userClient;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @Test
    void enrollUserThrowsWhenDuplicateEnrollmentExists() {
        EnrollmentCreateRequestDTO dto = new EnrollmentCreateRequestDTO("u1", "c1");
        when(enrollmentRepository.existsByUserIdAndCourseId("u1", "c1")).thenReturn(true);

        assertThrows(EnrollmentAlreadyExistsException.class, () -> enrollmentService.enrollUser(dto));
    }

    @Test
    void cancelEnrollmentMarksStatusCanceled() throws Exception {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId("e1");
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        when(enrollmentRepository.findById("e1")).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Enrollment canceled = enrollmentService.cancelEnrollment("e1");

        assertEquals(EnrollmentStatus.CANCELED, canceled.getStatus());
    }

    @Test
    void getEnrollmentsOfUserMapsUserDataFromFeignClient() {
        UserResponseDTO user = new UserResponseDTO();
        user.setUserId("u1");
        user.setUserName("Vijay");
        user.setEmailId("vijay@example.com");

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId("e1");
        enrollment.setUserId("u1");
        enrollment.setCourseId("c1");
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setCreatedAt(LocalDateTime.of(2026, 4, 6, 10, 0));

        when(userClient.getUserById("u1")).thenReturn(user);
        when(enrollmentRepository.findByUserId("u1")).thenReturn(List.of(enrollment));

        List<UserEnrollmentDTO> result = enrollmentService.getEnrollmentsOfUser("u1");

        assertEquals(1, result.size());
        assertEquals("Vijay", result.get(0).getUserName());
        assertEquals("c1", result.get(0).getCourseId());
        assertEquals("ACTIVE", result.get(0).getStatus());
    }

    @Test
    void getEnrollmentThrowsWhenMissing() {
        when(enrollmentRepository.findById("e404")).thenReturn(Optional.empty());

        assertThrows(EnrollmentNotFoundException.class, () -> enrollmentService.getEnrollment("e404"));
    }
}
