package com.nithish.finance_dashboard_backend.controller;

import com.nithish.finance_dashboard_backend.dto.CreateUserRequest;
import com.nithish.finance_dashboard_backend.dto.UpdateUserRequest;
import com.nithish.finance_dashboard_backend.model.Role;
import com.nithish.finance_dashboard_backend.model.User;
import com.nithish.finance_dashboard_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable String id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserStatus(@PathVariable String id, @RequestBody Map<String, Boolean> payload) {
        Boolean active = payload.get("active");
        if (active == null) {
            throw new IllegalArgumentException("The 'active' field is required in the request body");
        }
        return ResponseEntity.ok(userService.updateUserStatus(id, active));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserRole(@PathVariable String id, @RequestBody Map<String, String> payload) {
        String roleStr = payload.get("role");
        if (roleStr == null || roleStr.isBlank()) {
            throw new IllegalArgumentException("The 'role' field is required in the request body");
        }
        try {
            Role newRole = Role.valueOf(roleStr.toUpperCase());
            return ResponseEntity.ok(userService.updateUserRole(id, newRole));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Valid values are: VIEWER, ANALYST, ADMIN");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
