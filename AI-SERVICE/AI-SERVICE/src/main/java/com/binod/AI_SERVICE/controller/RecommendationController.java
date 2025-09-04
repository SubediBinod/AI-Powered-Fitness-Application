package com.binod.AI_SERVICE.controller;

import com.binod.AI_SERVICE.model.Recommendation;
import com.binod.AI_SERVICE.service.ServiceRecommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {
    @Autowired
    private ServiceRecommendation serviceRecommendation;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recommendation>> getUserRecommendation(@PathVariable String userId){
        return ResponseEntity.ok(serviceRecommendation.getUserRecommendation(userId));
    }
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Recommendation> getActivityRecommendation(@PathVariable String activityId){
        return ResponseEntity.ok(serviceRecommendation.getActivityRecommendation(activityId));
    }
}
