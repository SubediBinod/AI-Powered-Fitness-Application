package com.binod.Fitness_tracker_microservice.controller;

import com.binod.Fitness_tracker_microservice.dto.RegisterDetail;
import com.binod.Fitness_tracker_microservice.dto.UserProfile;
import com.binod.Fitness_tracker_microservice.model.User;
import com.binod.Fitness_tracker_microservice.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("api/fitness-app")
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("/{id}")
    public ResponseEntity<UserProfile>getUserDetails(@PathVariable String id) //@PathVariable matches the URL template:
    {
      UserProfile userProfile=  userService.getDetails(id);
        return ResponseEntity.ok(userProfile);
    }
    @PostMapping("/register")
    public ResponseEntity<?>registerUser(@Valid @RequestBody RegisterDetail registerDetail){
        UserProfile newUser=userService.createUser(registerDetail);
        return ResponseEntity.status(201).body(newUser);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id){

        userService.deleteUser(id);
        return ResponseEntity.ok("Successfully deleted");
    }
    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.validateByUserId(userId));
    }
    @GetMapping("/keycloak/{keycloakId}/validate")
    public ResponseEntity<Boolean> validateUserByKeycloak(@PathVariable String keycloakId) {
        return ResponseEntity.ok(userService.existsByKeycloakId(keycloakId));
    }


}
