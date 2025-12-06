package com.example.demo.repository;

import com.example.demo.entity.TaskEntity;
import com.example.demo.entity.TaskEntity.TaskPriority;
import com.example.demo.entity.TaskEntity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    // ========== BÚSQUEDAS BÁSICAS ==========
    
    // Buscar todas las tareas de un proyecto específico
    @Query("SELECT t FROM TaskEntity t WHERE t.project.id = :projectId")
    List<TaskEntity> findByProjectId(@Param("projectId") Long projectId);

    
    // CORREGIDO: Usamos assignedUser_Id para evitar conflicto con el getter del Entity
    List<TaskEntity> findByAssignedUser_Id(Long userId);
    
    // Buscar tareas por estado
    List<TaskEntity> findByStatus(TaskStatus status);
    
    // Buscar tareas por prioridad
    List<TaskEntity> findByPriority(TaskPriority priority);

    // ========== BÚSQUEDAS COMBINADAS ==========

    
    // Buscar tareas de un proyecto con estado específico
    List<TaskEntity> findByProjectIdAndStatus(Long projectId, TaskStatus status);
    
    // CORREGIDO: assignedUser_Id
    List<TaskEntity> findByAssignedUser_IdAndStatus(Long userId, TaskStatus status);
    
    // Buscar tareas de un proyecto con prioridad específica
    List<TaskEntity> findByProjectIdAndPriority(Long projectId, TaskPriority priority);
    
    // CORREGIDO: assignedUser_Id
    List<TaskEntity> findByAssignedUser_IdAndPriority(Long userId, TaskPriority priority);

    // ========== BÚSQUEDAS POR FECHAS ==========
    
    List<TaskEntity> findByEndDateBefore(LocalDateTime endDate);
    
    List<TaskEntity> findByEndDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM TaskEntity t WHERE t.endDate < :currentDate AND t.status NOT IN :completedStatuses")
    List<TaskEntity> findOverdueTasks(@Param("currentDate") LocalDateTime currentDate, 
                                    @Param("completedStatuses") List<TaskStatus> completedStatuses);

    // ========== BÚSQUEDAS AVANZADAS ==========
    
    List<TaskEntity> findByProjectIdAndAssignedUserIsNull(Long projectId);
    
    @Query("SELECT t FROM TaskEntity t WHERE t.project.department.id = :departmentId")
    List<TaskEntity> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    @Query("SELECT COUNT(t) FROM TaskEntity t WHERE t.project.id = :projectId AND t.status = :status")
    Long countByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TaskStatus status);
    
    // Este usa @Query, así que NO necesita cambiarse, ya es explícito
    @Query("SELECT COUNT(t) FROM TaskEntity t WHERE t.assignedUser.id = :userId AND t.status = :status")
    Long countByAssignedUserIdAndStatus(@Param("userId") Long userId, @Param("status") TaskStatus status);

    // ========== BÚSQUEDA POR FILTROS MÚLTIPLES ==========
    
    @Query("SELECT t FROM TaskEntity t WHERE " +
           "(:projectId IS NULL OR t.project.id = :projectId) AND " +
           "(:assignedUserId IS NULL OR t.assignedUser.id = :assignedUserId) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:startDate IS NULL OR t.startDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.endDate <= :endDate) AND " +
           "(:keyword IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<TaskEntity> findByFilters(@Param("projectId") Long projectId,
                                 @Param("assignedUserId") Long assignedUserId,
                                 @Param("status") TaskStatus status,
                                 @Param("priority") TaskPriority priority,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate,
                                 @Param("keyword") String keyword);

    // ========== ESTADÍSTICAS ==========
    
    @Query("SELECT t.status, COUNT(t) FROM TaskEntity t WHERE t.project.id = :projectId GROUP BY t.status")
    List<Object[]> getProjectTaskStatistics(@Param("projectId") Long projectId);
}