package com.example.Payment_Service.client;

import com.example.Payment_Service.dto.EnrollmentCreateFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "enrollments")
public interface EnrollmentClient {

    @PostMapping("/enrollments")
    void createEnrollment(@RequestBody EnrollmentCreateFeignDTO dto);
}
