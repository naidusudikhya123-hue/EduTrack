package com.Edutrack.api_gateway.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GatewayRoleAuthorizationTest {

    @Test
    void normalizeRoleMapsUserToStudent() {
        assertEquals("STUDENT", GatewayRoleAuthorization.normalizeRole("user"));
    }

    @Test
    void normalizeRoleMapsRoleIds() {
        assertEquals("ADMIN", GatewayRoleAuthorization.normalizeRole("R1"));
        assertEquals("INSTRUCTOR", GatewayRoleAuthorization.normalizeRole("R2"));
        assertEquals("STUDENT", GatewayRoleAuthorization.normalizeRole("R3"));
    }

    @Test
    void adminAllowedEverywhere() {
        assertTrue(GatewayRoleAuthorization.isAllowed("ADMIN", HttpMethod.DELETE, "/users/u1"));
        assertTrue(GatewayRoleAuthorization.isAllowed("R1", HttpMethod.DELETE, "/users/u1"));
    }

    @Test
    void studentCannotDeleteCourse() {
        assertFalse(GatewayRoleAuthorization.isAllowed(
                "STUDENT", HttpMethod.DELETE, "/courses/c1"));
        assertFalse(GatewayRoleAuthorization.isAllowed(
                "R3", HttpMethod.DELETE, "/courses/c1"));
    }

    @Test
    void studentCanGetCourses() {
        assertTrue(GatewayRoleAuthorization.isAllowed(
                "STUDENT", HttpMethod.GET, "/courses/c1"));
    }
}
