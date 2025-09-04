package com.binod.AI_SERVICE.service;

import com.binod.AI_SERVICE.model.Activity;
import com.binod.AI_SERVICE.model.Recommendation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class AIService {

    @Autowired
    private GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {
        // 1. Create a prompt string based on activity details
        String prompt = createPrompt(activity);

        // 2. Call GeminiService with the prompt → get AI response
        String aiResponse = geminiService.getAnswer(prompt);

        // 3. Process AI response into Recommendation object
        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // 4. Parse raw AI response into JSON tree
            JsonNode rootNode = mapper.readTree(aiResponse);

            // 5. Navigate to actual text node (Gemini puts JSON inside "candidates/content/parts[0]/text")
            JsonNode textNode = rootNode.path("candidates")
                    .get(0).path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            // 6. Extract raw text string
            String rawText = textNode.asText();

            // 7. Clean up → extract only JSON block from response
            String jsonContent = extractJsonBlock(rawText);
            log.info("Cleaned JSON content: {}", jsonContent);

            // 8. Parse cleaned JSON into JsonNode
            JsonNode analysisJson = mapper.readTree(jsonContent);

            // 9. Get analysis object
            JsonNode analysisNode = analysisJson.path("analysis");

            // 10. Build a human-readable analysis string (Overall, Pace, Heart Rate, Calories)
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate:");
            addAnalysisSection(fullAnalysis, analysisNode, "CaloriesBurned", "Calories Burned:");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories Burned:");

            // 11. Extract improvements, suggestions, and safety sections
            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            List<String> safety = extractSafetyGuidelines(analysisJson.path("safety"));

            // 12. Build and return Recommendation object
            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            // 13. If parsing fails, log error and fallback to default recommendation
            log.error("Failed to parse AI response, falling back. Response={}", aiResponse, e);
            return createDefaultRecommendation(activity);
        }
    }

    /** Extracts only the JSON block, stripping ```json ... ``` or extra text */
    private String extractJsonBlock(String rawText) {
        if (rawText == null) return "{}";

        // Remove code fences like ```json ... ```
        String cleaned = rawText.replaceAll("(?s)```[a-zA-Z]*", "")
                .replaceAll("(?s)```", "")
                .trim();

        // If multiple blocks, pick the first {...}
        int start = cleaned.indexOf("{");
        int end = cleaned.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return cleaned.substring(start, end + 1);
        }
        return cleaned; // fallback
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Unable to generate analysis at this time.")
                .improvements(Collections.singletonList("We don’t have improvements right now"))
                .suggestions(Collections.singletonList("Consider consulting a fitness trainer"))
                .safety(Collections.singletonList("Stay hydrated"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if (safetyNode.isArray()) {
            safetyNode.forEach(item -> safety.add(item.asText()));
        }
        return safety.isEmpty()
                ? Collections.singletonList("No specific safety provided")
                : safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if (suggestionsNode.isArray()) {
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s : %s", workout, description));
            });
        }
        return suggestions.isEmpty()
                ? Collections.singletonList("No specific suggestions provided")
                : suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementNode) {
        List<String> improvements = new ArrayList<>();
        if (improvementNode.isArray()) {
            improvementNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvements.add(String.format("%s : %s", area, detail));
            });
        }
        return improvements.isEmpty()
                ? Collections.singletonList("No specific improvements provided")
                : improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if (!analysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix)
                    .append(" ")
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPrompt(Activity activity) {
        // 14. Build the exact prompt string for AI, including JSON format instructions and activity details
        return String.format("""
                  Analyze this fitness activity and provide detailed recommendations in the following format:
                  {
                      "analysis" : {
                          "overall": "Overall analysis here",
                          "pace": "Pace analysis here",
                          "heartRate": "Heart rate analysis here",
                          "CaloriesBurned": "Calories Burned here"
                      },
                      "improvements": [
                          {
                              "area": "Area name",
                              "recommendation": "Detailed Recommendation"
                          }
                      ],
                      "suggestions" : [
                          {
                              "workout": "Workout name",
                              "description": "Detailed workout description"
                          }
                      ],
                      "safety": [
                          "Safety point 1",
                          "Safety point 2"
                      ]
                  }
                  
                  Analyze this activity:
                  Activity Type: %s
                  Duration: %d minutes
                  Calories Burned: %d
                  Additional Metrics: %s
                  
                  Provide detailed analysis focusing on performance, improvements,
                  next workout suggestions, and safety guidelines.
                  Ensure the response follows the EXACT JSON format shown above.
                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCalorieBurned(),
                activity.getAdditionalMetrics());
        //String.format() allows you to create a formatted string by inserting values into
        // placeholders inside the template string.
        //Placeholders like %s, %d, %f, etc. are replaced by actual values you pass in order.
    }
}
