package com.binod.activity_microservice.service;

import com.binod.activity_microservice.dto.ActivityRequest;
import com.binod.activity_microservice.dto.ActivityResponse;
import com.binod.activity_microservice.model.Activity;
import com.binod.activity_microservice.repo.ActivityRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ActivityService {
    @Autowired
    private ActivityRepo repo;
    @Autowired
    private  UserValidationService userValidationService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value(("${rabbitmq.routing.key}"))
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest activityRequest){
        boolean isValidUser= userValidationService.validateUser(activityRequest.getUserId());
         if(!isValidUser){
             throw  new RuntimeException("Invalid user"+activityRequest.getUserId());
         }
        Activity activity= new Activity().builder()
                .userId(activityRequest.getUserId())
                .type(activityRequest.getType())
                .duration(activityRequest.getDuration())
                .calorieBurned(activityRequest.getCaloriesBurned())
                .startTime(activityRequest.getStartTime())
                .additionalMetrics(activityRequest.getAdditionalMetrics())
                .build();
        Activity savedActivity =repo.save(activity);

        //publish to rabbitmq
        try{
            rabbitTemplate.convertAndSend(exchange,routingKey,savedActivity);

        }catch (Exception e){
            log.error("Failed to publish activity to RabbitMq",e);

        }

        return mapToResponse(savedActivity);
    }
    private ActivityResponse mapToResponse(Activity activity){
        ActivityResponse activityResponse= new ActivityResponse();
        activityResponse.setId(activity.getId());
        activityResponse.setUserId(activity.getUserId());
        activityResponse.setUserId(activity.getUserId());
        activityResponse.setType(activity.getType());
        activityResponse.setDuration(activity.getDuration());
        activityResponse.setCalorieBurned(activity.getCalorieBurned());
        activityResponse.setStartTime(activity.getStartTime());
        activityResponse.setAdditionalMetrics(activity.getAdditionalMetrics());
        activityResponse.setCreatedAt(activity.getCreatedAt());
        activityResponse.setUpdatedAt(activity.getUpdatedAt());

        return activityResponse;


    }


    public List<ActivityResponse> getUserActivities(String userID) {
      List<Activity> activities=  repo.findByUserId(userID);
      return activities.stream()
              .map(this::mapToResponse)
              .collect(Collectors.toList());  // converts Activity object to ActivityResponse object
    }

    public ActivityResponse getActivity(String id){
        Optional<Activity> activity= repo.findById(id);
        return activity
                .map(this::mapToResponse)
                .orElseThrow(()->new RuntimeException("Activity not found"));
    }

}
