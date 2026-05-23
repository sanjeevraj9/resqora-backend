package org.resqora.service.impl;

import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.CreateServiceRequest;
import org.resqora.dto.request.UpdateStatusRequest;
import org.resqora.dto.response.*;
import org.resqora.entity.MechanicProfile;
import org.resqora.entity.ServiceRequest;
import org.resqora.entity.User;
import org.resqora.entity.Vehicle;
import org.resqora.enums.PaymentStatus;
import org.resqora.enums.RequestStatus;
import org.resqora.enums.Role;
import org.resqora.exception.BadRequestException;
import org.resqora.exception.ResourceNotFoundException;
import org.resqora.exception.UnauthorizedException;
import org.resqora.repository.MechanicProfileRepository;
import org.resqora.repository.ServiceRequestRepository;
import org.resqora.repository.UserRepository;
import org.resqora.repository.VehicleRepository;
import org.resqora.service.NotificationService;
import org.resqora.service.ServiceRequestService;
import org.resqora.util.GeoUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final MechanicProfileRepository mechanicProfileRepository;
    private final NotificationService notificationService;

    @Override
    public ServiceRequestResponse createRequest(
            CreateServiceRequest request,
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Vehicle vehicle = vehicleRepository.findByIdAndUser(
                request.getVehicleId(),
                user
        ).orElseThrow(() ->
                new BadRequestException("Vehicle not found"));

        ServiceRequest serviceRequest = ServiceRequest.builder()
                .user(user)
                .vehicle(vehicle)
                .issueType(request.getIssueType())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .estimatedPrice(request.getEstimatedPrice())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentStatus())
                .status(RequestStatus.REQUESTED)
                .build();

        serviceRequest = serviceRequestRepository.save(serviceRequest);

        List<MechanicProfile> mechanics =
                mechanicProfileRepository.findByAvailabilityTrue();

        for (MechanicProfile mechanic : mechanics) {

            double distance = GeoUtil.calculateDistance(
                    serviceRequest.getLatitude().doubleValue(),
                    serviceRequest.getLongitude().doubleValue(),
                    mechanic.getLatitude().doubleValue(),
                    mechanic.getLongitude().doubleValue()
            );

            if (distance > 10.0) {
                continue;
            }

            RequestNotificationResponse notification =
                    RequestNotificationResponse.builder()
                            .requestId(serviceRequest.getId())
                            .issueType(serviceRequest.getIssueType().name())
                            .description(serviceRequest.getDescription())
                            .latitude(serviceRequest.getLatitude().doubleValue())
                            .longitude(serviceRequest.getLongitude().doubleValue())
                            .estimatedPrice(
                                    serviceRequest.getEstimatedPrice() != null
                                            ? serviceRequest.getEstimatedPrice().doubleValue()
                                            : 0.0
                            )
                            .customerName(user.getName())
                            .customerPhone(user.getPhone())
                            .build();

            notificationService.notifyMechanic(
                    mechanic.getUser(),
                    notification
            );
        }

        return map(serviceRequest);
    }

    @Override
    public ServiceRequestResponse getRequest(
            Long id,
            String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        ServiceRequest request =
                serviceRequestRepository.findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Request not found"));

        return map(request);
    }

    @Override
    public List<ServiceRequestResponse> getHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return serviceRequestRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public ServiceRequestResponse cancelRequest(
            Long id,
            String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        ServiceRequest request =
                serviceRequestRepository.findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Request not found"));

        if (request.getStatus() != RequestStatus.REQUESTED &&
                request.getStatus() != RequestStatus.ACCEPTED) {
            throw new UnauthorizedException("Cannot cancel this request");
        }

        request.setStatus(RequestStatus.CANCELLED);

        serviceRequestRepository.save(request);

        return map(request);
    }

    @Override
    public List<NearbyMechanicResponse> findNearbyMechanics(
            Long requestId,
            String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        ServiceRequest request =
                serviceRequestRepository.findByIdAndUser(requestId, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Request not found"));

        List<MechanicProfile> mechanics =
                mechanicProfileRepository.findByAvailabilityTrue();

        List<NearbyMechanicResponse> nearby = new ArrayList<>();

        for (MechanicProfile mechanic : mechanics) {

            double distance = GeoUtil.calculateDistance(
                    request.getLatitude().doubleValue(),
                    request.getLongitude().doubleValue(),
                    mechanic.getLatitude().doubleValue(),
                    mechanic.getLongitude().doubleValue()
            );

            if (distance <= 10.0) {
                nearby.add(
                        NearbyMechanicResponse.builder()
                                .mechanicId(mechanic.getUser().getId())
                                .shopName(mechanic.getShopName())
                                .rating(
                                        mechanic.getRating() != null
                                                ? mechanic.getRating().doubleValue()
                                                : 0.0
                                )
                                .distanceKm(distance)
                                .latitude(mechanic.getLatitude().doubleValue())
                                .longitude(mechanic.getLongitude().doubleValue())
                                .build()
                );
            }
        }

        nearby.sort(
                Comparator.comparing(
                        NearbyMechanicResponse::getDistanceKm
                )
        );

        return nearby;
    }

    @Override
    public ServiceRequestResponse acceptRequest(
            Long requestId,
            String email
    ) {

        User mechanic = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mechanic not found"));

        if (mechanic.getRole() != Role.MECHANIC) {
            throw new UnauthorizedException(
                    "Only mechanics can accept requests"
            );
        }

        ServiceRequest request =
                serviceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Request not found"));

        if (request.getStatus() != RequestStatus.REQUESTED) {
            throw new BadRequestException("Request already processed");
        }

        request.setMechanic(mechanic);
        request.setStatus(RequestStatus.ACCEPTED);

        serviceRequestRepository.save(request);

        AcceptRequestNotification notification =
                AcceptRequestNotification.builder()
                        .requestId(request.getId())
                        .mechanicName(mechanic.getName())
                        .message("Mechanic accepted your request")
                        .build();

        RequestClosedNotification closedNotification =
                RequestClosedNotification.builder()
                        .requestId(request.getId())
                        .message("Request accepted by another mechanic")
                        .build();



        notificationService.notifyUser(
                request.getUser(),
                notification
        );

        notificationService.notifyRequestClosed(
                closedNotification
        );

        return map(request);
    }

    @Override
    public ServiceRequestResponse rejectRequest(
            Long requestId,
            String email
    ) {

        User mechanic = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mechanic not found"));

        ServiceRequest request =
                serviceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Request not found"));

        if (request.getStatus() != RequestStatus.REQUESTED) {
            throw new BadRequestException("Request already processed");
        }

        request.getRejectedMechanics().add(mechanic);

        serviceRequestRepository.save(request);

        return map(request);
    }

    @Override
    public ServiceRequestResponse updateStatus(
            Long requestId,
            UpdateStatusRequest updateRequest,
            String email
    ) {

        userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mechanic not found"));

        ServiceRequest request =
                serviceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Request not found"));

        if (request.getMechanic() == null) {
            throw new BadRequestException("No mechanic assigned");
        }

        RequestStatus newStatus = updateRequest.getStatus();

        if (newStatus == RequestStatus.REQUESTED ||
                newStatus == RequestStatus.CANCELLED) {
            throw new BadRequestException("Invalid status update");
        }

        request.setStatus(newStatus);

        serviceRequestRepository.save(request);

        RequestStatusNotification notification =
                RequestStatusNotification.builder()
                        .requestId(request.getId())
                        .status(request.getStatus().name())
                        .message("Request status updated")
                        .build();

        notificationService.notifyStatusUpdate(
                request.getUser(),
                notification
        );

        return map(request);
    }

    public void updateLiveLocation(
            Long requestId,
            Double latitude,
            Double longitude
    ) {
        ServiceRequest request =
                serviceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Request not found"));

        LiveLocationNotification notification =
                LiveLocationNotification.builder()
                        .requestId(requestId)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build();

        notificationService.notifyLiveLocation(
                request.getUser(),
                notification
        );
    }

    @Override
    public ServiceRequestResponse getMechanicActiveRequest(
            String email
    ) {
        User mechanic = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mechanic not found"));

        List<RequestStatus> activeStatuses = List.of(
                RequestStatus.ACCEPTED,
                RequestStatus.ON_THE_WAY,
                RequestStatus.ARRIVED,
                RequestStatus.IN_PROGRESS
        );

        List<ServiceRequest> requests =
                serviceRequestRepository
                        .findByMechanicAndStatusIn(
                                mechanic,
                                activeStatuses
                        );
        System.out.println("AUTH EMAIL: " + email);
        System.out.println("REQUEST COUNT: " + requests.size());

        if (requests.isEmpty()) {
            return null;
        }

        return map(requests.get(0));
    }
    @Override
    public MechanicStatsResponse getMechanicStats(
            String email
    ) {
        User mechanic = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mechanic not found"));

        List<ServiceRequest> requests =
                serviceRequestRepository.findByMechanic(mechanic);

        int completed = 0;
        BigDecimal earnings = BigDecimal.ZERO;

        for (ServiceRequest request : requests) {
            if (request.getStatus() == RequestStatus.COMPLETED) {
                completed++;

                if (request.getEstimatedPrice() != null) {
                    earnings =
                            earnings.add(request.getEstimatedPrice());
                }
            }
        }

        return MechanicStatsResponse.builder()
                .completedJobs(completed)
                .totalEarnings(earnings)
                .build();
    }

    private ServiceRequestResponse map(ServiceRequest request) {
        return ServiceRequestResponse.builder()
                .id(request.getId())
                .vehicleId(request.getVehicle().getId())
                .issueType(request.getIssueType())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(request.getStatus())
                .estimatedPrice(request.getEstimatedPrice())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentStatus())
                .createdAt(request.getCreatedAt())

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

                .mechanicName(
                        request.getMechanic() != null
                                ? request.getMechanic().getName()
                                : null
                )
                .customerName(
                        request.getUser()!=null
                                ? request.getUser().getName():null
                )
                .build();
    }
    @Override
    public ServiceRequestResponse markCashCollected(
            Long requestId
    ) {
        ServiceRequest request =
                serviceRequestRepository
                        .findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Request not found"
                                ));

        request.setPaymentStatus(
                PaymentStatus.PAID
        );

        serviceRequestRepository.save(request);

        return map(request);
    }
}