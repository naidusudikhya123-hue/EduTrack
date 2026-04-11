package com.edutrack.certificateService.client;

import com.edutrack.certificateService.dto.UserEnrollmentFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "enrollments")
public interface EnrollmentClient {

    @GetMapping("/enrollments/users/{userId}/details")
    List<UserEnrollmentFeignDTO> getEnrollmentsForUser(@PathVariable("userId") String userId);
}
