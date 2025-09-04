package com.binod.api_gateway.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserService {

    private final WebClient userServiceWebClient;

    /**
     * Flow for validateUser():
     * 1. Call User Service GET -> api/fitness-app/{id}/validate
     * 2. Retrieve response body as Boolean
     * 3. If success → return Mono<Boolean>
     * 4. If error:
     *      - 404 → "User not found"
     *      - 400 → "Invalid Request"
     *      - otherwise → "Unexpected error"
     */
    public Mono<Boolean> validateUser(String id) {
        return userServiceWebClient.get()
                .uri("api/fitness-app/keycloak/{id}/validate", id) // 1. Call API with user id
                .retrieve()
                .bodyToMono(Boolean.class)                // 2. Convert body to Boolean
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new RuntimeException("User not found: " + id)); // 4a
                    } else if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                        return Mono.error(new RuntimeException("Invalid Request: " + id)); // 4b
                    }
                    return Mono.error(new RuntimeException("Unexpected error from User Service: " + e.getMessage(), e)); // 4c
                });
    }

    /**
     * Flow for registerUser():
     * 1. Call User Service POST -> api/fitness-app/register
     * 2. Pass request body as RegisterRequest
     * 3. Retrieve response body as UserResponse
     * 4. If success → return Mono<UserResponse>
     * 5. If error:
     *      - 400 → "Bad Request"
     *      - 500 → "Internal Server Error"
     *      - otherwise → "Unexpected error"
     */
    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
        return userServiceWebClient.post()
                .uri("api/fitness-app/register")          // 1. Call API endpoint
                .bodyValue(registerRequest)              // 2. Send request body
                .retrieve()
                .bodyToMono(UserResponse.class)          // 3. Convert body to UserResponse
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                        return Mono.error(new RuntimeException("BAD_REQUEST: " + e.getMessage())); // 5a
                    } else if (e.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                        return Mono.error(new RuntimeException("INTERNAL_SERVER_ERROR: " + e.getMessage())); // 5b
                    }
                    return Mono.error(new RuntimeException("Unexpected error from User Service: " + e.getMessage(), e)); // 5c
                });
    }
}
