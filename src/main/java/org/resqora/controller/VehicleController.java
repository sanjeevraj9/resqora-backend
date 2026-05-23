package org.resqora.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.VehicleRequest;
import org.resqora.dto.response.VehicleResponse;
import org.resqora.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponse> addVehicle(
            @Valid @RequestBody VehicleRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                vehicleService.addVehicle(request, authentication.getName())
        );
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getMyVehicles(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                vehicleService.getMyVehicles(authentication.getName())
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                vehicleService.updateVehicle(id, request, authentication.getName())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(
            @PathVariable Long id,
            Authentication authentication
    ) {
        vehicleService.deleteVehicle(id, authentication.getName());
        return ResponseEntity.ok("Vehicle deleted successfully");
    }
}