package org.resqora.service;

import org.resqora.dto.request.VehicleRequest;
import org.resqora.dto.response.VehicleResponse;

import java.util.List;

public interface VehicleService {

    VehicleResponse addVehicle(VehicleRequest request, String email);

    List<VehicleResponse> getMyVehicles(String email);

    VehicleResponse updateVehicle(Long id, VehicleRequest request, String email);

    void deleteVehicle(Long id, String email);
}