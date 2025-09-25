package com.example.demo.controller;

import com.example.demo.controller.ProjectController.ProjectResponse;
import com.example.demo.dto.request.department.AssignUserToDepartmentRequest;
import com.example.demo.dto.request.department.DepartmentRequestDTO;
import com.example.demo.dto.request.department.DepartmentUpdateDTO;
import com.example.demo.dto.request.department.UpdateUserRoleRequest;
import com.example.demo.dto.response.DepartmentResponseDTO;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TeamMemberRepository;
import com.example.demo.entity.Department;
import com.example.demo.entity.Project;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.UsersAsignation;
import com.example.demo.enums.ProjectStatus;
import com.example.demo.enums.Role;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.ActivityService; // NUEVO: Importar ActivityService
import com.example.demo.service.DepartmentService.DepartmentStats;
import com.example.demo.service.EmailService;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "http://localhost:5173")
public class DepartmentController {


    private static final Logger log = LoggerFactory.getLogger(DepartmentController.class);
    @Autowired
    private final DepartmentService departmentService;
    
    // NUEVO: Inyección de ActivityService
    @Autowired
    private ActivityService activityService;


        @Autowired
    private ProjectService projectService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;
    


    @Autowired
    private  EmailService emailService ;
    private UserService userService; 

    

    @Autowired
    public DepartmentController(DepartmentService departmentService, UserService userService) {
        this.departmentService = departmentService;
        this.projectService = projectService;
        this.activityService = activityService;
        this.departmentRepository = departmentRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userService = userService;
    }

