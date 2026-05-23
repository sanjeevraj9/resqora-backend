package org.resqora.dto.request;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RegisterMechanicRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @Pattern(regexp = "^[0-9]{10}$")
    private String phone;
    @Size(min=6)
    private String password;
    @NotBlank
    private String shopName;
    @NotNull
    private BigDecimal latitude;
    @NotNull
    private BigDecimal longitude;

}
