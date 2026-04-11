package com.example.assessment_service.client;

import com.example.assessment_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users/{userId}")
    UserDTO getUserById(@PathVariable("userId") String userId);
}
