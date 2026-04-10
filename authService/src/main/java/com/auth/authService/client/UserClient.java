package com.auth.authService.client;

import com.auth.authService.dto.UserCreateRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="user-service")
public interface UserClient {
    @PostMapping("/users")
    void createUser(@RequestBody UserCreateRequestDTO dto);
}