    @GetMapping("/{departmentId}/projects/count")
    public ResponseEntity<Map<String, Long>> countProjectsByDepartment(
            @PathVariable Long departmentId) {
        try {
            long count = projectService.countProjectsByDepartment(departmentId);
            return ResponseEntity.ok(Collections.singletonMap("count", count));
        } catch (Exception e) {
            log.error("Error counting projects for department {}: {}", departmentId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * CREAR NUEVO DEPARTAMENTO
     * POST /api/departments
     */
    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@Valid @RequestBody DepartmentRequestDTO requestDTO) {
        try {
            Department department = convertToEntity(requestDTO);
            Department createdDepartment = departmentService.createDepartment(department);
            
            // NUEVO: Registrar actividad de creación
            activityService.logDepartmentCreated(createdDepartment.getName());
            
            DepartmentResponseDTO responseDTO = convertToResponseDTO(createdDepartment);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("Error creating department: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * OBTENER DEPARTAMENTO CON PROYECTOS - ESTE ES EL ENDPOINT QUE NECESITAS
     * GET /api/departments/{id}
     */
    @GetMapping("/{id}")
    @Transactional // IMPORTANTE: Para mantener la sesión activa
    public ResponseEntity<Department> getDepartmentWithProjects(@PathVariable Long id) {
        // Usar el método con JOIN FETCH para traer los proyectos
        Optional<Department> departmentOpt = departmentRepository.findByIdWithProjects(id);

        if (departmentOpt.isPresent()) {
            Department department = departmentOpt.get();
            // Forzar la inicialización de los proyectos por si acaso
            department.getProjects().size();
            return ResponseEntity.ok(department);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * OBTENER DEPARTAMENTO POR ID (SOLO DATOS BÁSICOS SIN PROYECTOS)
     * GET /api/departments/{id}/basic
     */
    @GetMapping("/{id}/basic")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentBasicById(@PathVariable Long id) {
        Department department = departmentService.getDepartmentById(id);
        DepartmentResponseDTO responseDTO = convertToResponseDTO(department);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * OBTENER TODOS LOS DEPARTAMENTOS
     * GET /api/departments
     */
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        List<DepartmentResponseDTO> responseDTOs = departments.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * OBTENER SOLO DEPARTAMENTOS ACTIVOS
     * GET /api/departments/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<DepartmentResponseDTO>> getActiveDepartments() {
        List<Department> activeDepartments = departmentService.getActiveDepartments();
        List<DepartmentResponseDTO> responseDTOs = activeDepartments.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * OBTENER DEPARTAMENTOS CON PRESUPUESTO ACTIVO
     * GET /api/departments/with-budget
     */
    @GetMapping("/with-budget")
    public ResponseEntity<List<DepartmentResponseDTO>> getDepartmentsWithBudget() {
        List<Department> departments = departmentService.getDepartmentsWithActiveBudget();
        List<DepartmentResponseDTO> responseDTOs = departments.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * OBTENER DEPARTAMENTO POR NOMBRE
     * GET /api/departments/by-name/{name}
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentByName(@PathVariable String name) {
        Optional<Department> department = departmentService.getDepartmentByName(name);
        return department.map(dept -> ResponseEntity.ok(convertToResponseDTO(dept)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ACTUALIZAR DEPARTAMENTO
     * PUT /api/departments/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentUpdateDTO updateDTO) {
        try {
            Department department = convertToEntityForUpdate(updateDTO);
            Department updatedDepartment = departmentService.updateDepartment(id, department);
            
            // NUEVO: Registrar actividad de actualización
            activityService.logDepartmentUpdated(updatedDepartment.getName());
            
            DepartmentResponseDTO responseDTO = convertToResponseDTO(updatedDepartment);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error updating department with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * ACTIVAR DEPARTAMENTO
     * PATCH /api/departments/{id}/activate
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<DepartmentResponseDTO> activateDepartment(@PathVariable Long id) {
        try {
            Department activatedDepartment = departmentService.activateDepartment(id);
            
            // NUEVO: Registrar actividad de activación
            activityService.logDepartmentUpdated(activatedDepartment.getName() + " (Activated)");
            
            DepartmentResponseDTO responseDTO = convertToResponseDTO(activatedDepartment);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error activating department with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DESACTIVAR DEPARTAMENTO (SOFT DELETE)
     * PATCH /api/departments/{id}/deactivate
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<DepartmentResponseDTO> deactivateDepartment(@PathVariable Long id) {
        try {
            Department deactivatedDepartment = departmentService.deactivateDepartment(id);
            
            // NUEVO: Registrar actividad de desactivación
            activityService.logDepartmentUpdated(deactivatedDepartment.getName() + " (Deactivated)");
            
            DepartmentResponseDTO responseDTO = convertToResponseDTO(deactivatedDepartment);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error deactivating department with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * ELIMINAR PERMANENTEMENTE (HARD DELETE)
     * DELETE /api/departments/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        try {
            // NUEVO: Obtener el nombre antes de eliminar para el log
            Department department = departmentService.getDepartmentById(id);
            String departmentName = department.getName();
            
            departmentService.deleteDepartmentPermanently(id);
            
            // NUEVO: Registrar actividad de eliminación
            activityService.logDepartmentDeleted(departmentName);
            
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting department with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * BUSCAR DEPARTAMENTOS CON FILTROS
     * GET /api/departments/search
     */
    @GetMapping("/search")
    public ResponseEntity<List<DepartmentResponseDTO>> searchDepartments(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) BigDecimal minBudget,
            @RequestParam(required = false) BigDecimal maxBudget) {

        List<Department> departments = departmentService.searchDepartments(
                name, location, isActive, minBudget, maxBudget);

        List<DepartmentResponseDTO> responseDTOs = departments.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * BUSCAR POR TEXTO LIBRE
     * GET /api/departments/search-text?q=texto
     */
    @GetMapping("/search-text")
    public ResponseEntity<List<DepartmentResponseDTO>> searchByText(@RequestParam("q") String searchTerm) {
        List<Department> departments = departmentService.searchByText(searchTerm);
        List<DepartmentResponseDTO> responseDTOs = departments.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * OBTENER ESTADÍSTICAS
     * GET /api/departments/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDepartmentStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Usar Optional para manejar valores nulos
        Long totalDepts = departmentRepository.count();
        Long activeDepts = departmentRepository.countByIsActive(true);
        BigDecimal totalBudget = Optional.ofNullable(departmentRepository.sumBudget())
                                       .orElse(BigDecimal.ZERO);
        
        stats.put("totalDepartments", totalDepts != null ? totalDepts : 0);
        stats.put("activeDepartments", activeDepts != null ? activeDepts : 0);
        stats.put("totalBudget", totalBudget);
        
        return ResponseEntity.ok(stats);
    }


      @GetMapping("/{departmentId}/stats")
    public ResponseEntity<?> getDepartmentStats(@PathVariable Long departmentId) {
        try {
            // 1. Validar que el departamento existe
            Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Departamento no encontrado"));

            // 2. Obtener estadísticas usando los métodos del ProjectService
            long totalProjects = projectService.countProjectsByDepartment(departmentId);
            long completedProjects = projectService.countByDepartmentIdAndStatus(departmentId, ProjectStatus.COMPLETED);
            long activeProjects = projectService.countByDepartmentIdAndStatus(departmentId, ProjectStatus.IN_PROGRESS);
            long plannedProjects = projectService.countByDepartmentIdAndStatus(departmentId, ProjectStatus.PLANNED);
            long cancelledProjects = projectService.countByDepartmentIdAndStatus(departmentId, ProjectStatus.CANCELLED);
            
            double totalBudget = department.getBudget() != null ? department.getBudget().doubleValue() : 0.0;
            double budgetUsed = projectService.getTotalBudgetUsedByDepartment(departmentId);

            // 3. Retornar respuesta estructurada
            Map<String, Object> response = Map.of(
                "totalProjects", totalProjects,
                "completedProjects", completedProjects,
                "activeProjects", activeProjects,
                "plannedProjects", plannedProjects,
                "cancelledProjects", cancelledProjects,
                "totalBudget", totalBudget,
                "budgetUsed", budgetUsed
            );

            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener estadísticas del departamento"));
        }
    }



    /**
     * VERIFICAR SI DEPARTAMENTO TIENE PRESUPUESTO SUFICIENTE
     * GET /api/departments/{id}/check-budget?amount=1000
     */
    @GetMapping("/{id}/check-budget")
    public ResponseEntity<BudgetCheckResponse> checkBudget(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {

        boolean hasSufficientBudget = departmentService.hasSufficientBudget(id, amount);
        BudgetCheckResponse response = new BudgetCheckResponse(id, amount, hasSufficientBudget);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // MÉTODOS DE CONVERSIÓN DTO ↔ ENTIDAD
    // ========================================

    private Department convertToEntity(DepartmentRequestDTO requestDTO) {
        Department department = new Department();
        department.setName(requestDTO.getName());
        department.setDescription(requestDTO.getDescription());
        department.setBudget(requestDTO.getBudget());
        department.setLocation(requestDTO.getLocation());
        return department;
    }

    private Department convertToEntityForUpdate(DepartmentUpdateDTO updateDTO) {
        Department department = new Department();
        department.setName(updateDTO.getName());
        department.setDescription(updateDTO.getDescription());
        department.setBudget(updateDTO.getBudget());
        department.setLocation(updateDTO.getLocation());
        department.setIsActive(updateDTO.getIsActive());
        return department;
    }

    private DepartmentResponseDTO convertToResponseDTO(Department department) {
        DepartmentResponseDTO responseDTO = new DepartmentResponseDTO();
        responseDTO.setId(department.getId());
        responseDTO.setName(department.getName());
        responseDTO.setDescription(department.getDescription());
        responseDTO.setBudget(department.getBudget());
        responseDTO.setLocation(department.getLocation());
        responseDTO.setIsActive(department.getIsActive());
        return responseDTO;
    }

    /**
     * CLASE INTERNA PARA RESPUESTA DE VERIFICACIÓN DE PRESUPUESTO
     */
    public static class BudgetCheckResponse {
        private Long departmentId;
        private BigDecimal requiredAmount;
        private boolean hasSufficientBudget;

        public BudgetCheckResponse(Long departmentId, BigDecimal requiredAmount, boolean hasSufficientBudget) {
            this.departmentId = departmentId;
            this.requiredAmount = requiredAmount;
            this.hasSufficientBudget = hasSufficientBudget;
        }

        public Long getDepartmentId() { return departmentId; }
        public BigDecimal getRequiredAmount() { return requiredAmount; }
        public boolean isHasSufficientBudget() { return hasSufficientBudget; }
    }


     /**
     * Asignar un usuario a un departamento
     */
    @PostMapping("/assign")
    public ResponseEntity<?> assignUserToDepartment(@RequestBody AssignUserToDepartmentRequest request) {
        log.info("Datos recibidos en /assign: {}", request);

        try {
            UsersAsignation assignment = departmentService.assignUserToDepartment(
                request.getUsernameOrEmail(), 
                request.getDepartmentId(), 
                request.getRole()
            );

            log.info("Asignación exitosa: {}", assignment);

            // 👇 Buscar el correo del usuario asignado
             UserEntity usuarioAsignado = userService.findByUsernameOrEmail(request.getUsernameOrEmail());

            if (usuarioAsignado != null && usuarioAsignado.getEmail() != null) {
                emailService.enviarCorreo(
                    usuarioAsignado.getEmail(),
                    "Asignación a departamento",
                    "Has sido asignado al departamento con ID: " + request.getDepartmentId() +
                    " con el rol de: " + request.getRole()
                );
            }

            return ResponseEntity.ok(assignment);

        } catch (Exception e) {
            log.error("Error en asignación: ", e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error al asignar usuario al departamento: " + e.getMessage()));
        }
    }

    /**
     * Actualizar el rol de un usuario en su departamento
     */
    @PutMapping("/update-role/{usernameOrEmail}")
    public ResponseEntity<?> updateUserDepartmentRole(
            @PathVariable String usernameOrEmail, 
            @RequestBody UpdateUserRoleRequest request) {
        try {
            UsersAsignation assignment = departmentService.updateUserDepartmentRole(
                usernameOrEmail, 
                request.getNewRole()
            );
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error al actualizar rol: " + e.getMessage()));
        }
    }

    /**
     * Remover usuario de su departamento
     */
    @DeleteMapping("/remove/{usernameOrEmail}")
    public ResponseEntity<?> removeUserFromDepartment(@PathVariable String usernameOrEmail) {
        try {
            departmentService.removeUserFromDepartment(usernameOrEmail);
            return ResponseEntity.ok(new SuccessResponse("Usuario removido del departamento exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error al remover usuario: " + e.getMessage()));
        }
    }

    /**
     * Obtener la asignación de departamento de un usuario
     */
    @GetMapping("/user/{usernameOrEmail}")
    public ResponseEntity<?> getUserDepartmentAssignment(
        @PathVariable @NotBlank String usernameOrEmail) {

        try {
            // 1. Validación básica del parámetro
            if (usernameOrEmail.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("El username/email no puede estar vacío"));
            }

            // 2. Normalizar entrada
            String normalizedInput = usernameOrEmail.trim().toLowerCase();
            log.info("Buscando asignación para: {}", normalizedInput);

            // 3. Obtener asignación (correctamente con Optional)
            Optional<UsersAsignation> optionalAssignment = departmentService.getUserDepartmentAssignment(normalizedInput);

            if (optionalAssignment.isEmpty()) {
                log.warn("No se encontró asignación para el usuario: {}", normalizedInput);
                return ResponseEntity.ok(Map.of(
                    "message", "Usuario no encontrado o sin departamento asignado",
                    "status", "NOT_FOUND"
                ));
            }

            UsersAsignation assignment = optionalAssignment.get();
            Department department = assignment.getDepartment();

            if (department == null) {
                log.error("Departamento nulo para la asignación ID: {}", assignment.getId());
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("El departamento asignado no existe"));
            }

            // 4. Obtener estadísticas del departamento
            long totalProjects = projectService.countProjectsByDepartment(department.getId());
            long completedProjects = projectService.countByDepartmentIdAndStatus(department.getId(), ProjectStatus.COMPLETED);
            double totalBudget = department.getBudget() != null ? department.getBudget().doubleValue() : 0.0;
            double budgetUsed = projectService.getTotalBudgetUsedByDepartment(department.getId());

            // 5. Construir respuesta esperada por el frontend
            Map<String, Object> response = Map.of(
                "department", Map.of(
                    "id", department.getId(),
                    "name", department.getName() != null ? department.getName() : "Sin nombre"
                ),
                "totalProjects", totalProjects,
                "completedProjects", completedProjects,
                "totalBudget", totalBudget,
                "budgetUsed", budgetUsed
            );

            log.info("Respuesta exitosa para usuario: {}", normalizedInput);
            return ResponseEntity.ok(response);

        } catch (DataAccessException e) {
            log.error("Error de acceso a datos para usuario: " + usernameOrEmail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error de base de datos",
                    "message", "No se pudo acceder a los datos del usuario"
                ));
        } catch (Exception e) {
            log.error("Error inesperado al obtener asignación para: " + usernameOrEmail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error interno del servidor",
                    "message", "Ocurrió un error inesperado al procesar la solicitud",
                    "details", e.getMessage()
                ));
        }
    }

    /**
     * Obtener todos los usuarios de un departamento
     */
    @GetMapping("/department/{departmentId}/users")
    public ResponseEntity<?> getUsersByDepartment(@PathVariable Long departmentId) {
        try {
            List<UsersAsignation> users = departmentService.getUsersByDepartment(departmentId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error al obtener usuarios del departamento: " + e.getMessage()));
        }
    }


    /**
     * Verificar si un usuario tiene acceso a un departamento
     */
    @GetMapping("/check-access")
    public ResponseEntity<?> checkDepartmentAccess(
            @RequestParam String usernameOrEmail, 
            @RequestParam Long departmentId) {
        try {
            boolean hasAccess = departmentService.hasAccessToDepartment(usernameOrEmail, departmentId);
            return ResponseEntity.ok(new AccessCheckResponse(hasAccess));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error al verificar acceso: " + e.getMessage()));
        }
    }

    /**
     * Transferir usuario a otro departamento
     */
    @PutMapping("/transfer")
    public ResponseEntity<?> transferUserToDepartment(@RequestBody TransferUserRequest request) {
        try {
            UsersAsignation assignment = departmentService.transferUserToDepartment(
                request.getUsernameOrEmail(), 
                request.getNewDepartmentId(), 
                request.getRole()
            );
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error al transferir usuario: " + e.getMessage()));
        }
    }

    // DTOs para las respuestas
    public static class ErrorResponse {
        private String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }

    public static class SuccessResponse {
        private String message;
        
        public SuccessResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class InfoResponse {
        private String info;
        
        public InfoResponse(String info) {
            this.info = info;
        }
        
        public String getInfo() { return info; }
        public void setInfo(String info) { this.info = info; }
    }

    public static class AccessCheckResponse {
        private boolean hasAccess;
        
        public AccessCheckResponse(boolean hasAccess) {
            this.hasAccess = hasAccess;
        }
        
        public boolean isHasAccess() { return hasAccess; }
        public void setHasAccess(boolean hasAccess) { this.hasAccess = hasAccess; }
    }

    public static class TransferUserRequest {
        private String usernameOrEmail;
        private Long newDepartmentId;
        private Role role;
        
        // Getters y setters
        public String getUsernameOrEmail() { return usernameOrEmail; }
        public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
        
        public Long getNewDepartmentId() { return newDepartmentId; }
        public void setNewDepartmentId(Long newDepartmentId) { this.newDepartmentId = newDepartmentId; }
        
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
    }







}