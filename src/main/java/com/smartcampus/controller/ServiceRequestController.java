package com.smartcampus.controller;

import com.smartcampus.dto.FeedbackResponseDto;
import com.smartcampus.dto.FeedbackSubmitDto;
import com.smartcampus.dto.ServiceRequestCreateDto;
import com.smartcampus.dto.ServiceRequestResponseDto;
import com.smartcampus.dto.ServiceRequestUpdateDto;
import com.smartcampus.model.Category;
import com.smartcampus.model.RequestStatus;
import com.smartcampus.service.FeedbackService;
import com.smartcampus.service.ServiceRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ServiceRequestController {

    private final ServiceRequestService requestService;
    private final FeedbackService feedbackService;

    public ServiceRequestController(ServiceRequestService requestService, FeedbackService feedbackService) {
        this.requestService = requestService;
        this.feedbackService = feedbackService;
    }

    /**
     * GET /api/requests
     * Retrieves all service requests with optional filters:
     * - category
     * - status
     * - createdById
     * - assignedToId
     */
    @GetMapping
    public ResponseEntity<List<ServiceRequestResponseDto>> getAllRequests(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) Long createdById,
            @RequestParam(required = false) Long assignedToId) {
        List<ServiceRequestResponseDto> requests = requestService.getAllRequests(category, status, createdById, assignedToId);
        return ResponseEntity.ok(requests);
    }

    /**
     * GET /api/requests/{id}
     * Retrieves a single service request by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequestResponseDto> getRequestById(@PathVariable Long id) {
        ServiceRequestResponseDto request = requestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }

    /**
     * POST /api/requests
     * Creates a new service request.
     */
    @PostMapping
    public ResponseEntity<ServiceRequestResponseDto> createRequest(
            @Valid @RequestBody ServiceRequestCreateDto dto,
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId) {
        ServiceRequestResponseDto created = requestService.createRequest(dto, authUserId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * PUT /api/requests/{id}
     * Updates an existing service request (status, assignment, details).
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceRequestResponseDto> updateRequest(
            @PathVariable Long id,
            @RequestBody ServiceRequestUpdateDto dto,
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId) {
        ServiceRequestResponseDto updated = requestService.updateRequest(id, dto, authUserId);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/requests/{id}
     * Deletes a service request by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId) {
        requestService.deleteRequest(id, authUserId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/requests/{id}/feedback
     * Submits feedback and rating for a resolved service request.
     */
    @PostMapping("/{id}/feedback")
    public ResponseEntity<FeedbackResponseDto> submitFeedback(
            @PathVariable Long id,
            @Valid @RequestBody FeedbackSubmitDto dto,
            @RequestHeader(value = "X-User-Id", required = false) Long authUserId) {
        FeedbackResponseDto feedback = feedbackService.submitFeedback(id, dto, authUserId);
        return new ResponseEntity<>(feedback, HttpStatus.CREATED);
    }
}
