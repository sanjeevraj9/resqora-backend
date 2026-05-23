package org.resqora.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.resqora.enums.Role;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String email;
    private Role role;
    private String message;
}
