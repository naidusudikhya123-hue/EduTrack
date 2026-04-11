package com.auth.authService.service;

import com.auth.authService.client.UserClient;
import com.auth.authService.exception.EmailAlreadyRegisteredException;
import com.auth.authService.exception.InvalidCredentialsException;
import com.auth.authService.dto.LoginRequest;
import com.auth.authService.dto.LoginResponse;
import com.auth.authService.dto.UserCreateRequestDTO;
import com.auth.authService.dto.UserSignupRequestDTO;
import com.auth.authService.model.AuthUser;
import com.auth.authService.repository.AuthRepository;
import com.auth.authService.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private AuthService authService;

    @Test
    void signupRegistersUserAndCallsUserClient() {
        UserSignupRequestDTO req = new UserSignupRequestDTO();
        req.setUserName("testuser");
        req.setEmailId("test@example.com");
        req.setPassword("Secret1@x");
        req.setRoleId("R1");

        when(repo.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(encoder.encode("Secret1@x")).thenReturn("encoded-hash");
        when(repo.save(any(AuthUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String message = authService.signup(req);

        assertEquals("User Registered Successfully", message);
        verify(userClient).createUser(any(UserCreateRequestDTO.class));
        verify(repo).save(any(AuthUser.class));
    }

    @Test
    void signupThrowsWhenEmailExists() {
        UserSignupRequestDTO req = new UserSignupRequestDTO();
        req.setEmailId("taken@example.com");
        when(repo.findByEmail("taken@example.com")).thenReturn(Optional.of(new AuthUser()));

        assertThrows(EmailAlreadyRegisteredException.class, () -> authService.signup(req));
        verify(userClient, never()).createUser(any());
    }

    @Test
    void loginReturnsJwtWhenCredentialsValid() {
        AuthUser user = new AuthUser();
        user.setEmail("a@b.com");
        user.setPassword("stored-hash");
        user.setRole("R1");

        when(repo.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(encoder.matches("plain", "stored-hash")).thenReturn(true);
        when(jwtUtil.generateToken(eq("a@b.com"), eq("R1"))).thenReturn("jwt-token");

        LoginRequest login = new LoginRequest();
        login.setEmail("a@b.com");
        login.setPassword("plain");

        LoginResponse response = authService.login(login);

        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void loginThrowsWhenUserMissing() {
        when(repo.findByEmail("x@y.com")).thenReturn(Optional.empty());
        LoginRequest login = new LoginRequest();
        login.setEmail("x@y.com");
        login.setPassword("p");

        assertThrows(InvalidCredentialsException.class, () -> authService.login(login));
    }

    @Test
    void loginThrowsWhenPasswordWrong() {
        AuthUser user = new AuthUser();
        user.setEmail("a@b.com");
        user.setPassword("hash");
        when(repo.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(encoder.matches(any(), any())).thenReturn(false);

        LoginRequest login = new LoginRequest();
        login.setEmail("a@b.com");
        login.setPassword("wrong");

        assertThrows(InvalidCredentialsException.class, () -> authService.login(login));
    }
}
