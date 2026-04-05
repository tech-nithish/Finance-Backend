package com.nithish.finance_dashboard_backend.service;

import com.nithish.finance_dashboard_backend.dto.CreateUserRequest;
import com.nithish.finance_dashboard_backend.dto.UpdateUserRequest;
import com.nithish.finance_dashboard_backend.exception.ResourceNotFoundException;
import com.nithish.finance_dashboard_backend.model.Role;
import com.nithish.finance_dashboard_backend.model.User;
import com.nithish.finance_dashboard_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.VIEWER)
                .active(true)
                .build();
        return userRepository.save(user);
    }

    public User updateUser(String id, UpdateUserRequest request) {
        User existingUser = getUserById(id);
        if (request.getName() != null && !request.getName().isBlank()) {
            existingUser.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            existingUser.setEmail(request.getEmail());
        }
        // Encode password if a new one is provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    public User updateUserStatus(String id, boolean status) {
        User user = getUserById(id);
        user.setActive(status);
        return userRepository.save(user);
    }

    public User updateUserRole(String id, Role role) {
        User user = getUserById(id);
        user.setRole(role);
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
