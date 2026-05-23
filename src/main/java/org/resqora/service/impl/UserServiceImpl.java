package org.resqora.service.impl;

import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.UpdateMechanicProfileRequest;
import org.resqora.dto.request.UpdateUserProfileRequest;
import org.resqora.dto.response.MechanicProfileResponse;
import org.resqora.dto.response.UserProfileResponse;
import org.resqora.entity.User;
import org.resqora.enums.RequestStatus;
import org.resqora.exception.ResourceNotFoundException;
import org.resqora.repository.ServiceRequestRepository;
import org.resqora.repository.UserRepository;
import org.resqora.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    @Override
    public UserProfileResponse getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return map(user);
    }

    @Override
    public UserProfileResponse updateProfile(
            UpdateUserProfileRequest request,
            String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        user.setName(request.getName());
        user.setPhone(request.getPhone());

        user.setEmergencyContactName(
                request.getEmergencyContactName()
        );

        user.setEmergencyContactPhone(
                request.getEmergencyContactPhone()
        );

        userRepository.save(user);

        return map(user);
    }

    @Override
    public MechanicProfileResponse getMechanicProfile(
            String email
    ) {
        User mechanic = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mechanic not found"));

        int completedJobs =
                serviceRequestRepository
                        .findByMechanicAndStatusNotOrderByCreatedAtDesc(
                                mechanic,
                                RequestStatus.CANCELLED
                        )
                        .size();

        double totalEarnings =
                serviceRequestRepository
                        .findByMechanicAndStatusNotOrderByCreatedAtDesc(
                                mechanic,
                                RequestStatus.CANCELLED
                        )
                        .stream()
                        .mapToDouble(req ->
                                req.getEstimatedPrice() != null
                                        ? req.getEstimatedPrice().doubleValue()
                                        : 0
                        )
                        .sum();

        return MechanicProfileResponse.builder()
                .name(mechanic.getName())
                .email(mechanic.getEmail())
                .phone(mechanic.getPhone())
                .city(mechanic.getCity())
                .specialization(mechanic.getSpecialization())
                .experienceYears(mechanic.getExperienceYears())
                .rating(mechanic.getRating())
                .availability(mechanic.getIsActive())
                .completedJobs(completedJobs)
                .totalEarnings(totalEarnings)
                .build();
    }

    @Override
    public MechanicProfileResponse updateMechanicProfile(
            String email,
            UpdateMechanicProfileRequest request
    ) {
        User mechanic = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mechanic not found"));

        mechanic.setName(request.getName());
        mechanic.setPhone(request.getPhone());
        mechanic.setCity(request.getCity());
        mechanic.setSpecialization(
                request.getSpecialization()
        );
        mechanic.setExperienceYears(
                request.getExperienceYears()
        );

        userRepository.save(mechanic);

        return getMechanicProfile(email);
    }

    private UserProfileResponse map(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .emergencyContactName(
                        user.getEmergencyContactName()
                )
                .emergencyContactPhone(
                        user.getEmergencyContactPhone()
                )
                .build();
    }
}