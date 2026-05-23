package org.resqora.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.CreateServiceRequest;
import org.resqora.dto.request.UpdateStatusRequest;
import org.resqora.dto.response.LiveLocationNotification;
import org.resqora.dto.response.MechanicStatsResponse;
import org.resqora.dto.response.NearbyMechanicResponse;
import org.resqora.dto.response.ServiceRequestResponse;
import org.resqora.service.ServiceRequestService;
import org.resqora.service.impl.ServiceRequestServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    private final ServiceRequestServiceImpl serviceRequestServiceImpl;

    @PostMapping
    public ResponseEntity<ServiceRequestResponse> create(
            @Valid @RequestBody CreateServiceRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                serviceRequestService.createRequest(
                        request,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequestResponse> get(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                serviceRequestService.getRequest(
                        id,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<ServiceRequestResponse>> history(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                serviceRequestService.getHistory(
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ServiceRequestResponse> cancel(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                serviceRequestService.cancelRequest(
                        id,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{id}/nearby-mechanics")
    public ResponseEntity<List<NearbyMechanicResponse>> nearbyMechanics(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                serviceRequestService.findNearbyMechanics(
                        id,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<ServiceRequestResponse> accept(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                serviceRequestService.acceptRequest(
                        id,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ServiceRequestResponse> reject(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                serviceRequestService.rejectRequest(
                        id,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ServiceRequestResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                serviceRequestService.updateStatus(
                        id,
                        request,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{requestId}/location")
    public ResponseEntity<Void> updateLiveLocation(
            @PathVariable Long requestId,
            @RequestBody LiveLocationNotification location
    ) {
        serviceRequestServiceImpl.updateLiveLocation(
                requestId,
                location.getLatitude(),
                location.getLongitude()
        );

        return ResponseEntity.ok().build();
    }
    @GetMapping("/mechanic/active")
    public ResponseEntity<ServiceRequestResponse>
    getMechanicActive(Authentication authentication) {

        return ResponseEntity.ok(
                serviceRequestService.getMechanicActiveRequest(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/mechanic/stats")
    public ResponseEntity<MechanicStatsResponse>
    getMechanicStats(Authentication authentication) {

        return ResponseEntity.ok(
                serviceRequestService.getMechanicStats(
                        authentication.getName()
                )
        );
    }
    @PutMapping("/{id}/cash-collected")
    public ResponseEntity<ServiceRequestResponse> markCashCollected(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                serviceRequestService.markCashCollected(id)
        );
    }
}