package com.binod.Fitness_tracker_microservice.repository;

import com.binod.Fitness_tracker_microservice.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,String> {

    Boolean existsByKeycloakId(String id);

    boolean existsByEmail(@NotNull @Email(message = "Invalid email format") String email);

    User findByEmail(String email);
}
