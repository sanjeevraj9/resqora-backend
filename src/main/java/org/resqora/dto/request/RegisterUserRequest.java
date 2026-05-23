package org.resqora.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "Name is Required")
    private String name;

    @Email(message="Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "[0-9]{10}$",message = "phone must be 10 digits")
    private String phone;

    @Size(min=6,message = "Password must be at least 6 character")
    private String password;

}
