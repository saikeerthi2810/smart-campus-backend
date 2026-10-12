package com.smartcampus.service;

import com.smartcampus.dto.FeedbackResponseDto;
import com.smartcampus.dto.FeedbackSubmitDto;
import com.smartcampus.exception.UnauthorizedException;
import com.smartcampus.model.*;
import com.smartcampus.repository.FeedbackRepository;
import com.smartcampus.repository.ServiceRequestRepository;
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
public class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private ServiceRequestRepository requestRepository;

    private FeedbackService feedbackService;

    private User student;
    private ServiceRequest resolvedRequest;

    @BeforeEach
    void setUp() {
        feedbackService = new FeedbackService(feedbackRepository, requestRepository);
        student = new User(1L, "Keerthi", "keerthi@anurag.edu.in", "pass", Role.STUDENT, "CSE", "3rd", "Univ", "123");
        resolvedRequest = new ServiceRequest();
        resolvedRequest.setId(10L);
        resolvedRequest.setCreatedBy(student);
        resolvedRequest.setStatus(RequestStatus.RESOLVED);
    }

    @Test
    void testSubmitFeedbackSuccess() {
        when(requestRepository.findById(10L)).thenReturn(Optional.of(resolvedRequest));
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(invocation -> {
            Feedback f = invocation.getArgument(0);
            f.setId(100L);
            return f;
        });

        FeedbackSubmitDto dto = new FeedbackSubmitDto(5, "Excellent work");
        FeedbackResponseDto response = feedbackService.submitFeedback(10L, dto, 1L);

        assertNotNull(response);
        assertEquals(5, response.getRating());
        assertEquals("Excellent work", response.getComments());
    }

    @Test
    void testSubmitFeedbackFailsIfUnresolved() {
        resolvedRequest.setStatus(RequestStatus.IN_PROGRESS);
        when(requestRepository.findById(10L)).thenReturn(Optional.of(resolvedRequest));

        FeedbackSubmitDto dto = new FeedbackSubmitDto(5, "Good");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                feedbackService.submitFeedback(10L, dto, 1L));
        assertTrue(ex.getMessage().contains("Feedback can only be submitted for RESOLVED"));
    }

    @Test
    void testSubmitFeedbackRejectsNonCreator() {
        when(requestRepository.findById(10L)).thenReturn(Optional.of(resolvedRequest));

        FeedbackSubmitDto dto = new FeedbackSubmitDto(4, "Done");
        assertThrows(UnauthorizedException.class, () ->
                feedbackService.submitFeedback(10L, dto, 999L));
    }

    @Test
    void testSubmitFeedbackRejectsDuplicate() {
        resolvedRequest.setFeedback(new Feedback());
        when(requestRepository.findById(10L)).thenReturn(Optional.of(resolvedRequest));

        FeedbackSubmitDto dto = new FeedbackSubmitDto(4, "Again");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                feedbackService.submitFeedback(10L, dto, 1L));
        assertTrue(ex.getMessage().contains("Feedback has already been submitted"));
    }
}
