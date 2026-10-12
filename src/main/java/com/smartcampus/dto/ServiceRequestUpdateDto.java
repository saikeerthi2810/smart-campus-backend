package com.smartcampus.dto;

import com.smartcampus.model.Category;
import com.smartcampus.model.RequestStatus;

public class ServiceRequestUpdateDto {
    private String title;
    private String description;
    private Category category;
    private RequestStatus status;
    private String location;
    private Long assignedToId;

    public ServiceRequestUpdateDto() {
    }

    public ServiceRequestUpdateDto(String title, String description, Category category, RequestStatus status, String location, Long assignedToId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = status;
        this.location = location;
        this.assignedToId = assignedToId;
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

    public Long getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }
}
