package org.resqora.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.LoginRequest;
import org.resqora.dto.request.RegisterMechanicRequest;
import org.resqora.dto.response.AuthResponse;
import org.resqora.dto.request.RegisterUserRequest;
import org.resqora.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register/user")
    public ResponseEntity<AuthResponse> registerUser(
            @Valid @RequestBody RegisterUserRequest request
    ){
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/register/mechanic")
    public ResponseEntity<AuthResponse> registerMechanic(
            @Valid @RequestBody RegisterMechanicRequest request
    ){
        return ResponseEntity.ok(authService.registerMechanic(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ){
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify")
    public void verifyEmail(
            @RequestParam String token,
            HttpServletResponse response
    )throws Exception {
        authService.verifyEmail(token);
        response.sendRedirect("https://resqora-9jtt.vercel.app/email-verified");

    }


}
