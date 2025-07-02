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
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    // ========== BÚSQUEDAS BÁSICAS ==========
    
    // Buscar todas las tareas de un proyecto específico
    List<TaskEntity> findByProjectId(Long projectId);
    
    // Buscar tareas por estado
    List<TaskEntity> findByStatus(TaskStatus status);
    
    // Buscar tareas por prioridad
    List<TaskEntity> findByPriority(TaskPriority priority);

    // ========== BÚSQUEDAS COMBINADAS ==========
    
    // Buscar tareas de un proyecto con estado específico
    List<TaskEntity> findByProjectIdAndStatus(Long projectId, TaskStatus status);
    

    
    // Buscar tareas de un proyecto con prioridad específica
    List<TaskEntity> findByProjectIdAndPriority(Long projectId, TaskPriority priority);
    


    // ========== BÚSQUEDAS POR FECHAS ==========
    
    // Buscar tareas que vencen antes de una fecha específica
    List<TaskEntity> findByEndDateBefore(LocalDateTime endDate);
    
    // Buscar tareas que vencen entre dos fechas
    List<TaskEntity> findByEndDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Buscar tareas vencidas (fecha de fin pasada y no completadas)
    @Query("SELECT t FROM TaskEntity t WHERE t.endDate < :currentDate AND t.status NOT IN :completedStatuses")
    List<TaskEntity> findOverdueTasks(@Param("currentDate") LocalDateTime currentDate, 
                                    @Param("completedStatuses") List<TaskStatus> completedStatuses);

    // ========== BÚSQUEDAS AVANZADAS ==========
    
    
    // Buscar tareas de un departamento (a través del proyecto)
    @Query("SELECT t FROM TaskEntity t WHERE t.project.department.id = :departmentId")
    List<TaskEntity> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    // Contar tareas por estado en un proyecto
    @Query("SELECT COUNT(t) FROM TaskEntity t WHERE t.project.id = :projectId AND t.status = :status")
    Long countByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TaskStatus status);
    

    // ========== BÚSQUEDA POR FILTROS MÚLTIPLES ==========
    
    // Método principal de búsqueda con filtros múltiples
    @Query("SELECT t FROM TaskEntity t WHERE " +
           "(:projectId IS NULL OR t.project.id = :projectId) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:startDate IS NULL OR t.startDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.endDate <= :endDate) AND " +
           "(:keyword IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<TaskEntity> findByFilters(@Param("projectId") Long projectId,
                                 @Param("status") TaskStatus status,
                                 @Param("priority") TaskPriority priority,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate,
                                 @Param("keyword") String keyword);

    // ========== ESTADÍSTICAS ==========
    
    // Obtener estadísticas de un proyecto
    @Query("SELECT t.status, COUNT(t) FROM TaskEntity t WHERE t.project.id = :projectId GROUP BY t.status")
    List<Object[]> getProjectTaskStatistics(@Param("projectId") Long projectId);
    
  
}