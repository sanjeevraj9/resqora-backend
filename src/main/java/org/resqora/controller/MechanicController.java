package org.resqora.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.AvailabilityRequest;
import org.resqora.dto.request.UpdateMechanicProfileRequest;
import org.resqora.dto.response.MechanicProfileResponse;
import org.resqora.dto.response.ServiceRequestResponse;
import org.resqora.service.CloudinaryService;
import org.resqora.service.MechanicService;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mechanics")
@RequiredArgsConstructor
public class MechanicController {
    private final MechanicService mechanicService;
    private final CloudinaryService cloudinaryService;

    @GetMapping("/profile")
    public ResponseEntity<MechanicProfileResponse> profile(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                mechanicService.getProfile(authentication.getName())
        );
    }

    @PutMapping("/availability")
    public ResponseEntity<MechanicProfileResponse> availability(
            @Valid @RequestBody AvailabilityRequest request, Authentication authentication
    ) {
        return ResponseEntity.ok(
                mechanicService.updateAvailability(
                        request, authentication.getName()
                )
        );
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ServiceRequestResponse>> requests(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                mechanicService.getAssignedRequests(authentication.getName())
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<ServiceRequestResponse>> history(
            Authentication authentication
    ) {
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

    @PostMapping("/upload-profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(

            @RequestParam("file")
            MultipartFile file,

            Authentication authentication

    ) throws Exception {

        String imageUrl =
                cloudinaryService
                        .uploadProfilePhoto(
                                file,
                                authentication.getName()
                        );

        return ResponseEntity.ok(
                Map.of(
                        "imageUrl",
                        imageUrl
                )
        );
    }

    @PostMapping("/upload-shop-photo")
    public ResponseEntity<?> uploadShopPhoto(

            @RequestParam("file")
            MultipartFile file,

            Authentication authentication

    ) throws Exception {

        String imageUrl =
                cloudinaryService
                        .uploadShopPhoto(
                                file,
                                authentication.getName()
                        );

        return ResponseEntity.ok(
                Map.of(
                        "imageUrl",
                        imageUrl
                )
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<MechanicProfileResponse>
    updateProfile(

            @RequestBody
            UpdateMechanicProfileRequest request,

            Authentication authentication

    ) {

        return ResponseEntity.ok(

                mechanicService.updateProfile(

                        request,

                        authentication.getName()
                )
        );
    }


}
