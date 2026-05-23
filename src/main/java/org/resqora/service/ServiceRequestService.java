package org.resqora.service;

import org.resqora.dto.request.CreateServiceRequest;
import org.resqora.dto.request.UpdateStatusRequest;
import org.resqora.dto.response.MechanicStatsResponse;
import org.resqora.dto.response.NearbyMechanicResponse;
import org.resqora.dto.response.ServiceRequestResponse;

import java.util.List;

public interface ServiceRequestService {

    ServiceRequestResponse createRequest(CreateServiceRequest request,String email);

    ServiceRequestResponse getRequest(Long id, String email);

    List<ServiceRequestResponse> getHistory(String email);

    ServiceRequestResponse cancelRequest(Long id, String email);
    List<NearbyMechanicResponse> findNearbyMechanics(Long requestId, String email);

    ServiceRequestResponse acceptRequest(Long requestId,String email);

    ServiceRequestResponse rejectRequest(Long requestId,String email);

    ServiceRequestResponse updateStatus(
            Long requestId,
            UpdateStatusRequest request,
            String email
    );
    ServiceRequestResponse getMechanicActiveRequest(String email);

    MechanicStatsResponse getMechanicStats(String email);
    ServiceRequestResponse markCashCollected(Long requestId);
}
