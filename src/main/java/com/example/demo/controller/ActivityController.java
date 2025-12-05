package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ActivityDTO;
import com.example.demo.entity.Activity;
import com.example.demo.service.ActivityService;

@RestController
@RequestMapping("/api/activities")

public class ActivityController {
    
    @Autowired
    private ActivityService activityService;
    
    @GetMapping("/recent")
    public ResponseEntity<List<ActivityDTO>> getRecentActivities() {
        try {
            List<Activity> activities = activityService.getRecentActivities();
            List<ActivityDTO> dtos = activities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            // En caso de error, devolver lista vacía en lugar de error 500
            return ResponseEntity.ok(List.of());
        }
    }
    
    /**
     * Endpoint para crear actividades de prueba
     * Útil para testing - puedes llamarlo desde Postman o crear un botón en el frontend
     */
    @PostMapping("/sample")
    public ResponseEntity<String> createSampleActivities() {
        try {
            activityService.createSampleActivities();
            return ResponseEntity.ok("Sample activities created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating sample activities: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para crear una actividad personalizada
     */
    @PostMapping("/create")
    public ResponseEntity<ActivityDTO> createActivity(
            @RequestParam String type,
            @RequestParam String description) {
        try {
            Activity activity = activityService.createActivity(type, description);
            ActivityDTO dto = convertToDTO(activity);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private ActivityDTO convertToDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setDescription(activity.getDescription());
        dto.setType(activity.getType());
        dto.setCreatedAt(activity.getCreatedAt().toString());
        return dto;
    }
}