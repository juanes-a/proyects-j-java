package com.example.demo.controller;

import com.example.demo.controller.ProjectController.ProjectResponse;
import com.example.demo.dto.*;
import com.example.demo.dto.request.project.AssignUserToProjectRequest;
import com.example.demo.dto.request.project.ProjectCreateDTO;
import com.example.demo.dto.request.project.ProjectUpdateDTO;
import com.example.demo.dto.response.ProjectResponseDTO;
import com.example.demo.dto.response.TaskResponse;
import com.example.demo.entity.Department;
import com.example.demo.entity.Project;
import com.example.demo.entity.TaskEntity;
import com.example.demo.entity.UsersAsignation;
import com.example.demo.enums.ProjectStatus;
import com.example.demo.enums.ProjectPriority;
import com.example.demo.service.ActivityService;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.ProjectService;
import com.example.demo.exception.ProjectNotFoundException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.exception.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:5173")
@Validated
public class ProjectController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;
    
        @Autowired
    private ProjectRepository projectRepository;

        @Autowired
    private TaskRepository taskRepository;
    

    @Autowired
    private DepartmentService departmentService;

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
            
            Project updatedProject = projectService.updateProject(id, projectUpdateDTO);
            ProjectResponseDTO response = ProjectResponseDTO.fromEntity(updatedProject);
            
            activityService.logProjectUpdated(response.getName());
            return ResponseEntity.ok(response);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return buildErrorResponse(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar el proyecto");
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
    public ResponseEntity<?> deleteProject(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String providedName = request.get("name");

        try {
            Project project = projectService.getProjectEntityById(id);

            if (!project.getName().equals(providedName)) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Project name does not match. Deletion not allowed."));
            }

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
    public ResponseEntity<List<ProjectResponseDTO>> getOverdueProjects() {
        List<Project> projects = projectService.getOverdueProjects();

        List<ProjectResponseDTO> dtos = projects.stream()
                                            .map(ProjectResponseDTO::fromEntity)
                                            .toList();
        return ResponseEntity.ok(dtos);
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

    /*  Obtener lo projectos por email */
    @GetMapping("user/{usernameOrEmail}")
    public ResponseEntity<?> getProjectsByUserDepartment(@PathVariable @NotBlank String usernameOrEmail) {
        try {
            // Validación básica
            if (usernameOrEmail.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "El username/email no puede estar vacío"));
            }

            String normalizedInput = usernameOrEmail.trim().toLowerCase();
            
            // Obtener asignación del usuario
            UsersAsignation assignment = departmentService.getUserDepartmentAssignment(normalizedInput)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado o sin departamento asignado"));

            Department department = Optional.ofNullable(assignment.getDepartment())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado para el usuario"));

            // Obtener las entidades Project directamente del repositorio
            List<Project> projectEntities = projectRepository.findByDepartmentId(department.getId());
            
            // Convertir a DTOs
            List<ProjectResponseDTO> projects = projectEntities.stream()
                .map(ProjectResponseDTO::fromEntity)
                .toList();

            // Construir respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("departmentId", department.getId());
            response.put("departmentName", department.getName());
            response.put("projects", projects);

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error en getProjectsByUserDepartment: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("message", "Error interno del servidor"));
        }
    }


    /*Asignar un proyecto */
    @PostMapping("/assign-user")
    public ResponseEntity<?> assignUserToProject(@RequestBody AssignUserToProjectRequest request) {
        log.info("Datos recibidos en /projects/assign: {}", request);

        try {
            UsersAsignation assignment = projectService.assignUserToProject(
                request.getUsernameOrEmail(),
                request.getProjectId(),
                request.getRole()
            );
            log.info("Asignación exitosa a proyecto: {}", assignment);
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            log.error("Error en asignación a proyecto: ", e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error al asignar usuario al proyecto: " + e.getMessage()));
        }
    }

    /*Cargar proyecto asignado */
    @GetMapping("assing-project/{usernameOrEmail}")
    public ResponseEntity<?> getProjectsByUserProject(@PathVariable @NotBlank String usernameOrEmail) {
        try {
            // Validación básica
            if (usernameOrEmail.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "El username/email no puede estar vacío"));
            }

            String normalizedInput = usernameOrEmail.trim().toLowerCase();
            
            // Obtener asignación del usuario
            UsersAsignation assignment = projectService.getUserProjectAssignment(normalizedInput)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado o sin proyecto asignado"));
            
            // Obtener proyecto
            Project project = Optional.ofNullable(assignment.getProject())
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado para el usuario"));

            // Obtener las entidades tareas directamente del repositorio
        List<TaskEntity> taskList = taskRepository.findByProjectId(project.getId());
            
        // Convertir a DTOs
        List<TaskResponse> taskResponses = taskList.stream()
        .map(TaskResponse::fromEntity)
        .toList();

        // Construir respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("projectId", project.getId());
        response.put("projectName", project.getName());
        response.put("tasks", taskResponses);

        return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error en getProjectsByUserDepartment: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("message", "Error interno del servidor"));
        }
    }




}

    
