package org.resqora.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.UpdateMechanicProfileRequest;
import org.resqora.dto.request.UpdateUserProfileRequest;
import org.resqora.dto.response.MechanicProfileResponse;
import org.resqora.dto.response.UserProfileResponse;
import org.resqora.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // USER PROFILE
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                userService.getProfile(
                        authentication.getName()
                )
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateUserProfileRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                userService.updateProfile(
                        request,
                        authentication.getName()
                )
        );
    }

    // MECHANIC PROFILE
    @GetMapping("/mechanic/profile")
    public ResponseEntity<MechanicProfileResponse>
    getMechanicProfile(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                userService.getMechanicProfile(
                        authentication.getName()
                )
        );
    }

    @PutMapping("/mechanic/profile")
    public ResponseEntity<MechanicProfileResponse>
    updateMechanicProfile(
            @RequestBody UpdateMechanicProfileRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                userService.updateMechanicProfile(
                        authentication.getName(),
                        request
                )
        );
    }
}