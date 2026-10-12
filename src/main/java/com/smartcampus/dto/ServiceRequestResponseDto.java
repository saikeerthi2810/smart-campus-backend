package com.smartcampus.dto;

import com.smartcampus.model.Category;
import com.smartcampus.model.RequestStatus;
import com.smartcampus.model.ServiceRequest;

import java.time.LocalDateTime;

public class ServiceRequestResponseDto {
    private Long id;
    private String title;
    private String description;
    private Category category;
    private RequestStatus status;
    private String location;
    private UserResponseDto createdBy;
    private UserResponseDto assignedTo;
    private FeedbackResponseDto feedback;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ServiceRequestResponseDto() {
    }

    public ServiceRequestResponseDto(ServiceRequest request) {
        if (request != null) {
            this.id = request.getId();
            this.title = request.getTitle();
            this.description = request.getDescription();
            this.category = request.getCategory();
            this.status = request.getStatus();
            this.location = request.getLocation();
            if (request.getCreatedBy() != null) {
                this.createdBy = new UserResponseDto(request.getCreatedBy());
            }
            if (request.getAssignedTo() != null) {
                this.assignedTo = new UserResponseDto(request.getAssignedTo());
            }
            if (request.getFeedback() != null) {
                this.feedback = new FeedbackResponseDto(request.getFeedback());
            }
            this.createdAt = request.getCreatedAt();
            this.updatedAt = request.getUpdatedAt();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserResponseDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserResponseDto createdBy) {
        this.createdBy = createdBy;
    }

    public UserResponseDto getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserResponseDto assignedTo) {
        this.assignedTo = assignedTo;
    }

    public FeedbackResponseDto getFeedback() {
        return feedback;
    }

    public void setFeedback(FeedbackResponseDto feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
