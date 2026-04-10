package com.edutrack.certificateService.service;

import com.edutrack.certificateService.dto.CertificateRequestDTO;
import com.edutrack.certificateService.dto.CertificateResponseDTO;
import com.edutrack.certificateService.entity.Certificate;
import com.edutrack.certificateService.repository.CertificateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateServiceImplTest {

    @Mock
    private CertificateRepository repository;

    @InjectMocks
    private CertificateServiceImpl certificateService;

    @Test
    void generateCertificateReturnsExistingCertificateWhenPresent() {
        CertificateRequestDTO request = new CertificateRequestDTO();
        request.setUserId("u1");
        request.setCourseId("c1");

        Certificate existing = new Certificate();
        existing.setUserId("u1");
        existing.setCourseId("c1");
        existing.setCertificateNumber("CERT-12345678");

        when(repository.findByUserIdAndCourseId("u1", "c1")).thenReturn(Optional.of(existing));

        CertificateResponseDTO result = certificateService.generateCertificate(request);

        assertEquals("CERT-12345678", result.getCertificateNumber());
        verify(repository, never()).save(ArgumentMatchers.any(Certificate.class));
    }

    @Test
    void generateCertificateCreatesNewCertificateWhenMissing() {
        CertificateRequestDTO request = new CertificateRequestDTO();
        request.setUserId("u1");
        request.setCourseId("c1");

        when(repository.findByUserIdAndCourseId("u1", "c1")).thenReturn(Optional.empty());

        CertificateResponseDTO result = certificateService.generateCertificate(request);

        assertEquals("u1", result.getUserId());
        assertNotNull(result.getCertificateNumber());
        verify(repository).save(ArgumentMatchers.any(Certificate.class));
    }

    @Test
    void getCertificateThrowsWhenMissing() {
        when(repository.findByUserIdAndCourseId("u404", "c404")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> certificateService.getCertificate("u404", "c404"));
    }
}
