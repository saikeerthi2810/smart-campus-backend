package com.smartcampus.repository;

import com.smartcampus.model.Category;
import com.smartcampus.model.RequestStatus;
import com.smartcampus.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findAllByOrderByCreatedAtDesc();

    List<ServiceRequest> findByCreatedByIdOrderByCreatedAtDesc(Long createdById);

    List<ServiceRequest> findByAssignedToIdOrderByCreatedAtDesc(Long assignedToId);

    List<ServiceRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    List<ServiceRequest> findByCategoryOrderByCreatedAtDesc(Category category);
}
