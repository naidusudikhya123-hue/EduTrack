package com.edutrack.certificateService.service;

import com.edutrack.certificateService.dto.CertificateRequestDTO;
import com.edutrack.certificateService.dto.CertificateResponseDTO;
import com.edutrack.certificateService.entity.Certificate;
import com.edutrack.certificateService.repository.CertificateRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository repository;

    public CertificateServiceImpl(CertificateRepository repository) {
        this.repository = repository;
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

        Certificate cert = new Certificate();
        cert.setUserId(request.getUserId());
        cert.setCourseId(request.getCourseId());
        cert.setCertificateNumber(generateCertificateNumber());

        repository.save(cert);

        return map(cert);
    }

    @Override
    public CertificateResponseDTO getCertificate(String userId, String courseId) {
        Certificate cert = repository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

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