package org.resqora.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.resqora.enums.FuelType;
import org.resqora.enums.VehicleType;

@Data
public class VehicleRequest {

    @NotNull
    private VehicleType vehicleType;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotBlank
    private String registrationNumber;

    @NotNull
    private FuelType fuelType;
}