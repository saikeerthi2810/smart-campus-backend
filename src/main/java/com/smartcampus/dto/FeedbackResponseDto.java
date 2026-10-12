package com.smartcampus.dto;

import com.smartcampus.model.Feedback;
import java.time.LocalDateTime;

public class FeedbackResponseDto {
    private Long id;
    private int rating;
    private String comments;
    private LocalDateTime submittedAt;

    public FeedbackResponseDto() {
    }

    public FeedbackResponseDto(Feedback feedback) {
        if (feedback != null) {
            this.id = feedback.getId();
            this.rating = feedback.getRating();
            this.comments = feedback.getComments();
            this.submittedAt = feedback.getSubmittedAt();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
