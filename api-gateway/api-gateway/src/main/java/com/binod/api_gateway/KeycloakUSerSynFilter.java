package com.binod.api_gateway;

import com.binod.api_gateway.user.RegisterRequest;
import com.binod.api_gateway.user.UserService;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class KeycloakUSerSynFilter implements WebFilter {
    @Autowired
    private  UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // FILTER PIPELINE BASICS:
        // 1. filter() is the main entry point: called for EVERY request.
        // 2. exchange = full request/response context (headers, body, etc).
        // 3. chain = used to pass request further down the filter chain.
        // 4. return type is Mono<Void> = async signal of success/failure.
        //    (Reactive: don't block/wait, just return "I'll finish later")

        // --- STEP 1: Extract headers from the request ---
        String token= exchange.getRequest().getHeaders().getFirst("Authorization"); // Bearer token
        String userId= exchange.getRequest().getHeaders().getFirst("X-User-Id");    // Custom header if present

        // --- STEP 2: Decode JWT into RegisterRequest (contains user info) ---
        RegisterRequest registerRequest= getUserDetail(token);

        // --- STEP 3: If no userId in header, fallback to extracting from JWT (sub claim = keycloakId) ---
        if(userId==null){
            userId=registerRequest.getKeycloakId();
        }

        // --- STEP 4: If we have both token + userId, validate & sync user with user-service ---
        if(userId!=null && token != null){
            String finalUserId = userId;

            // 4.1 Validate user in user-service
            return userService.validateUser(userId)
                    //Use flatMap when you return reactive types (Mono, Flux)
                    .flatMap(exist->{
                        if(!exist){
                            // 4.2 If user does not exist, register using RegisterRequest
                            if(registerRequest!=null){
                                return userService.registerUser(registerRequest)
                                        .then(Mono.empty()); // return "done" but no data
                            }
                            else {
                                return Mono.empty();
                            }
                        }
                        else {
                            // 4.3 If user already exists, skip registration
                            log.info("User Already Exist, Skipping sync");
                            return Mono.empty();
                        }
                    })
                    // 4.4 After validate/register flow completes → mutate request and continue
                    .then(Mono.defer(()->{
                        // Mutate = create a copy of request with new headers
                        ServerHttpRequest mutatedRequest= exchange.getRequest().mutate()
                                .header("X-User-Id", finalUserId) // inject X-User-Id header
                                .build();

                        // Continue filter chain with the modified request
                        return chain.filter((exchange.mutate().request(mutatedRequest).build()));
                    }));

        }

        // --- STEP 5: If no userId/token, just continue with original request ---
        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetail(String token) {
        // JWT DECODING FLOW:
        // 1. Remove "Bearer" prefix
        // 2. Parse string into SignedJWT
        // 3. Extract claims (payload)
        // 4. Pull fields like email, sub, given_name, family_name

        try {
            // 1. Remove "Bearer" prefix (Authorization: Bearer <token>)
            String tokenWithoutBearer=token.replace("Bearer","").trim();

            // 2. Parse token into SignedJWT
            SignedJWT signedJWT= SignedJWT.parse(tokenWithoutBearer);

            // 3. Extract claims (payload data)
            JWTClaimsSet claims=signedJWT.getJWTClaimsSet();

            // 4. Build RegisterRequest using claims
            RegisterRequest registerRequest= new RegisterRequest();
            registerRequest.setEmail(claims.getStringClaim("email"));        // user email
            registerRequest.setKeycloakId(claims.getStringClaim("sub"));     // unique ID
            registerRequest.setPassword("123123");                           // default password (temporary)
            registerRequest.setFirstName(claims.getStringClaim("given_name"));
            registerRequest.setLastName(claims.getStringClaim("family_name"));

            return registerRequest;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
