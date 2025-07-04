package com.example.demo.controller;

import com.example.demo.dto.request.project.AssignUserToProjectRequest;
import com.example.demo.dto.request.task.AssignUserToTaskRequest;
import com.example.demo.dto.request.task.TaskRequestDTO;
import com.example.demo.dto.response.TaskResponse;
import com.example.demo.entity.TaskEntity;
import com.example.demo.entity.TaskEntity.TaskPriority;
import com.example.demo.entity.TaskEntity.TaskStatus;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.UsersAsignation;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UsersAsignationRepository;
import com.example.demo.service.TaskService;

import com.example.demo.util.PdfReportGenerator;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController                    // Indica que es un controlador REST que devuelve JSON
@RequestMapping("/api/tasks")      // Ruta base: todas las rutas empiezan con /api/tasks
@RequiredArgsConstructor          // Lombok: genera constructor automático para inyección
@Slf4j                           // Lombok: genera logger automático
@CrossOrigin(origins = "*")      // Permite CORS desde cualquier origen (para frontend)
public class TaskController {

    // Inyección automática del servicio
    private final TaskService taskService;
    private final UserRepository userRepository;
    private final UsersAsignationRepository usersAsignationRepository;


    // ========== ENDPOINTS CRUD BÁSICOS ==========

