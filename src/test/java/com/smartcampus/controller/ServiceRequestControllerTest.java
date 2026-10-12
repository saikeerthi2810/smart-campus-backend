package com.smartcampus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.dto.*;
import com.smartcampus.model.Category;
import com.smartcampus.model.RequestStatus;
import com.smartcampus.service.FeedbackService;
import com.smartcampus.service.ServiceRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ServiceRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ServiceRequestService requestService;

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private ServiceRequestController requestController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllRequests() throws Exception {
        ServiceRequestResponseDto dto = new ServiceRequestResponseDto();
        dto.setId(1L);
        dto.setTitle("Wi-Fi down");
        dto.setCategory(Category.NETWORK);
        dto.setStatus(RequestStatus.SUBMITTED);

        when(requestService.getAllRequests(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Wi-Fi down"));
    }

    @Test
    void testGetRequestById() throws Exception {
        ServiceRequestResponseDto dto = new ServiceRequestResponseDto();
        dto.setId(10L);
        dto.setTitle("Water leakage");
        dto.setStatus(RequestStatus.ASSIGNED);

        when(requestService.getRequestById(10L)).thenReturn(dto);

        mockMvc.perform(get("/api/requests/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Water leakage"));
    }

    @Test
    void testCreateRequest() throws Exception {
        ServiceRequestCreateDto createDto = new ServiceRequestCreateDto("Projector flickering", "Seminar Hall", Category.ELECTRICAL, "Block A", 1L);
        ServiceRequestResponseDto resDto = new ServiceRequestResponseDto();
        resDto.setId(20L);
        resDto.setTitle("Projector flickering");
        resDto.setStatus(RequestStatus.SUBMITTED);

        when(requestService.createRequest(any(ServiceRequestCreateDto.class), any()))
                .thenReturn(resDto);

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", "1")
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.title").value("Projector flickering"));
    }

    @Test
    void testUpdateRequest() throws Exception {
        ServiceRequestUpdateDto updateDto = new ServiceRequestUpdateDto();
        updateDto.setStatus(RequestStatus.IN_PROGRESS);

        ServiceRequestResponseDto resDto = new ServiceRequestResponseDto();
        resDto.setId(10L);
        resDto.setStatus(RequestStatus.IN_PROGRESS);

        when(requestService.updateRequest(eq(10L), any(ServiceRequestUpdateDto.class), any()))
                .thenReturn(resDto);

        mockMvc.perform(put("/api/requests/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", "2")
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void testSubmitFeedback() throws Exception {
        FeedbackSubmitDto feedbackDto = new FeedbackSubmitDto(5, "Resolved quickly!");
        FeedbackResponseDto resDto = new FeedbackResponseDto();
        resDto.setId(100L);
        resDto.setRating(5);
        resDto.setComments("Resolved quickly!");

        when(feedbackService.submitFeedback(eq(10L), any(FeedbackSubmitDto.class), any()))
                .thenReturn(resDto);

        mockMvc.perform(post("/api/requests/10/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", "1")
                        .content(objectMapper.writeValueAsString(feedbackDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comments").value("Resolved quickly!"));
    }
}
