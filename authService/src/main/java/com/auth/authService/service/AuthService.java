package com.auth.authService.service;

import com.auth.authService.client.UserClient;
import com.auth.authService.dto.*;
import com.auth.authService.model.AuthUser;
import com.auth.authService.repository.AuthRepository;
import com.auth.authService.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.auth.authService.client.UserClient;

@Service
public class AuthService {

    private final AuthRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
private final UserClient userClient;
    public AuthService(AuthRepository repo,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil,
                       UserClient userClient) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.userClient = userClient;
    }

    public String signup(UserSignupRequestDTO req) {
        if (repo.findByEmail(req.getEmailId()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        AuthUser user = new AuthUser();
        user.setEmail(req.getEmailId());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(req.getRoleId());

        repo.save(user);

        UserCreateRequestDTO dto = new UserCreateRequestDTO();
        dto.setEmailId(req.getEmailId());
        dto.setUserName(req.getUserName());
        dto.setRoleId(req.getRoleId());
        dto.setPassword(req.getPassword());

        userClient.createUser(dto);

        return "User Registered Successfully";
    }

    public LoginResponse login(LoginRequest req) {
        AuthUser user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found--"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new LoginResponse(token);
    }
}