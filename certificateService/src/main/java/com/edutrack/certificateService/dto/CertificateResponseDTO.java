package com.edutrack.certificateService.dto;

import java.time.LocalDateTime;

public class CertificateResponseDTO {

    private String userId;
    private String courseId;
    private String certificateNumber;
    private LocalDateTime issuedAt;

    public CertificateResponseDTO(String userId, String courseId,
                                  String certificateNumber,
                                  LocalDateTime issuedAt) {
        this.userId = userId;
        this.courseId = courseId;
        this.certificateNumber = certificateNumber;
        this.issuedAt = issuedAt;
    }

    public String getUserId() {
        return userId;
    }
    public String getCourseId() {
        return courseId;
    }
    public String getCertificateNumber() {
        return certificateNumber;
    }
    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
}