package com.example.demo.repository;

import com.example.demo.entity.Project;
import com.example.demo.enums.ProjectPriority;
import com.example.demo.enums.ProjectStatus;
import com.example.demo.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY PARA PROYECTOS
 *
 * Este Repository sigue la MISMA ESTRUCTURA que DepartmentRepository pero está
 * ADAPTADO para las necesidades específicas de Project:
 *
 * DIFERENCIAS PRINCIPALES CON DepartmentRepository:
 * 1. En lugar de "isActive" usamos "status" (más granular)
 * 2. Agregamos consultas por Department (relación)
 * 3. Agregamos consultas por fechas (startDate, endDate)
 * 4. Agregamos consultas por ProjectStatus específicos
 * 5. Mantenemos el mismo patrón de agregaciones y búsquedas
 *
 * MANTIENE DE DepartmentRepository:
 * - Queries por nombre
 * - Búsquedas con texto libre
 * - Consultas con rangos (presupuesto, fechas)
 * - Agregaciones (contar, sumar)
 * - Query personalizada con filtros múltiples
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // ========================================
    // CONSULTAS BÁSICAS POR NOMBRE
    // ========================================

    /**
     * Buscar proyecto por nombre (ignorando mayúsculas/minúsculas)
     * IGUAL que en Department
     */
    Optional<Project> findByNameIgnoreCase(String name);

    /**
     * Verificar si existe proyecto por nombre
     * IGUAL que en Department
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * NUEVO: Verificar si existe proyecto con el mismo nombre en el mismo departamento
     * ESPECÍFICO de Project por la relación con Department
     */
    boolean existsByNameIgnoreCaseAndDepartmentId(String name, Long departmentId);

    // ========================================
    // CONSULTAS POR ESTADO (REEMPLAZAN A isActive)
    // ========================================

    /**
     * CAMBIO: En lugar de findByIsActiveTrue() usamos estados específicos
     * Buscar proyectos por estado específico
     */
    List<Project> findByStatus(ProjectStatus status);

    /**
     * NUEVO: Buscar proyectos activos (todos menos CANCELLED)
     * Equivale a findByIsActiveTrue() de Department
     */
    @Query("SELECT p FROM Project p WHERE p.status != 'CANCELLED'")
    List<Project> findActiveProjects();

    /**
     * NUEVO: Buscar proyectos inactivos (solo CANCELLED)
     * Equivale a findByIsActiveFalse() de Department
     */
