package org.resqora.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.AvailabilityRequest;
import org.resqora.dto.response.MechanicProfileResponse;
import org.resqora.dto.response.ServiceRequestResponse;
import org.resqora.service.MechanicService;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mechanics")
@RequiredArgsConstructor
public class MechanicController {
    private final MechanicService mechanicService;

    @GetMapping("/profile")
    public ResponseEntity<MechanicProfileResponse> profile(
            Authentication authentication
    ){
        return ResponseEntity.ok(
                mechanicService.getProfile(authentication.getName())
        );
    }

    @PutMapping("/availability")
    public ResponseEntity<MechanicProfileResponse> availability(
            @Valid @RequestBody AvailabilityRequest request, Authentication authentication
            ){
        return ResponseEntity.ok(
                mechanicService.updateAvailability(
                        request,authentication.getName()
                )
        );
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ServiceRequestResponse>> requests(
            Authentication authentication
    ){
        return ResponseEntity.ok(
                mechanicService.getAssignedRequests(authentication.getName())
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<ServiceRequestResponse>> history(
            Authentication authentication
    ){
        return ResponseEntity.ok(
                mechanicService.getHistory(authentication.getName())
        );
    }
    @GetMapping("/assigned-requests")
    public ResponseEntity<List<ServiceRequestResponse>> getAssignedRequests(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                mechanicService.getAssignedRequests(
                        authentication.getName()
                )
        );
    }



}
