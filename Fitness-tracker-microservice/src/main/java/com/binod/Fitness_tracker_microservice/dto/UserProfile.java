package com.binod.Fitness_tracker_microservice.dto;

import com.binod.Fitness_tracker_microservice.model.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfile {
    private String id;
    private String keycloakId;
    String password;
    String email;
    String firstName;
    String lastName;
    UserRole userRole= UserRole.USER;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}