    /**
     * 🟢 GET /api/tasks - Obtener todas las tareas
     * Ejemplo: GET http://localhost:8080/api/tasks
     */
    @GetMapping("/report")
    public ResponseEntity<byte[]> generateTaskReport(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long assignedUserId,
            @RequestParam(required = false) TaskEntity.TaskStatus status,
            @RequestParam(required = false) TaskEntity.TaskPriority priority,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String keyword) {

        log.info("📄 Generando reporte PDF con filtros: proyecto={}, usuario={}, estado={}",
                projectId, assignedUserId, status);

        try {
            List<TaskEntity> filteredTasks = taskService.searchTasks(
                    projectId, assignedUserId, status, priority, startDate, endDate, keyword);

            ByteArrayInputStream pdfStream = PdfReportGenerator.generateTaskReport(filteredTasks);
            byte[] pdfBytes = pdfStream.readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=reporte_tareas.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("❌ Error al generar el reporte PDF: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping
    public ResponseEntity<List<TaskEntity>> getAllTasks() {
        log.info("📋 GET /api/tasks - Obteniendo todas las tareas");

        try {
            List<TaskEntity> tasks = taskService.getAllTasks();
            log.info("✅ Se encontraron {} tareas", tasks.size());

            // 200 OK con la lista de tareas
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            log.error("❌ Error al obtener tareas: {}", e.getMessage());
            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🟢 GET /api/tasks/{id} - Obtener tarea por ID
     * Ejemplo: GET http://localhost:8080/api/tasks/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskEntity> getTaskById(@PathVariable Long id) {
        log.info("🔍 GET /api/tasks/{} - Buscando tarea por ID", id);

        try {
            Optional<TaskEntity> task = taskService.getTaskById(id);

            if (task.isPresent()) {
                log.info("✅ Tarea encontrada: {}", task.get().getName());
                // 200 OK con la tarea
                return ResponseEntity.ok(task.get());
            } else {
                log.warn("⚠️ Tarea no encontrada con ID: {}", id);
                // 404 Not Found
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            log.error("❌ Error al buscar tarea {}: {}", id, e.getMessage());
            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🟡 POST /api/tasks - Crear nueva tarea
     * Ejemplo: POST http://localhost:8080/api/tasks
     * Body JSON: {
     *   "name": "Nueva tarea",
     *   "description": "Descripción",
     *   "project": {"id": 1},
     *   "priority": "HIGH"
     * }
     */
    @PostMapping
    public ResponseEntity<TaskEntity> createTask(@RequestBody TaskRequestDTO dto) {
        log.info(" POST /api/tasks - Creando nueva tarea desde DTO: {}", dto.getName());

        try {
            TaskEntity createdTask = taskService.createTask(dto);
            log.info("✅ Tarea creada exitosamente con ID: {}", createdTask.getId());

            // 201 Created con la tarea creada
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);

        } catch (IllegalArgumentException e) {
            log.error("❌ Error de validación al crear tarea: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.error("❌ Error interno al crear tarea: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🔵 PUT /api/tasks/{id} - Actualizar tarea completa
     * Ejemplo: PUT http://localhost:8080/api/tasks/1
     * Body JSON: {"name": "Tarea actualizada", "status": "COMPLETED"}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskEntity> updateTask(@PathVariable Long id,
                                               @RequestBody TaskEntity taskUpdate) {
        log.info("✏️ PUT /api/tasks/{} - Actualizando tarea", id);

        try {
            TaskEntity updatedTask = taskService.updateTask(id, taskUpdate);
            log.info("✅ Tarea {} actualizada exitosamente", id);

            // 200 OK con la tarea actualizada
            return ResponseEntity.ok(updatedTask);

        } catch (RuntimeException e) {
            log.error("❌ Error al actualizar tarea {}: {}", id, e.getMessage());
            // 404 Not Found si no existe la tarea
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("❌ Error interno al actualizar tarea {}: {}", id, e.getMessage());
            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🔴 DELETE /api/tasks/{id} - Eliminar tarea
     * Ejemplo: DELETE http://localhost:8080/api/tasks/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("🗑️ DELETE /api/tasks/{} - Eliminando tarea", id);

        try {
            taskService.deleteTask(id);
            log.info("✅ Tarea {} eliminada exitosamente", id);

            // 204 No Content (eliminación exitosa sin contenido)
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            log.error("❌ Error al eliminar tarea {}: {}", id, e.getMessage());
            // 404 Not Found si no existe la tarea
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("❌ Error interno al eliminar tarea {}: {}", id, e.getMessage());
            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== ENDPOINTS DE BÚSQUEDA ==========

    /**
     * 🟢 GET /api/tasks/project/{projectId} - Tareas de un proyecto
     * Ejemplo: GET http://localhost:8080/api/tasks/project/1
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskEntity>> getTasksByProject(@PathVariable Long projectId) {
        log.info("📁 GET /api/tasks/project/{} - Obteniendo tareas del proyecto", projectId);

        try {
            List<TaskEntity> tasks = taskService.getTasksByProject(projectId);
            log.info("✅ Se encontraron {} tareas en el proyecto {}", tasks.size(), projectId);

            return ResponseEntity.ok(tasks);

        } catch (RuntimeException e) {
            log.error("❌ Error: proyecto {} no encontrado", projectId);
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("❌ Error al obtener tareas del proyecto {}: {}", projectId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🟢 GET /api/tasks/user/{userId} - Tareas asignadas a un usuario
     * Ejemplo: GET http://localhost:8080/api/tasks/user/1
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskEntity>> getTasksByUser(@PathVariable Long userId) {
        log.info("👤 GET /api/tasks/user/{} - Obteniendo tareas del usuario", userId);

        try {
            List<TaskEntity> tasks = taskService.getTasksByUser(userId);
            log.info("✅ Se encontraron {} tareas asignadas al usuario {}", tasks.size(), userId);

            return ResponseEntity.ok(tasks);

        } catch (RuntimeException e) {
            log.error("❌ Error: usuario {} no encontrado", userId);
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("❌ Error al obtener tareas del usuario {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🟢 GET /api/tasks/search - Búsqueda avanzada con filtros
     * Ejemplo: GET http://localhost:8080/api/tasks/search?projectId=1&status=PENDING&priority=HIGH
     * Parámetros opcionales:
     * - projectId: ID del proyecto
     * - assignedUserId: ID del usuario asignado
     * - status: PENDING, IN_PROGRESS, IN_REVIEW, COMPLETED, CANCELLED
     * - priority: LOW, MEDIUM, HIGH, URGENT
     * - startDate: fecha inicio (formato: 2024-01-15T10:30:00)
     * - endDate: fecha fin (formato: 2024-01-15T10:30:00)
     * - keyword: palabra clave en nombre o descripción
     */
    @GetMapping("/search")
    public ResponseEntity<List<TaskEntity>> searchTasks(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long assignedUserId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String keyword) {

        log.info("🔍 GET /api/tasks/search - Búsqueda con filtros: proyecto={}, usuario={}, estado={}",
                projectId, assignedUserId, status);

        try {
            List<TaskEntity> tasks = taskService.searchTasks(
                projectId, assignedUserId, status, priority, startDate, endDate, keyword);

            log.info("✅ Búsqueda completada. Se encontraron {} tareas", tasks.size());
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            log.error("❌ Error en búsqueda de tareas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // ========== ENDPOINTS DE ASIGNACIÓN ==========

        /*Asignar tareas */
    @PostMapping("/assign-task")
    public ResponseEntity<?> assignUserToTask(@RequestBody AssignUserToTaskRequest request) {
        log.info("Datos recibidos en /task/assign: {}", request);

        try {
            UsersAsignation assignment = taskService.assignUserToTask(
                request.getUsernameOrEmail(),
                request.getTaskId(),
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

    /**
     * 🔵 PUT /api/tasks/{taskId}/unassign - Desasignar tarea (quitar usuario)
     * Ejemplo: PUT http://localhost:8080/api/tasks/1/unassign
     */
    @PutMapping("/{taskId}/unassign")
    public ResponseEntity<TaskEntity> unassignTask(@PathVariable Long taskId) {
        log.info("🚫 PUT /api/tasks/{}/unassign - Desasignando tarea", taskId);

        try {
            TaskEntity unassignedTask = taskService.unassignTask(taskId);
            log.info("✅ Tarea {} desasignada exitosamente", taskId);

            return ResponseEntity.ok(unassignedTask);

        } catch (RuntimeException e) {
            log.error("❌ Error al desasignar tarea {}: {}", taskId, e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("❌ Error interno al desasignar tarea {}: {}", taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== ENDPOINTS DE ESTADO ==========

    /**
     * 🔵 PUT /api/tasks/{taskId}/status - Cambiar estado de tarea
     * Ejemplo: PUT http://localhost:8080/api/tasks/1/status?status=COMPLETED
     */
    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskEntity> changeTaskStatus(@PathVariable Long taskId,
                                                      @RequestParam TaskStatus status) {
        log.info("🔄 PUT /api/tasks/{}/status - Cambiando estado a: {}", taskId, status);

        try {
            TaskEntity updatedTask = taskService.changeTaskStatus(taskId, status);
            log.info("✅ Estado de tarea {} cambiado a {} exitosamente", taskId, status);

            return ResponseEntity.ok(updatedTask);

        } catch (RuntimeException e) {
            log.error("❌ Error al cambiar estado de tarea {}: {}", taskId, e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("❌ Error interno al cambiar estado de tarea {}: {}", taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== ENDPOINTS DE ESTADÍSTICAS ==========

    /**
     * 🟢 GET /api/tasks/project/{projectId}/statistics - Estadísticas de proyecto
     * Ejemplo: GET http://localhost:8080/api/tasks/project/1/statistics
     * Respuesta JSON: {
     *   "PENDING": 3,
     *   "IN_PROGRESS": 2,
     *   "COMPLETED": 5
     * }
     */
    @GetMapping("/project/{projectId}/statistics")
    public ResponseEntity<Map<TaskStatus, Long>> getProjectStatistics(@PathVariable Long projectId) {
        log.info("📊 GET /api/tasks/project/{}/statistics - Obteniendo estadísticas", projectId);

        try {
            Map<TaskStatus, Long> statistics = taskService.getProjectTaskStatistics(projectId);
            log.info("✅ Estadísticas del proyecto {} obtenidas: {} estados", projectId, statistics.size());

            return ResponseEntity.ok(statistics);

        } catch (RuntimeException e) {
            log.error("❌ Error: proyecto {} no encontrado", projectId);
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("❌ Error al obtener estadísticas del proyecto {}: {}", projectId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    /**
     * 🟢 GET /api/tasks/overdue - Obtener tareas vencidas
     * Ejemplo: GET http://localhost:8080/api/tasks/overdue
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskEntity>> getOverdueTasks() {
        log.info("⏰ GET /api/tasks/overdue - Obteniendo tareas vencidas");

        try {
            List<TaskEntity> overdueTasks = taskService.getOverdueTasks();
            log.info("✅ Se encontraron {} tareas vencidas", overdueTasks.size());

            return ResponseEntity.ok(overdueTasks);

        } catch (Exception e) {
            log.error("❌ Error al obtener tareas vencidas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    /*Cargar tareas por user */

        @GetMapping("assigned-tasks/{usernameOrEmail}")
    public ResponseEntity<?> getAssignedTasks(@PathVariable @NotBlank String usernameOrEmail) {
        try {
            if (usernameOrEmail.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "El username/email no puede estar vacío"));
            }

            String normalizedInput = usernameOrEmail.trim().toLowerCase();

            // Buscar usuario
            UserEntity user = userRepository.findByUsernameOrEmail(normalizedInput, normalizedInput)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            // Buscar todas las asignaciones a tareas de este usuario
            List<UsersAsignation> assignments = usersAsignationRepository.findAssignedTasks(user);

            // Obtener las tareas asignadas (evitar duplicados si los hay)
            List<TaskEntity> taskList = assignments.stream()
                .map(UsersAsignation::getTask)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

            // Convertir a DTOs
            List<TaskResponse> taskResponses = taskList.stream()
                .map(TaskResponse::fromEntity)
                .toList();

            // Respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("user", user.getUsername());
            response.put("assignedTasks", taskResponses);

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error en getAssignedTasks: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("message", "Error interno del servidor"));
        }
    }

}

// Simple error response class for error messages
class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}