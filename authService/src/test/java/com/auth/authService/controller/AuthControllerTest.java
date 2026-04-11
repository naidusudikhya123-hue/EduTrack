package com.auth.authService.controller;

import com.auth.authService.dto.LoginRequest;
import com.auth.authService.dto.LoginResponse;
import com.auth.authService.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void loginDelegatesToService() {
        when(authService.login(any(LoginRequest.class))).thenReturn(new LoginResponse("jwt-here"));

        LoginRequest req = new LoginRequest();
        req.setEmail("a@b.com");
        req.setPassword("Secret1@x");

        LoginResponse response = authController.login(req);

        assertEquals("jwt-here", response.getToken());
        verify(authService).login(any(LoginRequest.class));
    }
}
