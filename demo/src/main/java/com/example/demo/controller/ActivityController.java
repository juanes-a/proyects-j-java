package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ActivityDTO;
import com.example.demo.entity.Activity;
import com.example.demo.repository.ActivityRepository;


@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "http://localhost:5173")
public class ActivityController {
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @GetMapping("/recent")
    public ResponseEntity<List<ActivityDTO>> getRecentActivities() {
        List<Activity> activities = activityRepository.findTop5ByOrderByCreatedAtDesc();
        List<ActivityDTO> dtos = activities.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    private ActivityDTO convertToDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setDescription(activity.getDescription());
        dto.setType(activity.getType());
        dto.setCreatedAt(activity.getCreatedAt());
        return dto;
    }
}