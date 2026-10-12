package com.smartcampus.service;

import com.smartcampus.dto.ServiceRequestCreateDto;
import com.smartcampus.dto.ServiceRequestResponseDto;
import com.smartcampus.dto.ServiceRequestUpdateDto;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.exception.UnauthorizedException;
import com.smartcampus.model.*;
import com.smartcampus.repository.ServiceRequestRepository;
import com.smartcampus.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceRequestService {

    private final ServiceRequestRepository requestRepository;
    private final UserRepository userRepository;

    public ServiceRequestService(ServiceRequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public List<ServiceRequestResponseDto> getAllRequests(Category category, RequestStatus status, Long createdById, Long assignedToId) {
        List<ServiceRequest> list;

        if (createdById != null) {
            list = requestRepository.findByCreatedByIdOrderByCreatedAtDesc(createdById);
        } else if (assignedToId != null) {
            list = requestRepository.findByAssignedToIdOrderByCreatedAtDesc(assignedToId);
        } else if (status != null) {
            list = requestRepository.findByStatusOrderByCreatedAtDesc(status);
        } else if (category != null) {
            list = requestRepository.findByCategoryOrderByCreatedAtDesc(category);
        } else {
            list = requestRepository.findAllByOrderByCreatedAtDesc();
        }

        return list.stream()
                .map(ServiceRequestResponseDto::new)
                .collect(Collectors.toList());
    }

    public ServiceRequestResponseDto getRequestById(Long id) {
        ServiceRequest request = findEntityById(id);
        return new ServiceRequestResponseDto(request);
    }

    public ServiceRequestResponseDto createRequest(ServiceRequestCreateDto dto, Long authUserId) {
        Long creatorId = (authUserId != null) ? authUserId : dto.getCreatedById();
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator user not found with id: " + creatorId));

        ServiceRequest request = new ServiceRequest();
        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setCategory(dto.getCategory());
        request.setStatus(RequestStatus.SUBMITTED);
        request.setLocation(dto.getLocation());
        request.setCreatedBy(creator);

        ServiceRequest saved = requestRepository.save(request);
        return new ServiceRequestResponseDto(saved);
    }

    public ServiceRequestResponseDto createRequest(ServiceRequestCreateDto dto) {
        return createRequest(dto, null);
    }

    public ServiceRequestResponseDto updateRequest(Long id, ServiceRequestUpdateDto dto, Long authUserId) {
        ServiceRequest request = findEntityById(id);

        if (authUserId != null) {
            User caller = userRepository.findById(authUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found: " + authUserId));

            // Check staff assignment authorization
            if (dto.getAssignedToId() != null && caller.getRole() != Role.ADMIN) {
                throw new UnauthorizedException("Only campus administrators are authorized to assign requests to staff.");
            }

            // Check status transition authorization
            if (dto.getStatus() != null) {
                if (caller.getRole() == Role.STAFF) {
                    if (request.getAssignedTo() == null || !request.getAssignedTo().getId().equals(caller.getId())) {
                        throw new UnauthorizedException("Staff members can only update status for requests assigned to them.");
                    }
                    if (dto.getStatus() != RequestStatus.IN_PROGRESS && dto.getStatus() != RequestStatus.RESOLVED) {
                        throw new IllegalArgumentException("Staff can only update request status to IN_PROGRESS or RESOLVED.");
                    }
                } else if (caller.getRole() == Role.STUDENT) {
                    if (!request.getCreatedBy().getId().equals(caller.getId())) {
                        throw new UnauthorizedException("Students can only modify their own requests.");
                    }
                    if (dto.getStatus() != RequestStatus.CANCELLED) {
                        throw new UnauthorizedException("Students can only cancel their own requests.");
                    }
                }
            }
        }

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            request.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            request.setDescription(dto.getDescription());
        }
        if (dto.getCategory() != null) {
            request.setCategory(dto.getCategory());
        }
        if (dto.getLocation() != null) {
            request.setLocation(dto.getLocation());
        }
        if (dto.getStatus() != null) {
            request.setStatus(dto.getStatus());
        }

        if (dto.getAssignedToId() != null) {
            User staff = userRepository.findById(dto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned staff user not found with id: " + dto.getAssignedToId()));

            if (staff.getRole() != Role.STAFF && staff.getRole() != Role.ADMIN) {
                throw new IllegalArgumentException("Requests can only be assigned to STAFF or ADMIN users");
            }
            request.setAssignedTo(staff);
            if (request.getStatus() == RequestStatus.SUBMITTED) {
                request.setStatus(RequestStatus.ASSIGNED);
            }
        }

        ServiceRequest updated = requestRepository.save(request);
        return new ServiceRequestResponseDto(updated);
    }

    public ServiceRequestResponseDto updateRequest(Long id, ServiceRequestUpdateDto dto) {
        return updateRequest(id, dto, null);
    }

    public void deleteRequest(Long id, Long authUserId) {
        ServiceRequest request = findEntityById(id);
        if (authUserId != null) {
            User caller = userRepository.findById(authUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found: " + authUserId));
            if (caller.getRole() != Role.ADMIN && (!request.getCreatedBy().getId().equals(caller.getId()) || request.getStatus() != RequestStatus.SUBMITTED)) {
                throw new UnauthorizedException("Only administrators or the student who created this request (while SUBMITTED) can delete it.");
            }
        }
        requestRepository.delete(request);
    }

    public void deleteRequest(Long id) {
        deleteRequest(id, null);
    }

    public ServiceRequest findEntityById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found with id: " + id));
    }
}
