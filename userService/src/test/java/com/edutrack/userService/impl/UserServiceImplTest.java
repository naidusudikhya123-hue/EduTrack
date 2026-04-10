package com.edutrack.userService.impl;

import com.edutrack.userService.dto.UserCreateRequestDTO;
import com.edutrack.userService.dto.UserUpdateDTO;
import com.edutrack.userService.entity.Role;
import com.edutrack.userService.entity.User;
import com.edutrack.userService.exception.RoleNotFoundException;
import com.edutrack.userService.exception.UserAlreadyExistsException;
import com.edutrack.userService.exception.UserNotFoundException;
import com.edutrack.userService.repository.RoleRepository;
import com.edutrack.userService.repository.UserRepository;
import com.edutrack.userService.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserSavesMappedUserWithGeneratedId() throws Exception {
        UserCreateRequestDTO dto = new UserCreateRequestDTO();
        dto.setUserName("Vijay");
        dto.setEmailId("vijay@example.com");
        dto.setPassword("Password@123");
        dto.setRoleId("R1");

        Role role = new Role("R1", "STUDENT");

        when(userRepository.existsByEmailId(dto.getEmailId())).thenReturn(false);
        when(userRepository.count()).thenReturn(0L);
        when(roleRepository.findById("R1")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.createUser(dto);

        assertEquals("u1", saved.getUserId());
        assertEquals("vijay@example.com", saved.getEmailId());
        assertEquals(role, saved.getRole());
    }

    @Test
    void createUserThrowsWhenEmailAlreadyExists() {
        UserCreateRequestDTO dto = new UserCreateRequestDTO();
        dto.setEmailId("vijay@example.com");

        when(userRepository.existsByEmailId(dto.getEmailId())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(dto));
    }

    @Test
    void updateUserUpdatesBasicFieldsAndRole() throws Exception {
        Role oldRole = new Role("R1", "STUDENT");
        Role newRole = new Role("R2", "INSTRUCTOR");
        User existing = new User();
        existing.setUserId("u1");
        existing.setUserName("Old Name");
        existing.setEmailId("old@example.com");
        existing.setRole(oldRole);

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setUserName("New Name");
        updateDTO.setEmailId("new@example.com");
        updateDTO.setRoleId("R2");

        when(userRepository.findById("u1")).thenReturn(Optional.of(existing));
        when(roleRepository.findById("R2")).thenReturn(Optional.of(newRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(updateDTO, "u1");

        assertEquals("New Name", updated.getUserName());
        assertEquals("new@example.com", updated.getEmailId());
        assertEquals(newRole, updated.getRole());
    }

    @Test
    void deleteUserThrowsWhenMissing() {
        when(userRepository.findById("u404")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser("u404"));
    }

    @Test
    void getUsersForParticularRoleThrowsWhenRoleMissing() {
        when(roleRepository.findByRoleName("ADMIN")).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> userService.getUsersForParticularRole("ADMIN"));
    }
}
