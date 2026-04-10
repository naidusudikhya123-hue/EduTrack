package com.edutrack.userService.repository;

import com.edutrack.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmailId(String emailId);

    User findByEmailId(String emailId);

    List<User> findByRole_RoleId(String roleId);

    List<User> findByUserNameContainingIgnoreCase(String userName);

    List<User> findByRole_RoleName(String roleName);
}