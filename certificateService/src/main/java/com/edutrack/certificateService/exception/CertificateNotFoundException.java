package com.edutrack.certificateService.exception;

public class CertificateNotFoundException extends RuntimeException {

    public CertificateNotFoundException(String message) {
        super(message);
    }
}
