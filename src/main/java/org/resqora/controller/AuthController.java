package org.resqora.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.LoginRequest;
import org.resqora.dto.request.RegisterMechanicRequest;
import org.resqora.dto.response.AuthResponse;
import org.resqora.dto.request.RegisterUserRequest;
import org.resqora.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
