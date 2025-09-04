package com.binod.AI_SERVICE.service;

import com.binod.AI_SERVICE.model.Recommendation;
import com.binod.AI_SERVICE.repo.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceRecommendation {
    @Autowired
    RecommendationRepository repo;

    public List<Recommendation> getUserRecommendation(String userId) {

        return repo.findByUserId(userId);
    }

    public Recommendation getActivityRecommendation(String activityId) {

        return repo.findByActivityId(activityId)
                .orElseThrow(() -> new RuntimeException("No Recommendation found for "+activityId));
    }
}
