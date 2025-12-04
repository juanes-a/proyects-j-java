package com.example.demo.service;
import com.example.demo.dto.request.task.TaskRequestDTO;
import com.example.demo.entity.Project;
import com.example.demo.entity.TaskEntity;
import com.example.demo.entity.TaskEntity.TaskPriority;
import com.example.demo.entity.TaskEntity.TaskStatus;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.UsersAsignation;
import com.example.demo.enums.Role;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UsersAsignationRepository;

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
@Slf4j                  // Lombok genera logger automáticamente
@Transactional           // Todas las operaciones son transaccionales por defecto
public class TaskService {

    // Inyección de dependencias automática con @RequiredArgsConstructor
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UsersAsignationRepository usersAsignationRepository;

    // ========== OPERACIONES CRUD BÁSICAS ==========

    /**
     * Crear una nueva tarea
     * @param dto - Entidad tarea a crear
     * @return TaskEntity - Tarea creada con ID generado
     */
    public TaskEntity createTask(TaskRequestDTO dto) {
        log.info("Creando nueva tarea desde DTO: {}", dto.getName());

        // Validar y obtener el proyecto
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + dto.getProjectId()));

        // Validar y obtener el usuario asignado (si lo hay)
        UserEntity user = null;
        if (dto.getAssignedUserId() != null) {
            user = userRepository.findById(dto.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + dto.getAssignedUserId()));
        }


        // Mapear DTO a entidad
        TaskEntity task = new TaskEntity();
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setStartDate(dto.getStartDate());
        task.setEndDate(dto.getEndDate());
        task.setEstimatedHours(dto.getEstimatedHours());
        task.setActualHours(dto.getActualHours());
        task.setProject(project);
        task.setAssignedUser(user);

        // Aplicar valores por defecto si no vienen
        task.setStatus(dto.getStatus() != null ? TaskStatus.valueOf(dto.getStatus()) : TaskStatus.PENDING);
        task.setPriority(dto.getPriority() != null ? TaskPriority.valueOf(dto.getPriority()) : TaskPriority.MEDIUM);

        // Guardar
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
        

        // Validar y actualizar usuario asignado
        if (taskUpdate.getAssignedUser() != null) {
            validateUserExists(taskUpdate.getAssignedUser().getId());
            existingTask.setAssignedUser(taskUpdate.getAssignedUser());
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
     * Obtener tareas asignadas a un usuario
     * @param userId - ID del usuario
     * @return List<TaskEntity> - Lista de tareas asignadas
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> getTasksByUser(Long userId) {
        log.debug("Obteniendo tareas del usuario: {}", userId);
        validateUserExists(userId);
        return taskRepository.findByAssignedUserId(userId);
    }

    /**
     * Búsqueda avanzada con filtros múltiples
     * @param projectId - ID del proyecto (opcional)
     * @param assignedUserId - ID del usuario asignado (opcional)
     * @param status - Estado de la tarea (opcional)
     * @param priority - Prioridad de la tarea (opcional)
     * @param startDate - Fecha inicio mínima (opcional)
     * @param endDate - Fecha fin máxima (opcional)
     * @param keyword - Palabra clave en nombre o descripción (opcional)
     * @return List<TaskEntity> - Lista de tareas filtradas
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> searchTasks(Long projectId, Long assignedUserId, 
                                      TaskStatus status, TaskPriority priority,
                                      LocalDateTime startDate, LocalDateTime endDate, 
                                      String keyword) {
        log.debug("Búsqueda de tareas con filtros - Proyecto: {}, Usuario: {}, Estado: {}", 
                 projectId, assignedUserId, status);
        
        return taskRepository.findByFilters(projectId, assignedUserId, status, priority, 
                                          startDate, endDate, keyword);
    }

    // ========== OPERACIONES DE ASIGNACIÓN ==========

    /**
     * Asignar tarea a un usuario
     */
    public UsersAsignation assignUserToTask(String usernameOrEmail, Long taskId, Role role) {
        // Validar usuario
        UserEntity user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado: " + usernameOrEmail));

        // Validar tarea
        TaskEntity taskEntity = taskRepository.findById(taskId)
            .orElseThrow(() -> new BusinessException("Tarea no encontrada con ID: " + taskId));

        // Asignar rol (si aplica cambiarlo)
        user.setRole(role);
        userRepository.save(user);

        // Verificar si ya está asignado a esta tarea (único caso que no permites)
        Optional<UsersAsignation> existingAssignment = usersAsignationRepository
            .findByUserAndTask(user, taskEntity);

        if (existingAssignment.isPresent()) {
            throw new BusinessException("El usuario ya está asignado a esta tarea.");
        }

        // Crear asignación
        UsersAsignation assignment = new UsersAsignation();
        assignment.setUser(user);
        assignment.setTask(taskEntity);
        assignment.setRolAsignado(role);
        assignment.setDateAsignDateTime(LocalDateTime.now());

        log.info("Asignando usuario a la tarea {}...", taskEntity.getId());
        UsersAsignation saved = usersAsignationRepository.save(assignment);
        usersAsignationRepository.flush();

        log.info("Asignación guardada: {}", saved);
        return saved;
    }


    /**
     * Desasignar tarea (quitar usuario asignado)
     * @param taskId - ID de la tarea
     * @return TaskEntity - Tarea sin usuario asignado
     */
    public TaskEntity unassignTask(Long taskId) {
        log.info("Desasignando tarea: {}", taskId);
        
        TaskEntity task = getTaskByIdOrThrow(taskId);
        task.setAssignedUser(null);
        
        // Cambiar estado a pendiente si estaba en progreso
        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            task.setStatus(TaskStatus.PENDING);
        }
        
        TaskEntity savedTask = taskRepository.save(task);
        log.info("Tarea desasignada exitosamente");
        return savedTask;
    }

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

    /**
     * Validar que existe un usuario con el ID dado
     * @param userId - ID del usuario a validar
     * @throws RuntimeException si no existe
     */
    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }
    }
}