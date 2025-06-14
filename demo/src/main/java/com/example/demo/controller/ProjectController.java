package com.example.demo.controller;

import com.example.demo.entity.Project;
import com.example.demo.enums.ProjectStatus;
import com.example.demo.enums.ProjectPriority;
import com.example.demo.service.ProjectService;
import com.example.demo.exception.ProjectNotFoundException;
import com.example.demo.exception.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * CONTROLLER PARA GESTIÓN DE PROYECTOS
 *
 * Este controller maneja TODAS las operaciones HTTP relacionadas con proyectos.
 * Está diseñado para funcionar PERFECTAMENTE con Postman usando JSON.
 *
 * ENDPOINTS PRINCIPALES:
 * - POST /api/projects -> Crear proyecto
 * - GET /api/projects -> Listar todos los proyectos activos
 * - GET /api/projects/{id} -> Obtener proyecto por ID
 * - PUT /api/projects/{id} -> Actualizar proyecto
 * - DELETE /api/projects/{id} -> Eliminar proyecto
 *
 * ENDPOINTS DE GESTIÓN DE ESTADO:
 * - POST /api/projects/{id}/start -> Iniciar proyecto
 * - POST /api/projects/{id}/complete -> Completar proyecto
 * - POST /api/projects/{id}/cancel -> Cancelar proyecto
 *
 * ENDPOINTS DE CONSULTAS ESPECIALIZADAS:
 * - GET /api/projects/department/{departmentId} -> Proyectos por departamento
 * - GET /api/projects/overdue -> Proyectos vencidos
 * - GET /api/projects/urgent -> Proyectos urgentes
 * - GET /api/projects/ending-this-week -> Proyectos que terminan esta semana
 * - GET /api/projects/search -> Búsqueda con filtros múltiples
 *
 * ENDPOINTS DE ESTADÍSTICAS:
 * - GET /api/projects/statistics/department/{departmentId} -> Stats por departamento
 * - GET /api/projects/summary -> Resumen general
 */
