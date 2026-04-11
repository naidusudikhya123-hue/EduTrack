package com.edutrack.certificateService.client;

import com.edutrack.certificateService.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users/{userId}")
    UserResponseDTO getUserById(@PathVariable("userId") String userId);
}
