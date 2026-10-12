package com.smartcampus.service;

import com.smartcampus.dto.ServiceRequestCreateDto;
import com.smartcampus.dto.ServiceRequestResponseDto;
import com.smartcampus.dto.ServiceRequestUpdateDto;
import com.smartcampus.exception.UnauthorizedException;
import com.smartcampus.model.*;
import com.smartcampus.repository.ServiceRequestRepository;
import com.smartcampus.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceRequestServiceTest {

    @Mock
    private ServiceRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    private ServiceRequestService requestService;

    private User student;
    private User admin;
    private User staff;

    @BeforeEach
    void setUp() {
        requestService = new ServiceRequestService(requestRepository, userRepository);
        student = new User(1L, "Keerthi", "keerthi@anurag.edu.in", "pass", Role.STUDENT, "CSE", "3rd", "Univ", "123");
        admin = new User(2L, "Admin", "admin@anurag.edu.in", "pass", Role.ADMIN, "Dept", "Staff", "Univ", "123");
        staff = new User(3L, "Staff", "staff@anurag.edu.in", "pass", Role.STAFF, "IT", "Staff", "Univ", "123");
    }

    @Test
    void testCreateRequestInitializesAsSubmitted() {
        ServiceRequestCreateDto dto = new ServiceRequestCreateDto("Wi-Fi down", "In Lab 3", Category.NETWORK, "Lab 3", 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(requestRepository.save(any(ServiceRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceRequestResponseDto response = requestService.createRequest(dto, 1L);

        assertNotNull(response);
        assertEquals("Wi-Fi down", response.getTitle());
        assertEquals(RequestStatus.SUBMITTED, response.getStatus());
    }

    @Test
    void testNonAdminCannotAssignStaff() {
        ServiceRequest req = new ServiceRequest();
        req.setId(10L);
        req.setCreatedBy(student);
        req.setStatus(RequestStatus.SUBMITTED);

        when(requestRepository.findById(10L)).thenReturn(Optional.of(req));
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        ServiceRequestUpdateDto updateDto = new ServiceRequestUpdateDto();
        updateDto.setAssignedToId(3L);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
                requestService.updateRequest(10L, updateDto, 1L));
        assertTrue(ex.getMessage().contains("Only campus administrators are authorized"));
    }

    @Test
    void testAdminCanAssignStaff() {
        ServiceRequest req = new ServiceRequest();
        req.setId(10L);
        req.setCreatedBy(student);
        req.setStatus(RequestStatus.SUBMITTED);

        when(requestRepository.findById(10L)).thenReturn(Optional.of(req));
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(3L)).thenReturn(Optional.of(staff));
        when(requestRepository.save(any(ServiceRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceRequestUpdateDto updateDto = new ServiceRequestUpdateDto();
        updateDto.setAssignedToId(3L);

        ServiceRequestResponseDto updated = requestService.updateRequest(10L, updateDto, 2L);
        assertEquals(RequestStatus.ASSIGNED, updated.getStatus());
        assertEquals("Staff", updated.getAssignedTo().getName());
    }

    @Test
    void testStaffCannotUpdateUnassignedRequest() {
        ServiceRequest req = new ServiceRequest();
        req.setId(10L);
        req.setCreatedBy(student);
        req.setAssignedTo(new User(4L, "Other Staff", "other@anurag.edu.in", "pass", Role.STAFF, "IT", "Staff", "Univ", "123"));
        req.setStatus(RequestStatus.ASSIGNED);

        when(requestRepository.findById(10L)).thenReturn(Optional.of(req));
        when(userRepository.findById(3L)).thenReturn(Optional.of(staff));

        ServiceRequestUpdateDto updateDto = new ServiceRequestUpdateDto();
        updateDto.setStatus(RequestStatus.IN_PROGRESS);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
                requestService.updateRequest(10L, updateDto, 3L));
        assertTrue(ex.getMessage().contains("Staff members can only update status for requests assigned to them"));
    }
}
