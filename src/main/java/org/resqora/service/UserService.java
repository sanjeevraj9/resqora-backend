package org.resqora.service;

import org.resqora.dto.request.UpdateMechanicProfileRequest;
import org.resqora.dto.request.UpdateUserProfileRequest;
import org.resqora.dto.response.MechanicProfileResponse;
import org.resqora.dto.response.UserProfileResponse;

public interface UserService {

    UserProfileResponse getProfile(String email);

    UserProfileResponse updateProfile(
            UpdateUserProfileRequest request,
            String email
    );
    MechanicProfileResponse getMechanicProfile(String email);

    MechanicProfileResponse updateMechanicProfile(String email, UpdateMechanicProfileRequest request);
}
