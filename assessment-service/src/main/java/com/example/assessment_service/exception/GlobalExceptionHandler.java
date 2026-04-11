package com.example.assessment_service.exception;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static Map<String, Object> body(int status, String error, String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("status", status);
        m.put("error", error);
        m.put("message", message);
        return m;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(UpstreamServiceException.class)
    public ResponseEntity<Map<String, Object>> handleUpstream(UpstreamServiceException ex) {
        log.error("Upstream service error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(body(HttpStatus.BAD_GATEWAY.value(), "Bad Gateway", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Validation failed"
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.warn("Validation error: {}", msg);
        return ResponseEntity.badRequest()
                .body(body(HttpStatus.BAD_REQUEST.value(), "Bad Request", msg));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeign(FeignException ex) {
        int code = ex.status();
        log.error("Feign client error status={}", code, ex);
        if (code == 404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(body(HttpStatus.NOT_FOUND.value(), "Not Found", "Remote resource not found"));
        }
        HttpStatus status = code > 0 && HttpStatus.resolve(code) != null
                ? HttpStatus.valueOf(code)
                : HttpStatus.BAD_GATEWAY;
        return ResponseEntity.status(status)
                .body(body(status.value(), status.getReasonPhrase(), "Downstream service error"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unhandled error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                        "Something went wrong. Please try again."));
    }
}
