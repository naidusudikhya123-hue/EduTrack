package com.Edutrack.api_gateway.security;

import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

/**
 * Role-based rules for gateway routes. JWT claim {@code role} must match auth service:
 * ADMIN, INSTRUCTOR, STUDENT. Alias: USER → STUDENT.
 * <p>
 * ADMIN: full access after authentication.
 * INSTRUCTOR: course write, course enrollment views, certificate generation; no user admin APIs.
 * STUDENT: read/browse courses, enroll, pay, quiz; no course mutations or user admin.
 */
public final class GatewayRoleAuthorization {

    private static final AntPathMatcher PATH = new AntPathMatcher();

    private GatewayRoleAuthorization() {
    }

    public static String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "";
        }
        String r = role.trim().toUpperCase();
        if ("USER".equals(r)) {
            return "STUDENT";
        }
        // Auth DB stores user-service role ids (R1, R2, R3); JWT "role" claim uses the same ids.
        // Must match role rows in user-service (see Role entity / tests: R1=STUDENT, R2=INSTRUCTOR).
        return switch (r) {
            case "R1" -> "STUDENT";
            case "R2" -> "INSTRUCTOR";
            case "R3" -> "ADMIN";
            default -> r;
        };
    }

    public static boolean isAllowed(String role, HttpMethod method, String path) {
        String r = normalizeRole(role);
        if (r.isEmpty()) {
            return false;
        }
        if ("ADMIN".equals(r)) {
            return true;
        }
        if ("INSTRUCTOR".equals(r)) {
            return instructorAllowed(method, path);
        }
        if ("STUDENT".equals(r)) {
            return studentAllowed(method, path);
        }
        return false;
    }

    private static boolean instructorAllowed(HttpMethod method, String path) {
        // User-service admin operations
        if (method == HttpMethod.GET && PATH.match("/users", path)) {
            return false;
        }
        if (method == HttpMethod.GET && PATH.match("/users/role/**", path)) {
            return false;
        }
        if (method == HttpMethod.POST && PATH.match("/users", path)) {
            return false;
        }
        if (PATH.match("/users/**", path) && (method == HttpMethod.PUT || method == HttpMethod.DELETE)) {
            return false;
        }
        // Enroll as a student only (instructor uses course analytics endpoints instead)
        if (method == HttpMethod.POST && PATH.match("/enrollments", path)) {
            return false;
        }
        return true;
    }

    private static boolean studentAllowed(HttpMethod method, String path) {
        // Course mutations
        if (method == HttpMethod.POST && PATH.match("/courses/create", path)) {
            return false;
        }
        if (PATH.match("/courses/**", path) && (method == HttpMethod.PUT || method == HttpMethod.DELETE)) {
            return false;
        }
        // User admin
        if (method == HttpMethod.GET && PATH.match("/users", path)) {
            return false;
        }
        if (method == HttpMethod.GET && PATH.match("/users/role/**", path)) {
            return false;
        }
        if (method == HttpMethod.POST && PATH.match("/users", path)) {
            return false;
        }
        if (PATH.match("/users/**", path) && (method == HttpMethod.PUT || method == HttpMethod.DELETE)) {
            return false;
        }
        // Instructor-only enrollment overview
        if (method == HttpMethod.GET && PATH.match("/enrollments/course/**", path)) {
            return false;
        }
        // Certificate issuance
        if (method == HttpMethod.POST && PATH.match("/certificates/generate", path)) {
            return false;
        }
        // Quiz user listing (staff-style)
        if (method == HttpMethod.GET && PATH.match("/quizzes/users/**", path)) {
            return false;
        }
        return true;
    }
}
