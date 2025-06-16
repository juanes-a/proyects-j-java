package com.example.demo.repository;

import com.example.demo.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // Buscar por nombre (ignorando mayúsculas/minúsculas)
    Optional<Department> findByNameIgnoreCase(String name);

    // Verificar si existe por nombre
    boolean existsByNameIgnoreCase(String name);

    // Buscar solo departamentos activos
    List<Department> findByIsActiveTrue();

    // Buscar solo departamentos inactivos
    List<Department> findByIsActiveFalse();

    // Buscar por rango de presupuesto
    List<Department> findByBudgetBetween(BigDecimal minBudget, BigDecimal maxBudget);

    // Buscar departamentos con presupuesto mayor a un valor
    List<Department> findByBudgetGreaterThan(BigDecimal budget);

    // Buscar departamentos por ubicación
    List<Department> findByLocationIgnoreCase(String location);

    // Buscar departamentos que contengan texto en nombre o descripción
    @Query("SELECT d FROM Department d WHERE " +
            "LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Department> findByNameOrDescriptionContaining(@Param("searchTerm") String searchTerm);

    // Buscar departamentos activos con presupuesto
    @Query("SELECT d FROM Department d WHERE d.isActive = true AND d.budget > 0")
    List<Department> findActiveDepartmentsWithBudget();

    // Contar departamentos activos
    long countByIsActiveTrue();

    // Contar departamentos por ubicación
    long countByLocation(String location);

    // Obtener suma total de presupuestos activos
    @Query("SELECT SUM(d.budget) FROM Department d WHERE d.isActive = true AND d.budget IS NOT NULL")
    BigDecimal getTotalActiveBudget();

    // Buscar departamentos ordenados por presupuesto descendente
    List<Department> findByIsActiveTrueOrderByBudgetDesc();

    // Buscar departamentos ordenados por nombre
    List<Department> findAllByOrderByNameAsc();
    @Query("SELECT d FROM Department d WHERE d.projects IS EMPTY")
    List<Department> findDepartmentsWithoutProjects();

    // Departamentos con más de X proyectos
    @Query("SELECT d FROM Department d WHERE SIZE(d.projects) > :count")
    List<Department> findDepartmentsWithMoreThanXProjects(@Param("count") int count);
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.projects WHERE d.id = :id")
    Optional<Department> findByIdWithProjects(@Param("id") Long id);

    // Método personalizado para buscar departamentos por múltiples criterios
    @Query("SELECT d FROM Department d WHERE " +
            "(:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:location IS NULL OR LOWER(d.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:isActive IS NULL OR d.isActive = :isActive) AND " +
            "(:minBudget IS NULL OR d.budget >= :minBudget) AND " +
            "(:maxBudget IS NULL OR d.budget <= :maxBudget)")
    List<Department> findDepartmentsByFilters(
            @Param("name") String name,
            @Param("location") String location,
            @Param("isActive") Boolean isActive,
            @Param("minBudget") BigDecimal minBudget,
            @Param("maxBudget") BigDecimal maxBudget
    );

    @Query("SELECT COALESCE(COUNT(d), 0) FROM Department d")
    Long countTotalDepartments();
    
    @Query("SELECT COALESCE(COUNT(d), 0) FROM Department d WHERE d.isActive = true")
    Long countActiveDepartments();
    
    @Query("SELECT COALESCE(SUM(d.budget), 0) FROM Department d")
    BigDecimal sumAllBudgets();
    
    @Query("SELECT SUM(d.budget) FROM Department d WHERE d.budget IS NOT NULL")
    BigDecimal sumBudget();

    long countByIsActive(boolean isActive);
}