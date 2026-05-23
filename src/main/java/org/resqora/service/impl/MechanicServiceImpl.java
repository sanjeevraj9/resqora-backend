package org.resqora.service.impl;

import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.AvailabilityRequest;
import org.resqora.dto.response.MechanicProfileResponse;
import org.resqora.dto.response.ServiceRequestResponse;
import org.resqora.entity.MechanicProfile;
import org.resqora.entity.ServiceRequest;
import org.resqora.entity.User;
import org.resqora.enums.RequestStatus;
import org.resqora.enums.Role;
import org.resqora.exception.ResourceNotFoundException;
import org.resqora.repository.MechanicProfileRepository;
import org.resqora.repository.ServiceRequestRepository;
import org.resqora.repository.UserRepository;
import org.resqora.service.MechanicService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MechanicServiceImpl implements MechanicService {

    private final UserRepository userRepository;
    private final MechanicProfileRepository mechanicProfileRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    @Override
    public MechanicProfileResponse getProfile(String email) {

        User mechanic = getMechanic(email);

        MechanicProfile profile =
                mechanicProfileRepository.findByUser(mechanic)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mechanic profile not found"
                                ));

        return mapProfile(mechanic, profile);
    }

    @Override
    public MechanicProfileResponse updateAvailability(
            AvailabilityRequest request,
            String email
    ) {
        User mechanic = getMechanic(email);

        MechanicProfile profile =
                mechanicProfileRepository.findByUser(mechanic)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mechanic profile not found"
                                ));

        profile.setAvailability(
                request.getAvailability()
        );

        mechanicProfileRepository.save(profile);

        return mapProfile(mechanic, profile);
    }

    @Override
    public List<ServiceRequestResponse> getAssignedRequests(
            String email
    ) {
        User mechanic = getMechanic(email);

        List<ServiceRequest> requests =
                serviceRequestRepository.findByMechanic(mechanic);

        return requests.stream()
                .map(this::mapRequest)
                .toList();
    }

    @Override
    public List<ServiceRequestResponse> getHistory(
            String email
    ) {
        User mechanic = getMechanic(email);

        return serviceRequestRepository
                .findByMechanicOrderByCreatedAtDesc(mechanic)
                .stream()
                .map(this::mapRequest)
                .toList();
    }

    private User getMechanic(String email) {

        User mechanic = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Mechanic not found"
                        ));

        if (mechanic.getRole() != Role.MECHANIC) {
            throw new RuntimeException("Access denied");
        }

        return mechanic;
    }

    private MechanicProfileResponse mapProfile(
            User user,
            MechanicProfile profile
    ) {
        List<ServiceRequest> jobs =
                serviceRequestRepository
                        .findByMechanicOrderByCreatedAtDesc(user);

        int completedJobs = (int) jobs.stream()
                .filter(job ->
                        job.getStatus() == RequestStatus.COMPLETED
                )
                .count();

        double totalEarnings = jobs.stream()
                .filter(job ->
                        job.getStatus() == RequestStatus.COMPLETED
                )
                .mapToDouble(job ->
                        job.getEstimatedPrice() != null
                                ? job.getEstimatedPrice().doubleValue()
                                : 0
                )
                .sum();

        return MechanicProfileResponse.builder()
                .mechanicId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .city(user.getCity())
                .shopName(profile.getShopName())
                .specialization(profile.getSpecialization())
                .experienceYears(
                        profile.getExperienceYears()
                )
                .rating(
                        profile.getRating() != null
                                ? profile.getRating().doubleValue()
                                : 5.0
                )
                .availability(
                        profile.getAvailability()
                )
                .completedJobs(completedJobs)
                .totalEarnings(totalEarnings)
                .build();
    }

    private ServiceRequestResponse mapRequest(
            ServiceRequest request
    ) {
        return ServiceRequestResponse.builder()
                .id(request.getId())
                .vehicleId(
                        request.getVehicle() != null
                                ? request.getVehicle().getId()
                                : null
                )
                .issueType(request.getIssueType())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(request.getStatus())
                .estimatedPrice(request.getEstimatedPrice())
                .createdAt(request.getCreatedAt())
                .customerName(
                        request.getUser() != null
                                ? request.getUser().getName()
                                : null
                )
                .vehicleBrand(
                        request.getVehicle() != null
                                ? request.getVehicle().getBrand()
                                : null
                )
                .vehicleModel(
                        request.getVehicle() != null
                                ? request.getVehicle().getModel()
                                : null
                )
                .build();
    }
}