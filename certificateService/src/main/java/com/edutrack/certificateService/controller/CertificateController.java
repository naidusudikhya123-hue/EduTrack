package com.edutrack.certificateService.controller;

import com.edutrack.certificateService.dto.CertificateRequestDTO;
import com.edutrack.certificateService.dto.CertificateResponseDTO;
import com.edutrack.certificateService.service.CertificateService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private final CertificateService service;

    public CertificateController(CertificateService service) {
        this.service = service;
    }

    // generate certificate
    @PostMapping("/generate")
    public CertificateResponseDTO generate(@RequestBody CertificateRequestDTO request) {
        return service.generateCertificate(request);
    }

    // get certificate
    @GetMapping("/{userId}/{courseId}")
    public CertificateResponseDTO get(
            @PathVariable String userId,
            @PathVariable String courseId) {

        return service.getCertificate(userId, courseId);
    }
}