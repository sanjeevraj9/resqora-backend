package org.resqora.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AvailabilityRequest {

    @NotNull
    private Boolean availability;


}
