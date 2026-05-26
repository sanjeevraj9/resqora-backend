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
import org.resqora.security.EmailService;
import org.resqora.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final MechanicProfileRepository mechanicProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Override
    public AuthResponse registerUser(RegisterUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone already registered");
        }

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .emailVerified(false)
                .verificationToken(verificationToken)
                .build();

        userRepository.save(user);

        try {
            emailService.sendVerificationEmail(
                    user.getEmail(),
                    verificationToken
            );
        } catch (Exception e) {
            System.out.println(
                    "Verification email failed: "
                            + e.getMessage()
            );
        }

        return AuthResponse.builder()
                .token(null)
                .email(user.getEmail())
                .role(user.getRole())
                .message("Registration successful! Please verify your email before logging in.")
                .build();
    }

    @Override
    public AuthResponse registerMechanic(RegisterMechanicRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone already registered");
        }

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.MECHANIC)
                .emailVerified(false)
                .verificationToken(verificationToken)
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

        try {
            emailService.sendVerificationEmail(
                    user.getEmail(),
                    verificationToken
            );
        } catch (Exception e) {
            System.out.println(
                    "Verification email failed: "
                            + e.getMessage()
            );
        }

        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .token(null)
                .email(user.getEmail())
                .role(user.getRole())
                .message("Registration successful! Please verify your email before logging in.")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        // Email verification check
        if (Boolean.FALSE.equals(user.getEmailVerified())) {
            throw new BadRequestException("Please verify your email first! Check your inbox.");
        }

        String token = jwtService.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_" + user.getRole().name())
                        .build()
        );

        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }

    @Override
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired verification link"));

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }
}