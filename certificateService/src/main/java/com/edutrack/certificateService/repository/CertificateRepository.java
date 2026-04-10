package com.edutrack.certificateService.repository;

import com.edutrack.certificateService.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByUserIdAndCourseId(String userId, String courseId);
}