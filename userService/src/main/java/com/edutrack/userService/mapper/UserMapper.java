package com.edutrack.userService.mapper;

import com.edutrack.userService.dto.UserCreateRequestDTO;
import com.edutrack.userService.dto.UserResponseDTO;
import com.edutrack.userService.entity.Role;
import com.edutrack.userService.entity.User;

public class UserMapper {

    public static UserResponseDTO mapUserToResponseDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setEmailId(user.getEmailId());
        dto.setRoleId(user.getRole().getRoleId());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }

    public static User mapCreateRequestToUser(UserCreateRequestDTO dto, Role role) {

        User user = new User();
        user.setUserName(dto.getUserName());
        user.setEmailId(dto.getEmailId());
        user.setPassword(dto.getPassword());
        user.setRole(role);

        return user;
    }
}