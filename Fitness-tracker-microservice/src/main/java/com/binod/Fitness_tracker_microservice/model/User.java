package com.binod.Fitness_tracker_microservice.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="Users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(
            unique = true,
            nullable = false
    )
    String email;
    private String keycloakId;
    @Column(nullable = false)
    String password;
    @Column(
            nullable = false
    )

    String firstName;
    String lastName;
    @Enumerated(EnumType.STRING)
    UserRole userRole= UserRole.USER;
    @CreationTimestamp
    LocalDateTime createdAt;
    @UpdateTimestamp
    LocalDateTime updatedAt;

}
