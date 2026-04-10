package com.auth.authService.controller;

import com.auth.authService.dto.LoginRequest;
import com.auth.authService.dto.LoginResponse;
import com.auth.authService.dto.UserSignupRequestDTO;
import com.auth.authService.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/signup")
    public String register(@RequestBody UserSignupRequestDTO req) {
        return service.signup(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return service.login(req);
    }
}