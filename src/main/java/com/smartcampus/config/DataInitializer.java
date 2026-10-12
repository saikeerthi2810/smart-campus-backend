package com.smartcampus.config;

import com.smartcampus.model.*;
import com.smartcampus.repository.FeedbackRepository;
import com.smartcampus.repository.ServiceRequestRepository;
import com.smartcampus.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ServiceRequestRepository requestRepository;
    private final FeedbackRepository feedbackRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           ServiceRequestRepository requestRepository,
                           FeedbackRepository feedbackRepository,
                           org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.feedbackRepository = feedbackRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Data already seeded
        }

        System.out.println(">>> Seeding Smart Campus demo users and sample service requests...");

        String defaultPasswordHash = passwordEncoder.encode("password123");

        // 1. Seed Student Profile: Keerthi
        User student = new User(
                null,
                "Keerthi",
                "keerthi@anurag.edu.in",
                defaultPasswordHash,
                Role.STUDENT,
                "CSE",
                "3rd Year",
                "Anurag University",
                "+91 9876543210"
        );
        student = userRepository.save(student);

        // 2. Seed Admin Profile
        User admin = new User(
                null,
                "Campus Administrator",
                "admin@anurag.edu.in",
                defaultPasswordHash,
                Role.ADMIN,
                "Estate & Facilities",
                "Staff",
                "Anurag University",
                "+91 9123456780"
        );
        admin = userRepository.save(admin);

        // 3. Seed Staff Profiles
        User staffNetwork = new User(
                null,
                "Suresh (Network Team)",
                "suresh.network@anurag.edu.in",
                defaultPasswordHash,
                Role.STAFF,
                "IT Infrastructure",
                "Staff",
                "Anurag University",
                "+91 9848012345"
        );
        staffNetwork = userRepository.save(staffNetwork);

        User staffElectrical = new User(
                null,
                "Ramesh (Electrical Team)",
                "ramesh.electrical@anurag.edu.in",
                defaultPasswordHash,
                Role.STAFF,
                "Electrical Maintenance",
                "Staff",
                "Anurag University",
                "+91 9848012346"
        );
        staffElectrical = userRepository.save(staffElectrical);

        User staffCleaning = new User(
                null,
                "Laxmi (Sanitation Team)",
                "laxmi.cleaning@anurag.edu.in",
                defaultPasswordHash,
                Role.STAFF,
                "Campus Sanitation",
                "Staff",
                "Anurag University",
                "+91 9848012347"
        );
        staffCleaning = userRepository.save(staffCleaning);

        // 4. Seed Sample Service Requests
        // Request 1: Network issue - In Progress
        ServiceRequest req1 = new ServiceRequest();
        req1.setTitle("Wi-Fi router in CSE Lab 3 frequently disconnecting");
        req1.setDescription("Students are unable to access lab servers during practical sessions. Wi-Fi AP-3 needs reset or cable inspection.");
        req1.setCategory(Category.NETWORK);
        req1.setStatus(RequestStatus.IN_PROGRESS);
        req1.setLocation("CSE Block - Lab 3, 2nd Floor");
        req1.setCreatedBy(student);
        req1.setAssignedTo(staffNetwork);
        requestRepository.save(req1);

        // Request 2: Electrical issue - Assigned
        ServiceRequest req2 = new ServiceRequest();
        req2.setTitle("Ceiling fan regulator sparking in Classroom 204");
        req2.setDescription("The speed regulator switch is loose and sparking when turned to position 3.");
        req2.setCategory(Category.ELECTRICAL);
        req2.setStatus(RequestStatus.ASSIGNED);
        req2.setLocation("Block B - Room 204");
        req2.setCreatedBy(student);
        req2.setAssignedTo(staffElectrical);
        requestRepository.save(req2);

        // Request 3: Plumbing issue - Submitted
        ServiceRequest req3 = new ServiceRequest();
        req3.setTitle("Water tap leaking in 1st Floor Restroom");
        req3.setDescription("Continuous water dripping from the washbasin tap causing water wastage.");
        req3.setCategory(Category.PLUMBING);
        req3.setStatus(RequestStatus.SUBMITTED);
        req3.setLocation("Main Academic Block - 1st Floor East Restroom");
        req3.setCreatedBy(student);
        requestRepository.save(req3);

        // Request 4: Cleaning issue - Resolved with Feedback
        ServiceRequest req4 = new ServiceRequest();
        req4.setTitle("Deep cleaning required in Seminar Hall 1");
        req4.setDescription("Post-conference cleanup needed for chairs and podium area.");
        req4.setCategory(Category.CLEANING);
        req4.setStatus(RequestStatus.RESOLVED);
        req4.setLocation("Auditorium Complex - Seminar Hall 1");
        req4.setCreatedBy(student);
        req4.setAssignedTo(staffCleaning);
        req4 = requestRepository.save(req4);

        Feedback feedback4 = new Feedback();
        feedback4.setServiceRequest(req4);
        feedback4.setRating(5);
        feedback4.setComments("Promptly cleaned and sanitized before the morning guest lecture. Thank you!");
        feedbackRepository.save(feedback4);

        req4.setFeedback(feedback4);
        requestRepository.save(req4);

        System.out.println(">>> Seeded 5 users and 4 service requests successfully!");
    }
}
