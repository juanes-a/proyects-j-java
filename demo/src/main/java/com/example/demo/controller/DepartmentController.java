package com.example.demo.controller;

import com.example.demo.dto.request.department.DepartmentRequestDTO;
import com.example.demo.dto.request.department.DepartmentUpdateDTO;
import com.example.demo.dto.response.DepartmentResponseDTO;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.TeamMemberRepository;
import com.example.demo.entity.Department;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.ActivityService; // NUEVO: Importar ActivityService
import com.example.demo.service.DepartmentService.DepartmentStats;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    private final DepartmentService departmentService;
    
    // NUEVO: Inyección de ActivityService
    @Autowired
    private ActivityService activityService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
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
}