package org.resqora.service.impl;

import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.LoginRequest;
import org.resqora.dto.request.RegisterMechanicRequest;
import org.resqora.dto.request.RegisterUserRequest;
import org.resqora.dto.response.AuthResponse;
import org.resqora.entity.MechanicProfile;
import org.resqora.entity.User;
import org.resqora.enums.Role;
import org.resqora.exception.BadRequestException;
import org.resqora.repository.MechanicProfileRepository;
import org.resqora.repository.UserRepository;
import org.resqora.security.JwtService;
import org.resqora.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final MechanicProfileRepository mechanicProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse registerUser(RegisterUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_USER")
                        .build()
        );

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .message("User registered successfully")
                .build();
    }

    @Override
    public AuthResponse registerMechanic(RegisterMechanicRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.MECHANIC)
                .build();

        userRepository.save(user);

        MechanicProfile mechanic = MechanicProfile.builder()
                .user(user)
                .shopName(request.getShopName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .rating(BigDecimal.ZERO)
                .experienceYears(0)
                .build();

        mechanicProfileRepository.save(mechanic);

        String token = jwtService.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_MECHANIC")
                        .build()
        );

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .message("Mechanic registered successfully")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_" + user.getRole().name())
                        .build()
        );

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }
}