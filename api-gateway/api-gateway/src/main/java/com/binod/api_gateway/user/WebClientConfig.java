package com.binod.api_gateway.user;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced
    //The @LoadBalanced annotation tells Spring to integrate this builder with Spring Cloud LoadBalancer (or Ribbon if using older Spring Cloud).
    //
    //That means whenever you use a base URL like "http://USER-SERVICE",
    // Spring will not directly hit USER-SERVICE on localhost:port.
    // Instead, it will ask the service registry (like Eureka, Consul, or Nacos) to find the available instance(s) of USER-SERVICE.
    public WebClient.Builder webClientBuilder(){

        return WebClient.builder();
    }
    @Bean
    //This creates a ready-to-use WebClient bean configured with a base URL (http://USER-SERVICE).
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder){
        return webClientBuilder.baseUrl("http://USER-SERVICE")
                .build();
    }
}
// by creating this instance you are not required to give base url , instead
// you can use @Autowired WebClient userServiceWebClient


//so instead of doing this
//        webClient.get()
//        .uri("http://USER-SERVICE/api/users/1")
//        .retrieve()
//        .bodyToMono(User.class);
//
//
//you can do this
//        userServiceWebClient.get()
//        .uri("/api/users/1")
//        .retrieve()
//        .bodyToMono(User.class);
