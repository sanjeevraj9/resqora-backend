package org.resqora.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.LoginRequest;
import org.resqora.dto.request.RegisterMechanicRequest;
import org.resqora.dto.response.AuthResponse;
import org.resqora.dto.request.RegisterUserRequest;
import org.resqora.entity.MechanicProfile;
import org.resqora.entity.User;
import org.resqora.enums.Role;
import org.resqora.repository.MechanicProfileRepository;
import org.resqora.repository.UserRepository;
import org.resqora.security.JwtService;
import org.resqora.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final MechanicProfileRepository
            mechanicProfileRepository;

    @PostMapping("/register/user")
    public ResponseEntity<AuthResponse> registerUser(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/register/mechanic")
    public ResponseEntity<AuthResponse> registerMechanic(
            @Valid @RequestBody RegisterMechanicRequest request
    ) {
        return ResponseEntity.ok(authService.registerMechanic(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify")
    public void verifyEmail(
            @RequestParam String token,
            HttpServletResponse response
    ) throws Exception {
        authService.verifyEmail(token);
        response.sendRedirect("https://resqora-9jtt.vercel.app/email-verified");

    }

    @PutMapping("/update-phone")
    public ResponseEntity<?> updatePhone(

            @RequestBody Map<String, String> body,

            @RequestHeader("Authorization")
            String authHeader

    ) {

        String token =
                authHeader.substring(7);

        String email =
                jwtService.extractUsername(token);

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow();

        user.setPhone(
                body.get("phone")
        );
        String name =
                body.get("name");

        if (
                name != null &&
                        !name.isBlank()
        ) {
            user.setName(name);
        }

        String role =
                body.get("role");

        if (role != null) {

            user.setRole(
                    Role.valueOf(role)
            );
        }

        userRepository.save(user);

        if (user.getRole() == Role.MECHANIC) {

            boolean profileExists =
                    mechanicProfileRepository
                            .findByUser(user)
                            .isPresent();

            if (!profileExists) {

                MechanicProfile profile =
                        new MechanicProfile();

                profile.setUser(user);

                profile.setLatitude(
                        BigDecimal.ZERO
                );

                profile.setLongitude(
                        BigDecimal.ZERO
                );

                profile.setAvailability(
                        false
                );

                profile.setExperienceYears(
                        0
                );

                profile.setRating(
                        BigDecimal.ZERO
                );

                String shopName =
                        body.get("shopName");
                String latitude =
                        body.get("latitude");

                String longitude =
                        body.get("longitude");

                profile.setShopName(
                        shopName != null &&
                                !shopName.isBlank()
                                ? shopName
                                : "Not Added"
                );
                if (
                        latitude != null &&
                                longitude != null
                ) {

                    profile.setLatitude(
                            new BigDecimal(latitude)
                    );

                    profile.setLongitude(
                            new BigDecimal(longitude)
                    );
                }

                profile.setSpecialization(
                        "General Repair"
                );

                mechanicProfileRepository
                        .save(profile);
            } else {

                MechanicProfile profile =
                        mechanicProfileRepository
                                .findByUser(user)
                                .orElseThrow();

                String shopName =
                        body.get("shopName");

                String latitude =
                        body.get("latitude");

                String longitude =
                        body.get("longitude");

                if (
                        shopName != null &&
                                !shopName.isBlank()
                ) {

                    profile.setShopName(
                            shopName
                    );
                }

                if (
                        latitude != null &&
                                longitude != null
                ) {

                    profile.setLatitude(
                            new BigDecimal(latitude)
                    );

                    profile.setLongitude(
                            new BigDecimal(longitude)
                    );
                }

                mechanicProfileRepository
                        .save(profile);
            }
        }

        String newToken =
                jwtService.generateToken(

                        org.springframework
                                .security
                                .core
                                .userdetails
                                .User

                                .withUsername(
                                        user.getEmail()
                                )

                                .password(
                                        user.getPassword()
                                )

                                .authorities(
                                        "ROLE_" +
                                                user.getRole().name()
                                )

                                .build()
                );

        return ResponseEntity.ok(
                Map.of(

                        "token",
                        newToken,

                        "role",
                        user.getRole().name()
                )
        );
    }
}