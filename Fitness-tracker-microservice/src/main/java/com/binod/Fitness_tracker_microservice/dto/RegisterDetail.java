package com.binod.Fitness_tracker_microservice.dto;

import com.binod.Fitness_tracker_microservice.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
public class RegisterDetail {

    @NotNull
    @Email(message = "Invalid email format")
    String email;
    @NotBlank(message = "First name is required")
    String password;
    private String keycloakId;
    String firstName;
    String lastName;
    UserRole userRole;
}
