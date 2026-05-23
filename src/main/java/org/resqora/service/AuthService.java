package org.resqora.service;

import org.resqora.dto.request.LoginRequest;
import org.resqora.dto.request.RegisterMechanicRequest;
import org.resqora.dto.request.RegisterUserRequest;
import org.resqora.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse registerUser(RegisterUserRequest request);

    AuthResponse registerMechanic(RegisterMechanicRequest request);

    AuthResponse login(LoginRequest request);
}
