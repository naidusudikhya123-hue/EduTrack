package com.edutrack.userService.service;

import com.edutrack.userService.dto.UserCreateRequestDTO;
import com.edutrack.userService.dto.UserUpdateDTO;
import com.edutrack.userService.entity.User;
import com.edutrack.userService.exception.RoleNotFoundException;
import com.edutrack.userService.exception.UserAlreadyExistsException;
import com.edutrack.userService.exception.UserNotFoundException;

import java.util.List;

public interface UserService{
    public User createUser(UserCreateRequestDTO userDTO) throws UserAlreadyExistsException, RoleNotFoundException;
    public User getUser(String userId) throws UserNotFoundException;
    public List<User> getAllUsers();
    public User updateUser(UserUpdateDTO userDTO, String userId) throws UserNotFoundException, RoleNotFoundException;
    public void deleteUser(String userId) throws UserNotFoundException;
    public List<User> getUsersForParticularRole(String roleName) throws RoleNotFoundException;
}