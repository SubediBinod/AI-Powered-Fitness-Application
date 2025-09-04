package com.binod.AI_SERVICE.service;

import com.binod.AI_SERVICE.model.Activity;
import com.binod.AI_SERVICE.model.Recommendation;
import com.binod.AI_SERVICE.repo.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {
    private final AIService aiService;
    private final RecommendationRepository recommendationRepository;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processActivity(Activity activity){
        // 1. Receive Activity message from RabbitMQ queue
        log.info("Received activity for {}", activity.getUserId());

        // 2. Call AIService to generate a Recommendation based on Activity
        Recommendation recommendation = aiService.generateRecommendation(activity);

        // 3. Save generated Recommendation into RecommendationRepository (DB)
        recommendationRepository.save(recommendation);
    }
}
