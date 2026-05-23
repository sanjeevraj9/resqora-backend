package org.resqora.service;

import org.resqora.dto.request.AvailabilityRequest;
import org.resqora.dto.response.MechanicProfileResponse;
import org.resqora.dto.response.ServiceRequestResponse;

import java.util.List;

public interface MechanicService {

    MechanicProfileResponse getProfile(String email);

    MechanicProfileResponse updateAvailability(
            AvailabilityRequest request, String email
    );

    List<ServiceRequestResponse> getAssignedRequests(String email);

    List<ServiceRequestResponse> getHistory(String email);
}
