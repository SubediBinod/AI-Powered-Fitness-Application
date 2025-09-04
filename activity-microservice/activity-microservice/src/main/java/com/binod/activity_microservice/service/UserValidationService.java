package com.binod.activity_microservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class UserValidationService {
    @Autowired
    private  WebClient userServiceWebClient;

    // Flow of validateUser():
    // 1. Send GET request → "api/fitness-app/{id}/validate" with user id
    // 2. Convert response → Boolean
    // 3. If Boolean.TRUE → return true
    // 4. If 404 → throw "User not found"
    // 5. If 400 → throw "Invalid Request"
    // 6. If other HTTP error → throw "Unexpected error"
    // 7. If service unreachable → throw "User Service not reachable"

    public boolean validateUser(String id) {
        try {
            return Boolean.TRUE.equals(
                    userServiceWebClient.get()
                            .uri("api/fitness-app/{id}/validate", id)
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .block()
            );
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new RuntimeException("User not found: " + id);
            } else if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new RuntimeException("Invalid Request: " + id);
            } else {
                throw new RuntimeException("Unexpected error from User Service: " + e.getMessage(), e);
            }
        } catch (WebClientRequestException e) {
            throw new RuntimeException("User Service not reachable: " + e.getMessage(), e);
        }
    }
}
