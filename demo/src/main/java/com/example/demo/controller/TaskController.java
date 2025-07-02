package com.example.demo.controller;

import com.example.demo.entity.TaskEntity;
import com.example.demo.entity.TaskEntity.TaskPriority;
import com.example.demo.entity.TaskEntity.TaskStatus;
import com.example.demo.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController                    // Indica que es un controlador REST que devuelve JSON
@RequestMapping("/api/tasks")      // Ruta base: todas las rutas empiezan con /api/tasks
@RequiredArgsConstructor          // Lombok: genera constructor automático para inyección
@Slf4j                           // Lombok: genera logger automático
@CrossOrigin(origins = "*")      // Permite CORS desde cualquier origen (para frontend)
public class TaskController {

    // Inyección automática del servicio
    private final TaskService taskService;

    // ========== ENDPOINTS CRUD BÁSICOS ==========

    /**
     * 🟢 GET /api/tasks - Obtener todas las tareas
     * Ejemplo: GET http://localhost:8080/api/tasks
     */
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
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskEntity> createTask(@RequestBody TaskEntity task) {
        log.info(" POST /api/tasks - Creando nueva tarea: {}", task.getName());
        
        try {
            TaskEntity createdTask = taskService.createTask(task);
            log.info("✅ Tarea creada exitosamente con ID: {}", createdTask.getId());
            
            // 201 Created con la tarea creada
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
            
        } catch (RuntimeException e) {
            log.error("❌ Error de validación al crear tarea: {}", e.getMessage());
            // 400 Bad Request para errores de validación
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            log.error("❌ Error interno al crear tarea: {}", e.getMessage());
            // 500 Internal Server Error
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

    // ========== ENDPOINTS DE ASIGNACIÓN ==========



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
}