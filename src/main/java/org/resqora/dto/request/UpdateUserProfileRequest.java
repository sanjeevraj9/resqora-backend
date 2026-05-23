package org.resqora.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    private String emergencyContactName;
    private String emergencyContactPhone;
}
