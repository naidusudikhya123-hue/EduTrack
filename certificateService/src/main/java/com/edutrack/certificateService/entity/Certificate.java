package com.edutrack.certificateService.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String courseId;
    private String certificateNumber;

    private LocalDateTime issuedAt;

    @PrePersist
    public void onCreate() {
        issuedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCourseId() {
        return courseId;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }
    public void setCertificateNumber(String certificateNumber) {

        this.certificateNumber = certificateNumber;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
}