package com.binod.api_gateway.user;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
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

