package com.edutrack.certificateService.controller;

import com.edutrack.certificateService.dto.CertificateRequestDTO;
import com.edutrack.certificateService.dto.CertificateResponseDTO;
import com.edutrack.certificateService.service.CertificateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateControllerTest {

    @Mock
    private CertificateService certificateService;

    @InjectMocks
    private CertificateController certificateController;

    @Test
    void generateDelegatesToService() {
        CertificateResponseDTO dto = new CertificateResponseDTO(
                "u1", "c1", "CERT-ABC", LocalDateTime.now());
        when(certificateService.generateCertificate(any(CertificateRequestDTO.class))).thenReturn(dto);

        CertificateRequestDTO request = new CertificateRequestDTO();
        request.setUserId("u1");
        request.setCourseId("c1");

        CertificateResponseDTO result = certificateController.generate(request);

        assertEquals("CERT-ABC", result.getCertificateNumber());
        verify(certificateService).generateCertificate(any(CertificateRequestDTO.class));
    }

    @Test
    void getDelegatesToService() {
        CertificateResponseDTO dto = new CertificateResponseDTO(
                "u1", "c1", "CERT-X", LocalDateTime.now());
        when(certificateService.getCertificate("u1", "c1")).thenReturn(dto);

        CertificateResponseDTO result = certificateController.get("u1", "c1");

        assertEquals("u1", result.getUserId());
    }
}
