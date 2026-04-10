package com.edutrack.userService.service.impl;

import com.edutrack.userService.dto.UserCreateRequestDTO;
import com.edutrack.userService.dto.UserUpdateDTO;
import com.edutrack.userService.entity.Role;
import com.edutrack.userService.entity.User;
import com.edutrack.userService.exception.RoleNotFoundException;
import com.edutrack.userService.exception.UserAlreadyExistsException;
import com.edutrack.userService.exception.UserNotFoundException;
import com.edutrack.userService.mapper.UserMapper;
import com.edutrack.userService.repository.RoleRepository;
import com.edutrack.userService.repository.UserRepository;
import com.edutrack.userService.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User createUser(UserCreateRequestDTO userDTO) throws UserAlreadyExistsException, RoleNotFoundException {
        if (userRepository.existsByEmailId(userDTO.getEmailId())) {
            throw new UserAlreadyExistsException("Email already registered please use a different email");
        }

        Role role = roleRepository.findById(userDTO.getRoleId()).orElseThrow(() -> new RoleNotFoundException("Role provided is not found " + userDTO.getRoleId()));

        User user = UserMapper.mapCreateRequestToUser(userDTO, role);
        user.setUserId(generateUserId());

        return userRepository.save(user);
    }

    @Override
    public User getUser(String userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UserUpdateDTO userDTO, String userId) throws UserNotFoundException, RoleNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userId));

        existingUser.setUserName(userDTO.getUserName());
        existingUser.setEmailId(userDTO.getEmailId());

        if (userDTO.getRoleId() != null) {
            Role role = roleRepository.findById(userDTO.getRoleId()).orElseThrow(() -> new RoleNotFoundException("Role not found: " + userDTO.getRoleId()));
            existingUser.setRole(role);
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(String userId) throws UserNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userId));

        userRepository.delete(existingUser);
    }

    @Override
    public List<User> getUsersForParticularRole(String roleName) throws RoleNotFoundException {
        Role role = roleRepository.findByRoleName(roleName).orElseThrow(() -> new RoleNotFoundException("Role does not exists: " + roleName));
        return userRepository.findByRole_RoleName(roleName);
    }

    private String generateUserId() {
        return "u" + (userRepository.count() + 1);
    }
}