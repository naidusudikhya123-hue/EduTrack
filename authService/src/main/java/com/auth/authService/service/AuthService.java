package com.auth.authService.service;

import com.auth.authService.client.UserClient;
import com.auth.authService.dto.*;
import com.auth.authService.exception.EmailAlreadyRegisteredException;
import com.auth.authService.exception.InvalidCredentialsException;
import com.auth.authService.model.AuthUser;
import com.auth.authService.repository.AuthRepository;
import com.auth.authService.security.JwtUtil;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final UserClient userClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public AuthService(AuthRepository repo,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil,
                       UserClient userClient,
                       CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.userClient = userClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public String signup(UserSignupRequestDTO req) {

        if (repo.findByEmail(req.getEmailId()).isPresent()) {
            throw new EmailAlreadyRegisteredException("An account with this email already exists");
        }

        // Save in Auth DB
        AuthUser user = new AuthUser();
        user.setEmail(req.getEmailId());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(req.getRoleId());

        repo.save(user);

        // Prepare User Service DTO
        UserCreateRequestDTO dto = new UserCreateRequestDTO();
        dto.setEmailId(req.getEmailId());
        dto.setUserName(req.getUserName());
        dto.setRoleId(req.getRoleId());
        dto.setPassword(req.getPassword());

        // Circuit Breaker Call
        String result = circuitBreakerFactory.create("userServiceSignup").run(
                () -> {
                    userClient.createUser(dto);
                    return "User Registered Successfully";
                },
                throwable -> userServiceSignupFallback(req.getEmailId())
        );

        return result;
    }

    public LoginResponse login(LoginRequest req) {

        AuthUser user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new LoginResponse(token);
    }

    private String userServiceSignupFallback(String emailId) {
        return "User registered in Auth Service, but profile creation failed temporarily. "
                + "Please try again later or contact support. Email: " + emailId;
    }
}