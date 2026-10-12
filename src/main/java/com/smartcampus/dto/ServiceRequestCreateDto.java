package com.smartcampus.dto;

import com.smartcampus.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ServiceRequestCreateDto {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    private String description;

    @NotNull(message = "Category is required (ELECTRICAL, NETWORK, CLEANING, PLUMBING, OTHER)")
    private Category category;

    private String location;

    @NotNull(message = "CreatedBy user ID is required")
    private Long createdById;

    public ServiceRequestCreateDto() {
    }

    public ServiceRequestCreateDto(String title, String description, Category category, String location, Long createdById) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.createdById = createdById;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }
}
