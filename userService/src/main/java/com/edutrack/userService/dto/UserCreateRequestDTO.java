package com.edutrack.userService.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class UserCreateRequestDTO {

    @NotBlank(message = "User Name cannot be blank")
    @Size(min = 3, max = 50, message = "User name must be between 3 and 50 characters")
    private String userName;

    @NotBlank(message = "Please provide emailId")
    @Email(message = "Invalid email format")
    private String emailId;

    @NotBlank(message = "Provide your password")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must be at least 8 characters long, contain one uppercase, one lowercase, one digit, and one special character"
    )
    private String password;

    @NotNull(message = "RoleId is mandatory")
    @Pattern(
            regexp = "^R\\d+$",
            message = "RoleId must be in format R1, R2, R3"
    )

    @NotNull(message = "RoleId is mandatory")
    private String roleId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

}