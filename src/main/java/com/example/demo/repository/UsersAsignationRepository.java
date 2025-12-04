package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Department;
import com.example.demo.entity.Project;
import com.example.demo.entity.TaskEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.UsersAsignation;
import com.example.demo.enums.Role;

@Repository
public interface UsersAsignationRepository extends JpaRepository<UsersAsignation, Long> {

    // Consultas para Departamentos
    Optional<UsersAsignation> findByUserAndDepartmentIsNotNull(UserEntity user);
    
    List<UsersAsignation> findByDepartment(Department department);
    
    List<UsersAsignation> findByDepartmentAndRolAsignado(Department department, Role role);
    
    @Query("SELECT ua FROM UsersAsignation ua WHERE ua.user.id = :userId AND ua.department IS NOT NULL")
    Optional<UsersAsignation> findDepartmentAssignmentByUserId(@Param("userId") Long userId);

    // Consultas para Proyectos
    Optional<UsersAsignation> findByUserAndProjectIsNotNull(UserEntity user);

    Optional<UsersAsignation> findByUserAndProject(UserEntity user, Project project);


    Optional<UsersAsignation> findByUserAndTask(UserEntity user, TaskEntity taskEntity);
    
    List<UsersAsignation> findByProject(Project project);
    
    List<UsersAsignation> findByProjectAndRolAsignado(Project project, Role role);

    // Consultas para Tareas
    @Query("SELECT ua FROM UsersAsignation ua WHERE ua.user = :user AND ua.task IS NOT NULL")
    List<UsersAsignation> findAssignedTasks(@Param("user") UserEntity user);


    List<UsersAsignation> findByUserAndTaskIsNotNull(UserEntity user);

    
    List<UsersAsignation> findByTask(TaskEntity task);
    
    List<UsersAsignation> findByTaskAndRolAsignado(TaskEntity task, Role role);

    // Consultas generales por usuario
    List<UsersAsignation> findByUser(UserEntity user);
    
    List<UsersAsignation> findByUserAndRolAsignado(UserEntity user, Role role);

    // Verificar si un usuario tiene asignaciones específicas
    @Query("SELECT COUNT(ua) > 0 FROM UsersAsignation ua WHERE ua.user.id = :userId AND ua.department.id = :departmentId")
    boolean existsByUserIdAndDepartmentId(@Param("userId") Long userId, @Param("departmentId") Long departmentId);
    
    @Query("SELECT COUNT(ua) > 0 FROM UsersAsignation ua WHERE ua.user.id = :userId AND ua.project.id = :projectId")
    boolean existsByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);
    
    @Query("SELECT COUNT(ua) > 0 FROM UsersAsignation ua WHERE ua.user.id = :userId AND ua.task.id = :taskId")
    boolean existsByUserIdAndTaskId(@Param("userId") Long userId, @Param("taskId") Long taskId);

    // Obtener todas las asignaciones de un tipo específico
    @Query("SELECT ua FROM UsersAsignation ua WHERE ua.department IS NOT NULL")
    List<UsersAsignation> findAllDepartmentAssignments();
    
    @Query("SELECT ua FROM UsersAsignation ua WHERE ua.project IS NOT NULL")
    List<UsersAsignation> findAllProjectAssignments();
    
    @Query("SELECT ua FROM UsersAsignation ua WHERE ua.task IS NOT NULL")
    List<UsersAsignation> findAllTaskAssignments();

    // Consultas para obtener asignaciones por rol
    List<UsersAsignation> findByRolAsignado(Role role);
    
    @Query("SELECT ua FROM UsersAsignation ua WHERE ua.rolAsignado = :role AND ua.department IS NOT NULL")
    List<UsersAsignation> findDepartmentAssignmentsByRole(@Param("role") Role role);
    
    @Query("SELECT ua FROM UsersAsignation ua WHERE ua.rolAsignado = :role AND ua.project IS NOT NULL")
    List<UsersAsignation> findProjectAssignmentsByRole(@Param("role") Role role);
    
    @Query("SELECT ua FROM UsersAsignation ua WHERE ua.rolAsignado = :role AND ua.task IS NOT NULL")
    List<UsersAsignation> findTaskAssignmentsByRole(@Param("role") Role role);

    @Query("SELECT ua FROM UsersAsignation ua " +
        "JOIN ua.department d " +
        "WHERE ua.user = :user " +
        "AND ua.department IS NOT NULL " +
        "AND d.isActive = true " +
        "ORDER BY ua.dateAsignDateTime DESC")
    Optional<UsersAsignation> findTopByUserAndDepartmentIsNotNullOrderByDateAsignDateTimeDesc(
        @Param("user") UserEntity user);


    @Query("SELECT ua FROM UsersAsignation ua " +
        "JOIN ua.project p " +
        "WHERE ua.user = :user " +
        "AND ua.project IS NOT NULL " +
        "ORDER BY ua.dateAsignDateTime DESC")
    Optional<UsersAsignation> findTopByUserAndProjectIsNotNullOrderByDateAsignDateTimeDesc(
        @Param("user") UserEntity user);
}