package com.Edutrack.enrollments.entity;

import com.Edutrack.enrollments.enums.EnrollmentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id","course_id"})})
public class Enrollment {

    @Id
    private String enrollmentId;

    @Column(name="user_id",nullable = false)
    private String userId;

    @Column(name="course_id",nullable = false)
    private String courseId;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt=LocalDateTime.now();
        this.updatedAt=LocalDateTime.now();
        this.status=EnrollmentStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt=LocalDateTime.now();
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
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

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}