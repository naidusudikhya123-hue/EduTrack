package com.edutrack.userService.controller;

import com.edutrack.userService.dto.UserResponseDTO;
import com.edutrack.userService.entity.Role;
import com.edutrack.userService.entity.User;
import com.edutrack.userService.exception.UserNotFoundException;
import com.edutrack.userService.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getUserReturns200() throws Exception {
        Role role = new Role("R1", "STUDENT");
        User user = new User();
        user.setUserId("u1");
        user.setUserName("Alice");
        user.setEmailId("a@b.com");
        user.setRole(role);

        when(userService.getUser("u1")).thenReturn(user);

        ResponseEntity<UserResponseDTO> response = userController.getUser("u1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("u1", response.getBody().getUserId());
        verify(userService).getUser(eq("u1"));
    }

    @Test
    void getUserPropagatesNotFound() throws Exception {
        when(userService.getUser("missing")).thenThrow(new UserNotFoundException("not found"));

        assertThrows(UserNotFoundException.class, () -> userController.getUser("missing"));
    }

    @Test
    void getAllUsersReturnsOk() {
        when(userService.getAllUsers()).thenReturn(List.of());

        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}
