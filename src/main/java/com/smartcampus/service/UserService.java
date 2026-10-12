package com.smartcampus.service;

import com.smartcampus.dto.LoginRequest;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.model.Role;
import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + user.getEmail());
        }
        // Never allow public self-registration as ADMIN
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Admin users cannot be registered publicly.");
        }
        if (user.getRole() == null) {
            user.setRole(Role.STUDENT);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User authenticateUser(LoginRequest request) {
        User user = getUserByEmail(request.getEmail());
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!matches && request.getPassword().equals(user.getPassword())) {
            // Automatically upgrade legacy unhashed password in MySQL to BCrypt
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user = userRepository.save(user);
            matches = true;
        }
        if (!matches) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return user;
    }
}
