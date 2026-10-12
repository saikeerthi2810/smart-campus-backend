package com.smartcampus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.dto.LoginRequest;
import com.smartcampus.dto.UserResponseDto;
import com.smartcampus.exception.GlobalExceptionHandler;
import com.smartcampus.model.Role;
import com.smartcampus.model.User;
import com.smartcampus.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginRequest req = new LoginRequest("keerthi@anurag.edu.in", "password123");

        User user = new User(1L, "Keerthi", "keerthi@anurag.edu.in", "hashedPassword",
                Role.STUDENT, "CSE", "3rd Year", "Anurag University", "+91 9876543210");

        when(userService.authenticateUser(any(LoginRequest.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Keerthi"))
                .andExpect(jsonPath("$.email").value("keerthi@anurag.edu.in"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.department").value("CSE"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        LoginRequest req = new LoginRequest("keerthi@anurag.edu.in", "wrongpassword");

        when(userService.authenticateUser(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid email or password"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        User regUser = new User(null, "Rajesh Kumar", "rajesh@anurag.edu.in", "plainpass",
                Role.STUDENT, "ECE", "2nd Year", "Anurag University", "+91 9123456789");

        User savedUser = new User(10L, "Rajesh Kumar", "rajesh@anurag.edu.in", "encodedPass",
                Role.STUDENT, "ECE", "2nd Year", "Anurag University", "+91 9123456789");

        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Rajesh Kumar"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User(2L, "Campus Administrator", "admin@anurag.edu.in", "hashedPass",
                Role.ADMIN, "Estate & Facilities", "Staff", "Anurag University", "+91 9123456780");

        when(userService.getUserById(2L)).thenReturn(user);

        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Campus Administrator"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}
