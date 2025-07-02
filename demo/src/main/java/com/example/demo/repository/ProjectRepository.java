package com.example.demo.repository;

import com.example.demo.entity.Project;
import com.example.demo.enums.ProjectPriority;
import com.example.demo.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

// Método para encontrar proyectos por prioridades y estado diferente al especificado
    @Query("SELECT p FROM Project p WHERE p.priority IN :priorities AND p.status != :status")
    List<Project> findByPriorityInAndStatusNot(
        @Param("priorities") List<ProjectPriority> priorities,
        @Param("status") ProjectStatus status
    );

    // Método para encontrar proyectos con fecha de fin anterior y estado diferente al especificado
    @Query("SELECT p FROM Project p WHERE p.endDate < :date AND p.status != :status")
    List<Project> findByEndDateBeforeAndStatusNot(
        @Param("date") LocalDate date,
        @Param("status") ProjectStatus status
    );

    // Método alternativo para proyectos urgentes (HIGH y CRITICAL) que no están completados
    @Query("SELECT p FROM Project p WHERE p.priority IN ('HIGH', 'CRITICAL') AND p.status != 'COMPLETED'")
    List<Project> findUrgentProjects();

    // Método alternativo para proyectos vencidos que no están completados
    @Query("SELECT p FROM Project p WHERE p.endDate < CURRENT_DATE AND p.status != 'COMPLETED'")
    List<Project> findOverdueProjects();
        


    // ============== Consultas básicas ==============
    Optional<Project> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndDepartmentId(String name, Long departmentId);

    // ============== Consultas por estado ==============
    List<Project> findByStatus(ProjectStatus status);
    
    @Query("SELECT p FROM Project p WHERE p.status != 'CANCELLED'")
    List<Project> findActiveProjects();
    
    @Query("SELECT p FROM Project p WHERE p.status = 'COMPLETED' ORDER BY p.endDate DESC")
    List<Project> findCompletedProjectsOrdered();

    // ============== Consultas por prioridad ==============
    List<Project> findByPriority(ProjectPriority priority);
    
    @Query("SELECT p FROM Project p WHERE p.priority IN :priorities AND p.status != 'CANCELLED'")
    List<Project> findByPriorityInAndStatusNotCancelled(@Param("priorities") List<ProjectPriority> priorities);
    
    List<Project> findByPriorityAndStatus(ProjectPriority priority, ProjectStatus status);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.priority = :priority")
    long countByPriority(@Param("priority") ProjectPriority priority);

    // ============== Consultas por departamento ==============
    @Query("SELECT p FROM Project p WHERE p.department.id = :departmentId")
    List<Project> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    @Query("SELECT p FROM Project p WHERE p.department.id = :departmentId AND p.status != 'CANCELLED'")
    List<Project> findActiveProjectsByDepartment(@Param("departmentId") Long departmentId);
    
    List<Project> findByDepartmentIdAndStatus(Long departmentId, ProjectStatus status);


    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.department.id = :departmentId AND p.status != 'CANCELLED'")
    long countActiveProjectsByDepartment(@Param("departmentId") Long departmentId);

    // ============== Consultas por fechas ==============
    List<Project> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    List<Project> findByStartDate(LocalDate startDate);
    
    @Query("SELECT p FROM Project p WHERE p.endDate < :currentDate AND p.status = 'IN_PROGRESS'")
    List<Project> findOverdueProjects(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT p FROM Project p WHERE p.endDate BETWEEN :today AND :endOfWeek")
    List<Project> findProjectsEndingThisWeek(
        @Param("today") LocalDate today,
        @Param("endOfWeek") LocalDate endOfWeek
    );

    // ============== Consultas por presupuesto ==============
    List<Project> findByBudgetBetween(BigDecimal minBudget, BigDecimal maxBudget);
    List<Project> findByBudgetGreaterThan(BigDecimal budget);
    
    @Query("SELECT p FROM Project p WHERE p.status != 'CANCELLED' AND p.budget > 0")
    List<Project> findActiveProjectsWithBudget();

    // ============== Búsquedas de texto ==============
    @Query("SELECT p FROM Project p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.objectives) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Project> searchInAllFields(@Param("searchTerm") String searchTerm);

    // ============== Agregaciones y estadísticas ==============
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status != 'CANCELLED'")
    long countActiveProjects();
    
    long countByStatus(ProjectStatus status);
    
    @Query("SELECT SUM(p.budget) FROM Project p WHERE p.status != 'CANCELLED' AND p.budget IS NOT NULL")
    BigDecimal getTotalActiveBudget();
    
    @Query("SELECT SUM(p.budget) FROM Project p WHERE p.department.id = :departmentId AND p.budget IS NOT NULL")
    BigDecimal getTotalBudgetByDepartment(@Param("departmentId") Long departmentId);

    // ============== Consultas ordenadas ==============
    @Query("SELECT p FROM Project p WHERE p.status != 'CANCELLED' ORDER BY p.budget DESC")
    List<Project> findActiveProjectsOrderByBudgetDesc();
    
    List<Project> findAllByOrderByNameAsc();
    List<Project> findAllByOrderByEndDateAsc();
    
    @Query("SELECT p FROM Project p WHERE p.department.id = :departmentId " +
           "ORDER BY CASE p.priority " +
           "WHEN 'CRITICAL' THEN 1 " +
           "WHEN 'HIGH' THEN 2 " +
           "WHEN 'MEDIUM' THEN 3 " +
           "WHEN 'LOW' THEN 4 END ASC, p.endDate ASC")
    List<Project> findByDepartmentOrderByPriorityAndEndDate(@Param("departmentId") Long departmentId);

    // ============== Consultas complejas ==============
    @Query("SELECT p FROM Project p WHERE " +
           "(COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:departmentId IS NULL OR p.department.id = :departmentId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:priority IS NULL OR p.priority = :priority) AND " +
           "(:startDate IS NULL OR p.startDate >= :startDate) AND " +
           "(:endDate IS NULL OR p.endDate <= :endDate) AND " +
           "(:minBudget IS NULL OR p.budget >= :minBudget) AND " +
           "(:maxBudget IS NULL OR p.budget <= :maxBudget)")
    List<Project> findWithFilters(
            @Param("name") String name,
            @Param("departmentId") Long departmentId,
            @Param("status") ProjectStatus status,
            @Param("priority") ProjectPriority priority,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minBudget") BigDecimal minBudget,
            @Param("maxBudget") BigDecimal maxBudget
    );

    
    long countByDepartmentId(Long departmentId);
    
    // Alternativa si el anterior no funciona
    @Query("SELECT COUNT(p) FROM Project p WHERE p.department.id = :departmentId")
    long countByDepartment(@Param("departmentId") Long departmentId);
    

     @Query("SELECT COUNT(p) FROM Project p WHERE p.department.id = :departmentId AND p.status = :status")
    int countByDepartmentIdAndStatus(@Param("departmentId") Long departmentId, 
                                   @Param("status") ProjectStatus status);

        // Contar proyectos completados por departamento
    @Query("SELECT COUNT(p) FROM Project p WHERE p.department.id = :departmentId AND p.status = 'COMPLETED'")
    long countCompletedProjectsByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT COALESCE(SUM(p.budget), 0) FROM Project p WHERE p.department.id = :departmentId")
    Double sumBudgetByDepartmentId(@Param("departmentId") Long departmentId);


}