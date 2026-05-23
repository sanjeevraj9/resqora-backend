package org.resqora.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String emergencyContactName;
    private String emergencyContactPhone;
}
