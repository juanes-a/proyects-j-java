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
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.Role;
import com.example.demo.service.UserService;
import com.example.demo.enums.ProjectStatus;
import com.example.demo.enums.ProjectPriority;
import com.example.demo.service.ActivityService;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.EmailService;
import com.example.demo.service.ProjectService;
import com.example.demo.exception.ProjectNotFoundException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.exception.BusinessException;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;


import com.example.demo.service.UserService;
import com.example.demo.util.PdfProjectReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    @Autowired
    private UserService UserService;
    @Autowired
    private  EmailService emailService ; 

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
        private boolean hasAssignees;
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

    @GetMapping("/report/pdf")
    public ResponseEntity<byte[]> generatePdfReport(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) ProjectPriority priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minBudget,
            @RequestParam(required = false) BigDecimal maxBudget
    ) {
        try {
            log.info("📄 Generando PDF con filtros: name={}, deptId={}, status={}, priority={}, start={}, end={}, minBudget={}, maxBudget={}",
                    name, departmentId, status, priority, startDate, endDate, minBudget, maxBudget);

            // 👉 Usamos el nuevo método del servicio que sí filtra bien
            List<Project> filteredProjects = projectService.searchProjects(
                    name,
                    departmentId,
                    status,
                    priority,
                    startDate,
                    endDate,
                    minBudget,
                    maxBudget
            );

            ByteArrayInputStream pdfStream = PdfProjectReportGenerator.generate(filteredProjects);
            byte[] pdfBytes = pdfStream.readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=projects-report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("❌ Error al generar el reporte PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




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

    /* Obtener lo projectos por email */
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
        // [CORRECCIÓN] Cambiar el log para que use la ruta correcta
        log.info("Datos recibidos en /projects/assign-user: {}", request);

        try {
            // 1. Ejecutar la lógica de asignación (valida usuario/proyecto, asigna rol)
            UsersAsignation assignment = projectService.assignUserToProject(
                request.getUsernameOrEmail(),
                request.getProjectId(),
                request.getRole()
            );

            log.info("Asignación exitosa a proyecto: {}", assignment);

            // 2. Buscar el correo del usuario asignado para el email
            UserEntity usuarioAsignado = UserService.findByUsernameOrEmail(request.getUsernameOrEmail());

            if (usuarioAsignado != null && usuarioAsignado.getEmail() != null) {
                // 3. Enviar correo
                emailService.enviarCorreo(
                    usuarioAsignado.getEmail(),
                    "Asignación a proyecto",
                    "Has sido asignado al proyecto con ID: " + request.getProjectId() +
                    " con el rol de: " + request.getRole()
                );
            }

            return ResponseEntity.ok(assignment);

        } catch (BusinessException e) {
            // [NUEVO] Manejo explícito de BusinessException (Ej. Usuario no encontrado, Proyecto ya asignado)
            log.error("Error de negocio en asignación a proyecto: ", e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            // [CORRECCIÓN] Manejo genérico para cualquier otra excepción (problemas de correo, JPA, etc.)
            log.error("Error interno en asignación a proyecto: ", e);
            
            // Mensaje más seguro y devolver 500 (Internal Server Error) para errores de servidor.
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Ocurrió un error interno en el servidor. Verifique la configuración del correo o la base de datos.";
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) 
                .body(new ErrorResponse("Error al asignar usuario al proyecto: " + errorMessage));
        }
    }



    // Agregar este método en tu controller


    /*Cargar proyecto asignado */
    @GetMapping("assing-project/{usernameOrEmail}")
    public ResponseEntity<?> getProjectsByUserProject(@PathVariable @NotBlank String usernameOrEmail) {
        try {
            String normalizedInput = usernameOrEmail.trim().toLowerCase();

            // Obtener usuario para verificar rol
            UserEntity user = UserService.findByUsernameOrEmail(normalizedInput);


            List<TaskResponse> taskResponses;
            Map<String, Object> response = new HashMap<>();

            // Si es ADMIN_GLOBAL, obtener TODAS las tareas
            if (Role.ADMIN_GLOBAL.equals(user.getRole())){
                List<TaskEntity> allTasks = taskRepository.findAll();
                taskResponses = allTasks.stream()
                        .map(TaskResponse::fromEntity)
                        .toList();

                response.put("projectId", null);
                response.put("projectName", "Todos los proyectos");
                response.put("tasks", taskResponses);

            } else {
                // Lógica existente para ADMIN_COLLAB
                UsersAsignation assignment = projectService.getUserProjectAssignment(normalizedInput)
                        .orElseThrow(() -> new ResourceNotFoundException("Usuario sin proyecto asignado"));

                Project project = assignment.getProject();
                List<TaskEntity> taskList = taskRepository.findByProjectId(project.getId());

                taskResponses = taskList.stream()
                        .map(TaskResponse::fromEntity)
                        .toList();

                response.put("projectId", project.getId());
                response.put("projectName", project.getName());
                response.put("tasks", taskResponses);
            }

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }


    // ============== Carga Masiva ==============

    @PostMapping("/upload")
    public ResponseEntity<?> uploadCsv(@RequestParam("file") MultipartFile file) {
        log.info("📧 Petición de carga masiva CSV recibida.");
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("El archivo no puede estar vacío."));
        }

        try {
            // Llama al método que usa CsvHelper.parseProjectsDto(file)
            projectService.processCsvUpload(file); 
            return ResponseEntity.ok(new ErrorResponse("Carga masiva CSV completada exitosamente."));

        } catch (BusinessException e) {
            // 400 BAD REQUEST: Errores de datos (IDs no válidos, fechas/enums incorrectos)
            log.error("❌ Error de negocio en carga CSV: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            // 500 INTERNAL SERVER ERROR: Errores de I/O, del servidor, o parseo inesperado
            log.error("❌ Error interno del servidor al procesar CSV: ", e);
            String errorMessage = "Error interno del servidor al procesar CSV. Revise los logs del servidor.";
            
            if (e.getMessage() != null && e.getMessage().contains("multipart")) {
                 errorMessage = "Error al leer el archivo. Asegúrese de que el archivo es un CSV válido y no está corrupto.";
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(errorMessage));
        }
    }

    @PostMapping("/upload/excel")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file) {
        log.info("📧 Petición de carga masiva Excel recibida.");
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("El archivo no puede estar vacío."));
        }

        try {
            // CORRECCIÓN: Se elimina el casting innecesario ((Object) projectService)
            // Ya que el método processExcelUpload está ahora correctamente definido en ProjectService
            projectService.processExcelUpload(file); 
            return ResponseEntity.ok(new ErrorResponse("Carga masiva Excel completada exitosamente."));
            
        } catch (BusinessException e) {
            // 400 BAD REQUEST: Errores de datos (IDs no válidos, fechas/enums incorrectos)
            log.error("❌ Error de negocio en carga Excel: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            // 500 INTERNAL SERVER ERROR: Errores de I/O, del servidor, o parseo inesperado
            log.error("❌ Error interno del servidor al procesar Excel: ", e);
            String errorMessage = "Error interno del servidor al procesar Excel. Revise los logs del servidor.";
            
            if (e.getMessage() != null && e.getMessage().contains("multipart")) {
                 errorMessage = "Error al leer el archivo. Asegúrese de que el archivo es un Excel válido y no está corrupto.";
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(errorMessage));
        }
    }
}