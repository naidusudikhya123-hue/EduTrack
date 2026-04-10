package com.edutrack.certificateService.service;

import com.edutrack.certificateService.dto.CertificateRequestDTO;
import com.edutrack.certificateService.dto.CertificateResponseDTO;

public interface CertificateService {

    CertificateResponseDTO generateCertificate(CertificateRequestDTO request);

    CertificateResponseDTO getCertificate(String userId, String courseId);
}