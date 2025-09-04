package com.binod.AI_SERVICE.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiUri;
    @Value("${gemini.api.key}")
    private String geminiKey;

    // 1. WebClient.Builder injected -> build() to create WebClient
    public GeminiService(WebClient.Builder webclientBuilder) {
        this.webClient = webclientBuilder.build();
    }

    public String getAnswer(String prompt) {
        // 2. Build request body -> structure must match Gemini API format
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt) // 3. Put user prompt here
                        })
                }
        );

        // 4. Make POST request with headers and body
        String response = webClient.post()
                .uri(geminiUri) // 5. Target Gemini endpoint
                .header("Content-Type", "application/json") // 6. Set content type
                .header("X-goog-api-key", geminiKey) // 7. Add API key
                .bodyValue(requestBody) // 8. Attach body (prompt)
                .retrieve()
                .bodyToMono(String.class) // 9. Convert response to String
                .block(); // 10. Block to get sync response (not reactive)

        // 11. Return response back to controller/service layer
        return response;
    }
}

// format the request because the curl api takes request as
//curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent" \
//        -H 'Content-Type: application/json' \
//        -H 'X-goog-api-key: GEMINI_API_KEY' \
//        -X POST \
//        -d '{
//        "contents": [
//        {
//        "parts": [
//        {
//        "text": "Explain how AI works in a few words"
//        }
//        ]
//        }
//        ]
//        }'
