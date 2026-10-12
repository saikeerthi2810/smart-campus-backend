package com.smartcampus.service;

import com.smartcampus.dto.FeedbackResponseDto;
import com.smartcampus.dto.FeedbackSubmitDto;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.model.Feedback;
import com.smartcampus.model.RequestStatus;
import com.smartcampus.model.ServiceRequest;
import com.smartcampus.repository.FeedbackRepository;
import com.smartcampus.repository.ServiceRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ServiceRequestRepository requestRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, ServiceRequestRepository requestRepository) {
        this.feedbackRepository = feedbackRepository;
        this.requestRepository = requestRepository;
    }

    public FeedbackResponseDto submitFeedback(Long requestId, FeedbackSubmitDto dto) {
        return submitFeedback(requestId, dto, null);
    }

    public FeedbackResponseDto submitFeedback(Long requestId, FeedbackSubmitDto dto, Long authUserId) {
        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found with id: " + requestId));

        if (authUserId != null && !request.getCreatedBy().getId().equals(authUserId)) {
            throw new com.smartcampus.exception.UnauthorizedException("Only the student who submitted this request can provide feedback.");
        }

        if (request.getStatus() != RequestStatus.RESOLVED) {
            throw new IllegalArgumentException("Feedback can only be submitted for RESOLVED service requests.");
        }

        if (request.getFeedback() != null) {
            throw new IllegalArgumentException("Feedback has already been submitted for this service request.");
        }

        Feedback feedback = new Feedback();
        feedback.setServiceRequest(request);
        feedback.setRating(dto.getRating());
        feedback.setComments(dto.getComments());

        Feedback saved = feedbackRepository.save(feedback);
        request.setFeedback(saved);
        requestRepository.save(request);

        return new FeedbackResponseDto(saved);
    }

    public FeedbackResponseDto getFeedbackByRequestId(Long requestId) {
        Feedback feedback = feedbackRepository.findByServiceRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("No feedback found for request id: " + requestId));
        return new FeedbackResponseDto(feedback);
    }
}
