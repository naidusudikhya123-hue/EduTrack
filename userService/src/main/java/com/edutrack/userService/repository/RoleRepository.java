package com.edutrack.userService.repository;

import com.edutrack.userService.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,String> {

    Optional<Role> findByRoleName(String roleName);
}