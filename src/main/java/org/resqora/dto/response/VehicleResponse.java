package org.resqora.dto.response;

import lombok.Builder;
import lombok.Data;
import org.resqora.enums.FuelType;
import org.resqora.enums.VehicleType;

@Data
@Builder
public class VehicleResponse {

    private Long id;
    private VehicleType vehicleType;
    private String brand;
    private String model;
    private String registrationNumber;
    private FuelType fuelType;
}