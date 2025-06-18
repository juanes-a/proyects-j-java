package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.request.project.ProjectCreateDTO;
import com.example.demo.dto.request.project.ProjectUpdateDTO;
import com.example.demo.entity.Project;
import com.example.demo.enums.ProjectStatus;
import com.example.demo.enums.ProjectPriority;
import com.example.demo.service.ActivityService;
import com.example.demo.service.ProjectService;
import com.example.demo.exception.ProjectNotFoundException;
import com.example.demo.exception.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:5173")
@Validated
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ActivityService activityService; 

    // ============== DTOs ==============
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectResponse {
        private Long id;
        private String name;
        private String description;
        private DepartmentDTO department;
        private String status;
        private String priority;
        private LocalDate startDate;
        private LocalDate endDate;
        private Double budget;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentDTO {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelRequest {
        private String reason;
    }

    // ============== Endpoints CRUD ==============

    @PostMapping
    public ResponseEntity<?> createProject(
            @Valid @RequestBody ProjectCreateDTO projectCreateDTO,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        try {
            // Validación adicional de fechas
            if (projectCreateDTO.getStartDate().isAfter(projectCreateDTO.getEndDate())) {
                throw new BusinessException("La fecha de fin debe ser posterior a la fecha de inicio");
            }

            ProjectResponse response = projectService.createProject(projectCreateDTO);
                    // Registrar la actividad
            activityService.logProjectCreated(response.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (BusinessException e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateDTO projectUpdateDTO,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }
        
        try {
            ProjectResponse response = projectService.updateProject(id, projectUpdateDTO);

                // Registrar la actividad
            activityService.logProjectUpdated(response.getName());
            return ResponseEntity.ok(response);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        try {
            ProjectResponse project = projectService.getProjectById(id);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
            try {
                projectService.deleteProject(id);
                return ResponseEntity.noContent().build();
            } catch (ProjectNotFoundException e) {
                return ResponseEntity.notFound().build();
            } catch (BusinessException e) {
                return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
            }
        }

    // ============== Gestión de Estado ==============

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startProject(@PathVariable Long id) {
        try {
            ProjectResponse project = projectService.startProject(id);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeProject(@PathVariable Long id) {
        try {
            ProjectResponse project = projectService.completeProject(id);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelProject(
            @PathVariable Long id, 
            @RequestBody(required = false) CancelRequest request) {
        try {
            ProjectResponse project = projectService.cancelProject(id, 
                request != null ? request.getReason() : null);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ============== Consultas Especiales ==============

    @GetMapping("/overdue")
    public ResponseEntity<List<Project>> getOverdueProjects() {
        List<Project> projects = projectService.getOverdueProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/urgent")
    public ResponseEntity<?> getUrgentProjects() {
        try {
            List<ProjectResponse> projects = projectService.getUrgentProjects();
            return ResponseEntity.ok(projects);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/ending-this-week")
    public ResponseEntity<List<ProjectResponse>> getProjectsEndingThisWeek() {
        List<ProjectResponse> projects = projectService.getProjectsEndingThisWeek();
        return ResponseEntity.ok(projects);
    }

    // ============== Métodos de Apoyo ==============

    private ResponseEntity<Map<String, Object>> buildValidationErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = bindingResult.getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage,
                (existing, replacement) -> existing
            ));
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("errors", errors);
        response.put("message", "Validation failed");
        
        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", message);
        
        return ResponseEntity.badRequest().body(response);
    }

    
}