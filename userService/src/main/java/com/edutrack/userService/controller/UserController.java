package com.edutrack.userService.controller;

import com.edutrack.userService.dto.UserCreateRequestDTO;
import com.edutrack.userService.dto.UserResponseDTO;
import com.edutrack.userService.dto.UserUpdateDTO;
import com.edutrack.userService.entity.User;
import com.edutrack.userService.exception.RoleNotFoundException;
import com.edutrack.userService.exception.UserAlreadyExistsException;
import com.edutrack.userService.exception.UserNotFoundException;
import com.edutrack.userService.mapper.UserMapper;
import com.edutrack.userService.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateRequestDTO requestDTO) throws RoleNotFoundException, UserAlreadyExistsException {
        User user=userService.createUser(requestDTO);
        UserResponseDTO responseDTO= UserMapper.mapUserToResponseDTO(user);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String userId) throws UserNotFoundException {
        User user=userService.getUser(userId);
        UserResponseDTO responseDTO=UserMapper.mapUserToResponseDTO(user);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers()
    {
        List<UserResponseDTO> users=userService.getAllUsers().stream().map(user->UserMapper.mapUserToResponseDTO(user)).collect(Collectors.toList());
        return new ResponseEntity<>(users,HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable String userId, @Valid @RequestBody UserUpdateDTO updateDTO) throws UserNotFoundException, RoleNotFoundException {
        User updatedUser=userService.updateUser(updateDTO,userId);
        UserResponseDTO responseDTO=UserMapper.mapUserToResponseDTO(updatedUser);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) throws UserNotFoundException {
        userService.deleteUser(userId);
        return new ResponseEntity<>("User deleted succesfully",HttpStatus.OK);
    }

    @GetMapping("role/{roleName}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable String roleName) throws RoleNotFoundException {
        List<UserResponseDTO> users=userService.getUsersForParticularRole(roleName).stream().map(user->UserMapper.mapUserToResponseDTO(user)).collect(Collectors.toList());
        return new ResponseEntity<>(users,HttpStatus.OK);

    }

}