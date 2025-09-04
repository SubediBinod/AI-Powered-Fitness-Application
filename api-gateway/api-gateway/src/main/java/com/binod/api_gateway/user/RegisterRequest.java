package com.binod.api_gateway.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotNull
    @Email(message = "Invalid email format")
    String email;
    @NotBlank(message = "First name is required")
    String password;
    private String keycloakId;
    String firstName;
    String lastName;
}
