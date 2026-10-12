package com.smartcampus.service;

import com.smartcampus.dto.LoginRequest;
import com.smartcampus.model.Role;
import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void testCreateUserSuccess() {
        User user = new User(null, "Test Student", "student@test.edu", "pass123", Role.STUDENT, "CSE", "3rd", "Univ", "12345");
        when(userRepository.existsByEmail("student@test.edu")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser(user);

        assertNotNull(created);
        assertEquals("hashedPassword", created.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserRejectsPublicAdmin() {
        User admin = new User(null, "Admin", "admin@test.edu", "pass123", Role.ADMIN, "Dept", "Staff", "Univ", "12345");
        when(userRepository.existsByEmail("admin@test.edu")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.createUser(admin));
        assertTrue(ex.getMessage().contains("Admin users cannot be registered publicly"));
    }

    @Test
    void testCreateUserDuplicateEmail() {
        User user = new User(null, "Student", "dup@test.edu", "pass123", Role.STUDENT, "CSE", "1st", "Univ", "12345");
        when(userRepository.existsByEmail("dup@test.edu")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    void testAuthenticateSuccess() {
        User user = new User(1L, "Keerthi", "keerthi@anurag.edu.in", "hashedPass", Role.STUDENT, "CSE", "3rd Year", "Anurag", "123");
        when(userRepository.findByEmail("keerthi@anurag.edu.in")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedPass")).thenReturn(true);

        User authenticated = userService.authenticateUser(new LoginRequest("keerthi@anurag.edu.in", "password123"));
        assertEquals("Keerthi", authenticated.getName());
    }

    @Test
    void testAuthenticateInvalidPassword() {
        User user = new User(1L, "Keerthi", "keerthi@anurag.edu.in", "hashedPass", Role.STUDENT, "CSE", "3rd Year", "Anurag", "123");
        when(userRepository.findByEmail("keerthi@anurag.edu.in")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "hashedPass")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                userService.authenticateUser(new LoginRequest("keerthi@anurag.edu.in", "wrongpass")));
    }
}
