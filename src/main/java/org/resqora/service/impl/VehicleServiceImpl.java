package org.resqora.service.impl;

import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.VehicleRequest;
import org.resqora.dto.response.VehicleResponse;
import org.resqora.entity.User;
import org.resqora.entity.Vehicle;
import org.resqora.exception.ResourceNotFoundException;
import org.resqora.repository.UserRepository;
import org.resqora.repository.VehicleRepository;
import org.resqora.service.VehicleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Override
    public VehicleResponse addVehicle(VehicleRequest request, String email) {

        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new ResourceNotFoundException("Vehicle already exists");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Vehicle vehicle = Vehicle.builder()
                .user(user)
                .vehicleType(request.getVehicleType())
                .brand(request.getBrand())
                .model(request.getModel())
                .registrationNumber(request.getRegistrationNumber())
                .fuelType(request.getFuelType())
                .build();

        vehicleRepository.save(vehicle);

        return map(vehicle);
    }

    @Override
    public List<VehicleResponse> getMyVehicles(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return vehicleRepository.findByUser(user)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public VehicleResponse updateVehicle(Long id, VehicleRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Vehicle vehicle = vehicleRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setFuelType(request.getFuelType());

        vehicleRepository.save(vehicle);

        return map(vehicle);
    }

    @Override
    public void deleteVehicle(Long id, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Vehicle vehicle = vehicleRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        vehicleRepository.delete(vehicle);
    }

    private VehicleResponse map(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .vehicleType(vehicle.getVehicleType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .registrationNumber(vehicle.getRegistrationNumber())
                .fuelType(vehicle.getFuelType())
                .build();
    }
}