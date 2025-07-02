package com.example.demo.service;
import com.example.demo.dto.request.task.TaskRequestDTO;
import com.example.demo.entity.Project;
import com.example.demo.entity.TaskEntity;
import com.example.demo.entity.TaskEntity.TaskPriority;
import com.example.demo.entity.TaskEntity.TaskStatus;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor  // Lombok genera constructor con campos final
@Slf4j                   // Lombok genera logger automáticamente
@Transactional           // Todas las operaciones son transaccionales por defecto
public class TaskService {

    // Inyección de dependencias automática con @RequiredArgsConstructor
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // ========== OPERACIONES CRUD BÁSICAS ==========

    /**
     * Crear una nueva tarea
     * @param dto - Entidad tarea a crear
     * @return TaskEntity - Tarea creada con ID generado
     */
    public TaskEntity createTask(TaskEntity task) {
        log.info("Creando nueva tarea: {}", task.getName());
        
        // Validar que el proyecto existe
        validateProjectExists(task.getProject().getId());
        
        // Validar usuario asignado si existe
        if (task.getAssignedUser() != null) {
            validateUserExists(task.getAssignedUser().getId());
        }
        
        // Establecer valores por defecto si no vienen
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }
        
        TaskEntity savedTask = taskRepository.save(task);
        log.info("✅ Tarea creada exitosamente con ID: {}", savedTask.getId());
        return savedTask;
    }
    /**
     * Obtener todas las tareas
     * @return List<TaskEntity> - Lista de todas las tareas
     */
    @Transactional(readOnly = true)  // Solo lectura, optimización
    public List<TaskEntity> getAllTasks() {
        log.debug("Obteniendo todas las tareas");
        return taskRepository.findAll();
    }

    /**
     * Obtener tarea por ID
     * @param id - ID de la tarea
     * @return Optional<TaskEntity> - Tarea encontrada o vacío
     */
    @Transactional(readOnly = true)
    public Optional<TaskEntity> getTaskById(Long id) {
        log.debug("Buscando tarea con ID: {}", id);
        return taskRepository.findById(id);
    }

    /**
     * Obtener tarea por ID con excepción si no existe
     * @param id - ID de la tarea
     * @return TaskEntity - Tarea encontrada
     * @throws RuntimeException si no existe
     */
    @Transactional(readOnly = true)
    public TaskEntity getTaskByIdOrThrow(Long id) {
        return getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con ID: " + id));
    }

    /**
     * Actualizar una tarea existente
     * @param id - ID de la tarea a actualizar
     * @param taskUpdate - Datos actualizados
     * @return TaskEntity - Tarea actualizada
     */
    public TaskEntity updateTask(Long id, TaskEntity taskUpdate) {
        log.info("Actualizando tarea con ID: {}", id);
        
        TaskEntity existingTask = getTaskByIdOrThrow(id);
        
        // Actualizar campos no nulos
        if (taskUpdate.getName() != null) {
            existingTask.setName(taskUpdate.getName());
        }
        if (taskUpdate.getDescription() != null) {
            existingTask.setDescription(taskUpdate.getDescription());
        }
        if (taskUpdate.getStatus() != null) {
            existingTask.setStatus(taskUpdate.getStatus());
        }
        if (taskUpdate.getPriority() != null) {
            existingTask.setPriority(taskUpdate.getPriority());
        }
        if (taskUpdate.getStartDate() != null) {
            existingTask.setStartDate(taskUpdate.getStartDate());
        }
        if (taskUpdate.getEndDate() != null) {
            existingTask.setEndDate(taskUpdate.getEndDate());
        }
        if (taskUpdate.getEstimatedHours() != null) {
            existingTask.setEstimatedHours(taskUpdate.getEstimatedHours());
        }
        if (taskUpdate.getActualHours() != null) {
            existingTask.setActualHours(taskUpdate.getActualHours());
        }
        

        TaskEntity savedTask = taskRepository.save(existingTask);
        log.info("Tarea actualizada exitosamente: {}", savedTask.getId());
        return savedTask;
    }

    /**
     * Eliminar una tarea
     * @param id - ID de la tarea a eliminar
     */
    public void deleteTask(Long id) {
        log.info("Eliminando tarea con ID: {}", id);
        
        TaskEntity task = getTaskByIdOrThrow(id);
        taskRepository.delete(task);
        
        log.info("Tarea eliminada exitosamente: {}", id);
    }

    // ========== OPERACIONES DE BÚSQUEDA ==========

    /**
     * Obtener tareas de un proyecto específico
     * @param projectId - ID del proyecto
     * @return List<TaskEntity> - Lista de tareas del proyecto
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> getTasksByProject(Long projectId) {
        log.debug("Obteniendo tareas del proyecto: {}", projectId);
        validateProjectExists(projectId);
        return taskRepository.findByProjectId(projectId);
    }

   
    /**
     * Búsqueda avanzada con filtros múltiples
     * @param projectId - ID del proyecto (opcional)
     * @param status - Estado de la tarea (opcional)
     * @param priority - Prioridad de la tarea (opcional)
     * @param startDate - Fecha inicio mínima (opcional)
     * @param endDate - Fecha fin máxima (opcional)
     * @param keyword - Palabra clave en nombre o descripción (opcional)
     * @return List<TaskEntity> - Lista de tareas filtradas
     */
    
    // ========== OPERACIONES DE ASIGNACIÓN ==========

    /**
     * Asignar tarea a un usuario
     * @param taskId - ID de la tarea
     * @param userId - ID del usuario a asignar
     * @return TaskEntity - Tarea con usuario asignado
     */
    

    /**
     * Desasignar tarea (quitar usuario asignado)
     * @param taskId - ID de la tarea
     * @return TaskEntity - Tarea sin usuario asignado
     */
   
    // ========== OPERACIONES DE ESTADO ==========

    /**
     * Cambiar estado de una tarea
     * @param taskId - ID de la tarea
     * @param newStatus - Nuevo estado
     * @return TaskEntity - Tarea con estado actualizado
     */
    public TaskEntity changeTaskStatus(Long taskId, TaskStatus newStatus) {
        log.info("Cambiando estado de tarea {} a {}", taskId, newStatus);
        
        TaskEntity task = getTaskByIdOrThrow(taskId);
        task.setStatus(newStatus);
        
        TaskEntity savedTask = taskRepository.save(task);
        log.info("Estado de tarea cambiado exitosamente");
        return savedTask;
    }

    // ========== OPERACIONES DE ESTADÍSTICAS ==========

    /**
     * Obtener estadísticas de tareas de un proyecto
     * @param projectId - ID del proyecto
     * @return Map<TaskStatus, Long> - Mapa de estado -> cantidad
     */
    @Transactional(readOnly = true)
    public Map<TaskStatus, Long> getProjectTaskStatistics(Long projectId) {
        log.debug("Obteniendo estadísticas del proyecto: {}", projectId);
        validateProjectExists(projectId);
        
        List<Object[]> results = taskRepository.getProjectTaskStatistics(projectId);
        return results.stream()
                .collect(Collectors.toMap(
                    result -> (TaskStatus) result[0],  // Estado
                    result -> (Long) result[1]         // Cantidad
                ));
    }

    /**
     * Obtener tareas vencidas
     * @return List<TaskEntity> - Lista de tareas vencidas
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> getOverdueTasks() {
        log.debug("Obteniendo tareas vencidas");
        
        // Estados que se consideran no completados
        List<TaskStatus> nonCompletedStatuses = List.of(
            TaskStatus.PENDING, TaskStatus.IN_PROGRESS, TaskStatus.IN_REVIEW
        );
        
        return taskRepository.findOverdueTasks(LocalDateTime.now(), nonCompletedStatuses);
    }

    // ========== MÉTODOS DE VALIDACIÓN PRIVADOS ==========

    /**
     * Validar que existe un proyecto con el ID dado
     * @param projectId - ID del proyecto a validar
     * @throws RuntimeException si no existe
     */
    private void validateProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException("Proyecto no encontrado con ID: " + projectId);
        }
    }


}