package com.binod.AI_SERVICE.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Activity {
    @Id
    private String id;
    private String userId;
    private String type;
    private Integer duration;
    private Integer calorieBurned;
    private LocalDateTime startTime;
    private Map<String,Object> additionalMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;




}
