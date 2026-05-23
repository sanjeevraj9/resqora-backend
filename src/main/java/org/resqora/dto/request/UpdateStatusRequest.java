package org.resqora.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.resqora.enums.RequestStatus;

@Data
public class UpdateStatusRequest {
    @NotNull
    private RequestStatus status;
}
