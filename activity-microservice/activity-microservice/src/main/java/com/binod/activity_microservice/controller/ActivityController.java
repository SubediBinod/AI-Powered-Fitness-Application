package com.binod.activity_microservice.controller;

import com.binod.activity_microservice.dto.ActivityRequest;
import com.binod.activity_microservice.dto.ActivityResponse;
import com.binod.activity_microservice.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest activityRequest){


        return ResponseEntity.ok(activityService.trackActivity(activityRequest));

    }


    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserActivities(@RequestHeader("X-User-Id") String userID){
        return ResponseEntity.ok(activityService.getUserActivities(userID));

    }
    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse>getActivity(@PathVariable String activityId){
        return ResponseEntity.ok(activityService.getActivity(activityId));
    }
}