// Removed as it duplicates the method already defined in line 69.

    /**
     * NUEVO: Buscar proyectos en progreso
     * ESPECÍFICO de Project
     */
    List<Project> findByStatusOrderByEndDateAsc(ProjectStatus status);

    /**
     * NUEVO: Buscar proyectos completados
     * ESPECÍFICO de Project
     */
    @Query("SELECT p FROM Project p WHERE p.status = 'COMPLETED' ORDER BY p.updatedAt DESC")
    List<Project> findCompletedProjectsOrderByCompletionDate();

    // ========================================
    // CONSULTAS POR RELACIÓN CON DEPARTMENT
    // ========================================

    /**
     * NUEVO: Buscar proyectos por departamento
     * ESPECÍFICO de Project por la relación
     */
    List<Project> findByDepartmentId(Long departmentId);

    /**
     * NUEVO: Buscar proyectos activos por departamento
     * ESPECÍFICO de Project
     */
    @Query("SELECT p FROM Project p WHERE p.department.id = :departmentId AND p.status != 'CANCELLED'")
    List<Project> findActiveProjectsByDepartment(@Param("departmentId") Long departmentId);

    /**
     * NUEVO: Buscar proyectos por departamento y estado
     * ESPECÍFICO de Project
     */
    List<Project> findByDepartmentIdAndStatus(Long departmentId, ProjectStatus status);

    /**
     * NUEVO: Contar proyectos por departamento
     * ESPECÍFICO de Project
     */
    long countByDepartmentId(Long departmentId);

    /**
     * NUEVO: Contar proyectos activos por departamento
     * ESPECÍFICO de Project
     */
    @Query("SELECT COUNT(p) FROM Project p WHERE p.department.id = :departmentId AND p.status != 'CANCELLED'")
    long countActiveProjectsByDepartment(@Param("departmentId") Long departmentId);

    // ========================================
    // CONSULTAS POR FECHAS (NUEVAS)
    // ========================================

    /**
     * NUEVO: Buscar proyectos que terminan en un rango de fechas
     * ESPECÍFICO de Project
     */
    List<Project> findByEndDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * NUEVO: Buscar proyectos que empiezan en una fecha específica
     * ESPECÍFICO de Project
     */
    List<Project> findByStartDate(LocalDate startDate);

    /**
     * NUEVO: Buscar proyectos vencidos (fecha fin pasada y en progreso)
     * ESPECÍFICO de Project
     */
    @Query("SELECT p FROM Project p WHERE p.endDate < :currentDate AND p.status = 'IN_PROGRESS'")
    List<Project> findOverdueProjects(@Param("currentDate") LocalDate currentDate);

    /**
     * NUEVO: Buscar proyectos que terminan esta semana
     * ESPECÍFICO de Project
     */
    @Query("SELECT p FROM Project p WHERE p.endDate BETWEEN :startOfWeek AND :endOfWeek AND p.status = 'IN_PROGRESS'")
    List<Project> findProjectsEndingThisWeek(@Param("startOfWeek") LocalDate startOfWeek,
                                             @Param("endOfWeek") LocalDate endOfWeek);

    // ========================================
    // CONSULTAS POR PRESUPUESTO (SIMILARES A Department)
    // ========================================

    /**
     * IGUAL que Department: Buscar por rango de presupuesto
     */
    List<Project> findByBudgetBetween(BigDecimal minBudget, BigDecimal maxBudget);

    /**
     * IGUAL que Department: Buscar proyectos con presupuesto mayor a un valor
     */

    List<Project> findByBudgetGreaterThan(BigDecimal budget);

    /**
     * NUEVO: Buscar proyectos activos con presupuesto
     * ADAPTADO de Department (cambió isActive por status)
     */
    @Query("SELECT p FROM Project p WHERE p.status != 'CANCELLED' AND p.budget > 0")
    List<Project> findActiveProjectsWithBudget();

    // ========================================
    // BÚSQUEDAS DE TEXTO (SIMILARES A Department)
    // ========================================

    /**
     * ADAPTADO de Department: Buscar en nombre, descripción Y objetivos
     * CAMBIO: Agregamos búsqueda en el campo "objectives"
     */
    @Query("SELECT p FROM Project p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.objectives) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Project> findByNameDescriptionOrObjectivesContaining(@Param("searchTerm") String searchTerm);

    // ========================================
    // AGREGACIONES Y ESTADÍSTICAS (SIMILARES A Department)
    // ========================================

    /**
     * ADAPTADO: Contar proyectos activos (en lugar de isActiveTrue)
     */
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status != 'CANCELLED'")
    long countActiveProjects();

    /**
     * NUEVO: Contar proyectos por estado
     * ESPECÍFICO de Project
     */
    long countByStatus(ProjectStatus status);

    /**
     * ADAPTADO de Department: Obtener suma total de presupuestos activos
     * CAMBIO: Cambió condición de isActive por status
     */
    @Query("SELECT SUM(p.budget) FROM Project p WHERE p.status != 'CANCELLED' AND p.budget IS NOT NULL")
    BigDecimal getTotalActiveBudget();

    /**
     * NUEVO: Obtener suma de presupuestos por departamento
     * ESPECÍFICO de Project
     */
    @Query("SELECT SUM(p.budget) FROM Project p WHERE p.department.id = :departmentId AND p.budget IS NOT NULL")
    BigDecimal getTotalBudgetByDepartment(@Param("departmentId") Long departmentId);

    // ========================================
    // CONSULTAS ORDENADAS (SIMILARES A Department)
    // ========================================

    /**
     * ADAPTADO: Proyectos activos ordenados por presupuesto descendente
     */
    @Query("SELECT p FROM Project p WHERE p.status != 'CANCELLED' ORDER BY p.budget DESC")
    List<Project> findActiveProjectsOrderByBudgetDesc();

    /**
     * IGUAL que Department: Ordenar por nombre
     */
    List<Project> findAllByOrderByNameAsc();

    // métodos prioridad

    // ========================================
// CONSULTAS POR PRIORIDAD (AGREGAR AL REPOSITORY)
// ========================================

    /**
     * Buscar proyectos por prioridad específica
     */
    List<Project> findByPriority(ProjectPriority priority);

    /**
     * Buscar proyectos urgentes (HIGH + CRITICAL) activos
     * CASO DE USO: Dashboard de alertas
     */
    @Query("SELECT p FROM Project p WHERE p.priority IN ('HIGH', 'CRITICAL') AND p.status != 'CANCELLED'")
    List<Project> findUrgentActiveProjects();

    /**
     * Buscar proyectos por prioridad y estado
     * CASO DE USO: "Proyectos críticos en progreso"
     */
    List<Project> findByPriorityAndStatus(ProjectPriority priority, ProjectStatus status);

    /**
     * Buscar proyectos por departamento ordenados por prioridad (nivel desc)
     * CASO DE USO: Dashboard del departamento
     */
    @Query("SELECT p FROM Project p WHERE p.department.id = :departmentId " +
            "ORDER BY p.priority DESC, p.endDate ASC")
    List<Project> findByDepartmentOrderByPriorityAndEndDate(@Param("departmentId") Long departmentId);

    /**
     * Contar proyectos por prioridad
     * CASO DE USO: Estadísticas de gestión
     */
    long countByPriority(ProjectPriority priority);

    /**
     * Contar proyectos urgentes activos por departamento
     * CASO DE USO: KPIs departamentales
     */
    @Query("SELECT COUNT(p) FROM Project p WHERE p.department.id = :departmentId " +
            "AND p.priority IN ('HIGH', 'CRITICAL') AND p.status != 'CANCELLED'")
    long countUrgentActiveProjectsByDepartment(@Param("departmentId") Long departmentId);

    /**
     * Buscar proyectos vencidos de alta prioridad
     * CASO DE USO: Alertas críticas
     */
    @Query("SELECT p FROM Project p WHERE p.endDate < :currentDate " +
            "AND p.status = 'IN_PROGRESS' AND p.priority IN ('HIGH', 'CRITICAL')")
    List<Project> findOverdueHighPriorityProjects(@Param("currentDate") LocalDate currentDate);

    /**
     * NUEVO: Ordenar por fecha de fin
     * ESPECÍFICO de Project
     */
    List<Project> findAllByOrderByEndDateAsc();

    // ========================================
    // QUERY PERSONALIZADA CON FILTROS MÚLTIPLES
    // ========================================

    /**
     * ADAPTADO de Department: Query con filtros múltiples
     * CAMBIOS:
     * - Agregado filtro por departmentId
     * - Agregado filtro por status
     * - Agregado filtros por fechas
     * - Cambió isActive por status
     */
    @Query("SELECT p FROM Project p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:departmentId IS NULL OR p.department.id = :departmentId) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:priority IS NULL OR p.priority = :priority) AND " +
            "(:startDate IS NULL OR p.startDate >= :startDate) AND " +
            "(:endDate IS NULL OR p.endDate <= :endDate) AND " +
            "(:minBudget IS NULL OR p.budget >= :minBudget) AND " +
            "(:maxBudget IS NULL OR p.budget <= :maxBudget)")
    List<Project> findProjectsByFilters(
            @Param("name") String name,
            @Param("departmentId") Long departmentId,
            @Param("status") ProjectStatus status,
            @Param("priority") ProjectPriority priority,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minBudget") BigDecimal minBudget,
            @Param("maxBudget") BigDecimal maxBudget

    );
}