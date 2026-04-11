package com.edutrack.certificateService.service;

import com.edutrack.certificateService.client.CourseClient;
import com.edutrack.certificateService.client.EnrollmentClient;
import com.edutrack.certificateService.client.UserClient;
import com.edutrack.certificateService.dto.CertificateRequestDTO;
import com.edutrack.certificateService.dto.CertificateResponseDTO;
import com.edutrack.certificateService.dto.UserEnrollmentFeignDTO;
import com.edutrack.certificateService.entity.Certificate;
import com.edutrack.certificateService.exception.CertificateNotFoundException;
import com.edutrack.certificateService.repository.CertificateRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository repository;
    private final UserClient userClient;
    private final CourseClient courseClient;
    private final EnrollmentClient enrollmentClient;

    public CertificateServiceImpl(
            CertificateRepository repository,
            UserClient userClient,
            CourseClient courseClient,
            EnrollmentClient enrollmentClient) {
        this.repository = repository;
        this.userClient = userClient;
        this.courseClient = courseClient;
        this.enrollmentClient = enrollmentClient;
    }

    @Override
    public CertificateResponseDTO generateCertificate(CertificateRequestDTO request) {

        // prevent duplicate certificates
        Certificate existing = repository
                .findByUserIdAndCourseId(request.getUserId(), request.getCourseId())
                .orElse(null);

        if (existing != null) {
            return map(existing);
        }

        validateIssueEligibility(request.getUserId(), request.getCourseId());

        Certificate cert = new Certificate();
        cert.setUserId(request.getUserId());
        cert.setCourseId(request.getCourseId());
        cert.setCertificateNumber(generateCertificateNumber());

        repository.save(cert);

        return map(cert);
    }

    private void validateIssueEligibility(String userId, String courseId) {
        try {
            userClient.getUserById(userId);
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new IllegalArgumentException("User not found: " + userId);
            }
            throw new IllegalStateException("User service unavailable", e);
        }
        try {
            courseClient.getCourseById(courseId);
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new IllegalArgumentException("Course not found: " + courseId);
            }
            throw new IllegalStateException("Course service unavailable", e);
        }
        List<UserEnrollmentFeignDTO> enrollments = enrollmentClient.getEnrollmentsForUser(userId);
        boolean activeEnrollment = enrollments.stream()
                .anyMatch(e -> courseId.equals(e.getCourseId())
                        && e.getStatus() != null
                        && !"CANCELED".equalsIgnoreCase(e.getStatus()));
        if (!activeEnrollment) {
            throw new IllegalArgumentException(
                    "No active enrollment for user " + userId + " and course " + courseId);
        }
    }

    @Override
    public CertificateResponseDTO getCertificate(String userId, String courseId) {
        Certificate cert = repository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new CertificateNotFoundException("Certificate not found"));

        return map(cert);
    }

    private String generateCertificateNumber() {
        return "CERT-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private CertificateResponseDTO map(Certificate cert) {
        return new CertificateResponseDTO(
                cert.getUserId(),
                cert.getCourseId(),
                cert.getCertificateNumber(),
                cert.getIssuedAt()
        );
    }
}