@RestController
@RequestMapping("/api/projects")
@Validated
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // ========================================
    // OPERACIONES CRUD BÁSICAS
    // ========================================

    /**
     * CREAR PROYECTO
     *
     * POST /api/projects
     *
     * JSON EJEMPLO PARA POSTMAN:
     * {
     *   "name": "Sistema de Inventario",
     *   "description": "Desarrollo de sistema web para gestión de inventario",
     *   "objectives": "Automatizar el control de stock y generar reportes",
     *   "departmentId": 1,
     *   "startDate": "2024-02-01",
     *   "endDate": "2024-05-30",
     *   "budget": 25000.00,
     *   "priority": "HIGH"
     * }
     */
    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody Map<String, Object> projectData) {
        try {
            // EXTRAER DATOS DEL JSON
            String name = (String) projectData.get("name");
            String description = (String) projectData.get("description");
            String objectives = (String) projectData.get("objectives");
            Long departmentId = Long.valueOf(projectData.get("departmentId").toString());
            LocalDate startDate = LocalDate.parse((String) projectData.get("startDate"));
            LocalDate endDate = LocalDate.parse((String) projectData.get("endDate"));


            // CAMPOS OPCIONALES
            BigDecimal budget = null;
            if (projectData.get("budget") != null) {
                budget = new BigDecimal(projectData.get("budget").toString());
            }
            ProjectStatus status = ProjectStatus.PLANNED;
            if (projectData.get("status") != null) {
                status = ProjectStatus.valueOf(projectData.get("status").toString());
            }

            ProjectPriority priority = ProjectPriority.MEDIUM; // DEFAULT
            if (projectData.get("priority") != null) {
                priority = ProjectPriority.valueOf((String) projectData.get("priority"));
            }

            // CREAR PROYECTO USANDO EL SERVICE
            Project project = projectService.createProject(name, description, objectives,
                    departmentId, startDate, endDate,
                    budget, priority, status);

            return ResponseEntity.status(HttpStatus.CREATED).body(project);

        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("BUSINESS_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("VALIDATION_ERROR",
                    "Error en los datos proporcionados: " + e.getMessage()));
        }
    }

    /**
     * OBTENER TODOS LOS PROYECTOS ACTIVOS
     *
     * GET /api/projects
     */
    @GetMapping
    public ResponseEntity<List<Project>> getAllActiveProjects() {
        List<Project> projects = projectService.getAllActiveProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * OBTENER PROYECTO POR ID
     *
     * GET /api/projects/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        try {
            Project project = projectService.getProjectById(id);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ACTUALIZAR PROYECTO
     *
     * PUT /api/projects/{id}
     *
     * JSON EJEMPLO PARA POSTMAN (mismo formato que CREATE):
     * {
     *   "name": "Sistema de Inventario v2",
     *   "description": "Desarrollo mejorado con nuevas funcionalidades",
     *   "objectives": "Automatizar y agregar módulo de reportes avanzados",
     *   "startDate": "2024-02-01",
     *   "endDate": "2024-06-30",
     *   "budget": 30000.00,
     *   "priority": "CRITICAL"
     * }
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id,
                                           @Valid @RequestBody Map<String, Object> projectData) {
        try {
            // EXTRAER DATOS DEL JSON
            String name = (String) projectData.get("name");
            String description = (String) projectData.get("description");
            String objectives = (String) projectData.get("objectives");
            LocalDate startDate = LocalDate.parse((String) projectData.get("startDate"));
            LocalDate endDate = LocalDate.parse((String) projectData.get("endDate"));

            // CAMPOS OPCIONALES
            BigDecimal budget = null;
            if (projectData.get("budget") != null) {
                budget = new BigDecimal(projectData.get("budget").toString());
            }

            ProjectPriority priority = ProjectPriority.MEDIUM;
            if (projectData.get("priority") != null) {
                priority = ProjectPriority.valueOf((String) projectData.get("priority"));
            }

            // ACTUALIZAR USANDO EL SERVICE
            Project project = projectService.updateProject(id, name, description, objectives,
                    startDate, endDate, budget, priority);

            return ResponseEntity.ok(project);

        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("BUSINESS_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("VALIDATION_ERROR",
                    "Error en los datos proporcionados: " + e.getMessage()));
        }
    }

    /**
     * ELIMINAR PROYECTO
     *
     * DELETE /api/projects/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok(createSuccessResponse("Proyecto eliminado exitosamente"));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("BUSINESS_ERROR", e.getMessage()));
        }
    }

    // ========================================
    // GESTIÓN DEL CICLO DE VIDA DEL PROYECTO
    // ========================================

    /**
     * INICIAR PROYECTO
     *
     * POST /api/projects/{id}/start
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<?> startProject(@PathVariable Long id) {
        try {
            Project project = projectService.startProject(id);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("BUSINESS_ERROR", e.getMessage()));
        }
    }

    /**
     * COMPLETAR PROYECTO
     *
     * POST /api/projects/{id}/complete
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeProject(@PathVariable Long id) {
        try {
            Project project = projectService.completeProject(id);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("BUSINESS_ERROR", e.getMessage()));
        }
    }

    /**
     * CANCELAR PROYECTO
     *
     * POST /api/projects/{id}/cancel
     *
     * JSON OPCIONAL PARA POSTMAN:
     * {
     *   "reason": "Falta de presupuesto"
     * }
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelProject(@PathVariable Long id,
                                           @RequestBody(required = false) Map<String, Object> requestData) {
        try {
            String reason = null;
            if (requestData != null && requestData.get("reason") != null) {
                reason = (String) requestData.get("reason");
            }

            Project project = projectService.cancelProject(id, reason);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("BUSINESS_ERROR", e.getMessage()));
        }
    }

    // ========================================
    // CONSULTAS ESPECIALIZADAS
    // ========================================

    /**
     * OBTENER PROYECTOS POR DEPARTAMENTO
     *
     * GET /api/projects/department/{departmentId}
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<?> getProjectsByDepartment(@PathVariable Long departmentId) {
        try {
            List<Project> projects = projectService.getProjectsByDepartment(departmentId);
            return ResponseEntity.ok(projects);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("BUSINESS_ERROR", e.getMessage()));
        }
    }

    /**
     * OBTENER PROYECTOS VENCIDOS
     *
     * GET /api/projects/overdue
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<Project>> getOverdueProjects() {
        List<Project> projects = projectService.getOverdueProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * OBTENER PROYECTOS URGENTES ACTIVOS
     *
     * GET /api/projects/urgent
     */
    @GetMapping("/urgent")
    public ResponseEntity<List<Project>> getUrgentActiveProjects() {
        List<Project> projects = projectService.getUrgentActiveProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * OBTENER PROYECTOS QUE TERMINAN ESTA SEMANA
     *
     * GET /api/projects/ending-this-week
     */
    @GetMapping("/ending-this-week")
    public ResponseEntity<List<Project>> getProjectsEndingThisWeek() {
        List<Project> projects = projectService.getProjectsEndingThisWeek();
        return ResponseEntity.ok(projects);
    }

    /**
     * BÚSQUEDA CON FILTROS MÚLTIPLES
     *
     * GET /api/projects/search?name=Sistema&departmentId=1&status=IN_PROGRESS&priority=HIGH
     *
     * PARÁMETROS OPCIONALES:
     * - name: Buscar por nombre (texto parcial)
     * - departmentId: Filtrar por departamento
     * - status: PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
     * - priority: LOW, MEDIUM, HIGH, CRITICAL
     * - startDate: Fecha inicio desde (yyyy-MM-dd)
     * - endDate: Fecha fin hasta (yyyy-MM-dd)
     * - minBudget: Presupuesto mínimo
     * - maxBudget: Presupuesto máximo
     */
    @GetMapping("/search")
    public ResponseEntity<List<Project>> searchProjects(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) BigDecimal minBudget,
            @RequestParam(required = false) BigDecimal maxBudget) {

        // CONVERTIR STRINGS A ENUMS
        ProjectStatus projectStatus = null;
        if (status != null) {
            projectStatus = ProjectStatus.valueOf(status.toUpperCase());
        }

        ProjectPriority projectPriority = null;
        if (priority != null) {
            projectPriority = ProjectPriority.valueOf(priority.toUpperCase());
        }

        // CONVERTIR FECHAS
        LocalDate parsedStartDate = null;
        if (startDate != null) {
            parsedStartDate = LocalDate.parse(startDate);
        }

        LocalDate parsedEndDate = null;
        if (endDate != null) {
            parsedEndDate = LocalDate.parse(endDate);
        }

        // EJECUTAR BÚSQUEDA
        List<Project> projects = projectService.searchProjects(name, departmentId, projectStatus,
                projectPriority, parsedStartDate, parsedEndDate,
                minBudget, maxBudget);

        return ResponseEntity.ok(projects);
    }

    // ========================================
    // ESTADÍSTICAS Y REPORTES
    // ========================================

    /**
     * OBTENER ESTADÍSTICAS POR DEPARTAMENTO
     *
     * GET /api/projects/statistics/department/{departmentId}
     */
    @GetMapping("/statistics/department/{departmentId}")
    public ResponseEntity<?> getProjectStatisticsByDepartment(@PathVariable Long departmentId) {
        try {
            var statistics = projectService.getProjectStatisticsByDepartment(departmentId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("ERROR", e.getMessage()));
        }
    }

    /**
     * OBTENER RESUMEN GENERAL DE PROYECTOS
     *
     * GET /api/projects/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getProjectSummary() {
        try {
            var summary = projectService.getProjectSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("ERROR", e.getMessage()));
        }
    }

    // ========================================
    // ENDPOINTS DE UTILIDAD PARA POSTMAN
    // ========================================

    /**
     * OBTENER TODOS LOS ESTADOS DISPONIBLES
     *
     * GET /api/projects/enums/status
     *
     * ÚTIL PARA SABER QUE VALORES USAR EN POSTMAN
     */
    @GetMapping("/enums/status")
    public ResponseEntity<ProjectStatus[]> getProjectStatusValues() {
        return ResponseEntity.ok(ProjectStatus.values());
    }

    /**
     * OBTENER TODAS LAS PRIORIDADES DISPONIBLES
     *
     * GET /api/projects/enums/priority
     */
    @GetMapping("/enums/priority")
    public ResponseEntity<ProjectPriority[]> getProjectPriorityValues() {
        return ResponseEntity.ok(ProjectPriority.values());
    }

    // ========================================
    // MÉTODOS DE APOYO PARA RESPUESTAS JSON
    // ========================================

    /**
     * Crear respuesta de error consistente para Postman
     */
    private Map<String, Object> createErrorResponse(String errorType, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("errorType", errorType);
        error.put("message", message);
        error.put("timestamp", java.time.LocalDateTime.now());
        return error;
    }

    /**
     * Crear respuesta de éxito consistente para Postman
     */
    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", java.time.LocalDateTime.now());
        return response;
    }
}