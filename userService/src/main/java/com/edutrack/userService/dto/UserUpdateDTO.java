package com.edutrack.userService.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class UserUpdateDTO {

    @NotBlank(message = "User Name cannot be blank")
    @Size(min = 3, max = 50, message = "User name must be between 3 and 50 characters")
    private String userName;

    @NotBlank(message = "Please provide emailId")
    @Email(message = "Invalid email format")
    private String emailId;

    @NotNull(message = "RoleId is mandatory")
    private String roleId;

    public UserUpdateDTO()
    {

    }

    public UserUpdateDTO(String userName, String emailId, String roleId, LocalDateTime updatedAt) {
        this.userName = userName;
        this.emailId = emailId;
        this.roleId = roleId;
    }

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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

}