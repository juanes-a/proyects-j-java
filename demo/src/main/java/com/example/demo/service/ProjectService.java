package com.example.demo.service;

import com.example.demo.entity.Project;
import com.example.demo.entity.Department;
import com.example.demo.enums.ProjectStatus;
import com.example.demo.enums.ProjectPriority;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.exception.ProjectNotFoundException;
import com.example.demo.exception.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * SERVICE PARA GESTIÓN DE PROYECTOS
 *
 * Esta clase contiene TODA la lógica de negocio relacionada con proyectos.
 * Es el punto central donde se ejecutan las reglas del dominio,
 * validaciones complejas y operaciones transaccionales.
 *
 * RESPONSABILIDADES:
 * 1. Operaciones CRUD con validaciones de negocio
 * 2. Gestión del ciclo de vida del proyecto (estados)
 * 3. Validaciones que requieren acceso a BD
 * 4. Coordinación con otras entidades (Department)
 * 5. Lógica de agregación y reportes
 *
 * PATRÓN: Cada método público es una "operación de negocio"
 */
@Service
@Transactional(readOnly = true) // Por defecto SOLO LECTURA
public class ProjectService {

    // ========================================
    // DEPENDENCIAS INYECTADAS
    // ========================================

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // ========================================
    // OPERACIONES CRUD CON LÓGICA DE NEGOCIO
    // ========================================

    /**
     * CREAR PROYECTO - Con validaciones de negocio
     *
     * REGLAS DE NEGOCIO:
     * 1. El departamento debe existir y estar activo
     * 2. No puede haber otro proyecto con el mismo nombre en el mismo departamento
     * 3. La fecha de inicio no puede ser posterior a la fecha de fin
     * 4. La fecha de inicio no puede ser anterior a hoy (para proyectos nuevos)
     */
    @Transactional // ESCRITURA - requiere transacción
    public Project createProject(String name, String description, String objectives,
                                 Long departmentId, LocalDate startDate, LocalDate endDate,
                                 BigDecimal budget, ProjectPriority priority,ProjectStatus status) {

        // VALIDACIÓN 1: Departamento existe y está activo

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new BusinessException("El departamento especificado no existe"));

        if (!department.getIsActive()) {
            throw new BusinessException("No se puede crear un proyecto en un departamento inactivo");
        }

        // VALIDACIÓN 2: Nombre único en el departamento
        if (projectRepository.existsByNameIgnoreCaseAndDepartmentId(name, departmentId)) {
            throw new BusinessException("Ya existe un proyecto con ese nombre en el departamento");
        }

        // VALIDACIÓN 3: Fechas lógicas
        validateProjectDates(startDate, endDate);

        // VALIDACIÓN 4: Fecha de inicio no en el pasado (para proyectos nuevos)
        if (startDate.isBefore(LocalDate.now())) {
            throw new BusinessException("La fecha de inicio no puede ser anterior a hoy");
        }

        // CREACIÓN: Usar constructor de la entidad
        Project project = new Project(name, description, objectives, department,
                startDate, endDate, budget,priority, status);

        // Asignar prioridad (si no se especifica, usa MEDIUM por defecto)
        if (priority != null) {
            project.setPriority(priority);
        }

        return projectRepository.save(project);
    }

    /**
     * OBTENER PROYECTO POR ID - Con validación de existencia
     */
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado con ID: " + id));
    }

    /**
     * OBTENER TODOS LOS PROYECTOS ACTIVOS
     * Usa el método del repository que filtra por status != CANCELLED
     */
    public List<Project> getAllActiveProjects() {
        return projectRepository.findActiveProjects();
    }

    /**
     * OBTENER PROYECTOS POR DEPARTAMENTO
     */
    public List<Project> getProjectsByDepartment(Long departmentId) {
        // Validar que el departamento existe
        if (!departmentRepository.existsById(departmentId)) {
            throw new BusinessException("El departamento especificado no existe");
        }
        return projectRepository.findByDepartmentId(departmentId);
    }

    /**
     * ACTUALIZAR PROYECTO - Con validaciones específicas
     */
    @Transactional
    public Project updateProject(Long id, String name, String description, String objectives,
                                 LocalDate startDate, LocalDate endDate, BigDecimal budget,
                                 ProjectPriority priority) {

        Project project = getProjectById(id);

        // REGLA: No se puede modificar un proyecto completado
        if (project.isCompleted()) {
            throw new BusinessException("No se puede modificar un proyecto completado");
        }

        // VALIDACIÓN: Si cambia el nombre, verificar unicidad en el departamento
        if (!project.getName().equalsIgnoreCase(name)) {
            if (projectRepository.existsByNameIgnoreCaseAndDepartmentId(name, project.getDepartment().getId())) {
                throw new BusinessException("Ya existe un proyecto con ese nombre en el departamento");
            }
        }

        // VALIDACIÓN: Fechas lógicas
        validateProjectDates(startDate, endDate);

        // REGLA: Si el proyecto está en progreso, no se puede cambiar fecha de inicio
        if (project.isInProgress() && !project.getStartDate().equals(startDate)) {
            throw new BusinessException("No se puede cambiar la fecha de inicio de un proyecto en progreso");
        }

        // ACTUALIZAR CAMPOS
        project.setName(name);
        project.setDescription(description);
        project.setObjectives(objectives);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setBudget(budget);
        project.setPriority(priority);

        return projectRepository.save(project);
    }

    // ========================================
    // GESTIÓN DEL CICLO DE VIDA DEL PROYECTO
    // ========================================

    /**
     * INICIAR PROYECTO - Cambiar estado a IN_PROGRESS
     *
     * REGLAS DE NEGOCIO:
     * 1. Solo proyectos en estado PLANNED
     * 2. La fecha de inicio debe ser hoy o anterior
     * 3. El departamento debe estar activo
     */
    @Transactional
    public Project startProject(Long id) {
        Project project = getProjectById(id);

        // VALIDACIÓN: Estado correcto
        if (!project.isPlanned()) {
            throw new BusinessException("Solo se pueden iniciar proyectos en estado PLANNED");
        }

        // VALIDACIÓN: Fecha de inicio válida
        if (project.getStartDate().isAfter(LocalDate.now())) {
            throw new BusinessException("No se puede iniciar un proyecto antes de su fecha de inicio");
        }

        // VALIDACIÓN: Departamento activo
        if (!project.getDepartment().getIsActive()) {
            throw new BusinessException("No se puede iniciar un proyecto de un departamento inactivo");
        }

        // USAR MÉTODO DE LA ENTIDAD (lógica de dominio)
        project.start();

        return projectRepository.save(project);
    }

    /**
     * COMPLETAR PROYECTO - Cambiar estado a COMPLETED
     */
    @Transactional
    public Project completeProject(Long id) {
        Project project = getProjectById(id);

        // USAR MÉTODO DE LA ENTIDAD
        project.complete(); // Ya tiene las validaciones internas

        return projectRepository.save(project);
    }

    /**
     * CANCELAR PROYECTO - Cambiar estado a CANCELLED
     */
    @Transactional
    public Project cancelProject(Long id, String reason) {
        Project project = getProjectById(id);

        // USAR MÉTODO DE LA ENTIDAD
        project.cancel(); // Ya tiene las validaciones internas



        return projectRepository.save(project);
    }

    // ========================================
    // CONSULTAS ESPECIALIZADAS
    // ========================================

    /**
     * OBTENER PROYECTOS VENCIDOS
     * Proyectos en progreso que han pasado su fecha de fin
     */
    public List<Project> getOverdueProjects() {
        return projectRepository.findOverdueProjects(LocalDate.now());
    }

    /**
     * OBTENER PROYECTOS URGENTES ACTIVOS
     * Proyectos con prioridad HIGH o CRITICAL que están activos
     */
    public List<Project> getUrgentActiveProjects() {
        return projectRepository.findUrgentActiveProjects();
    }

    /**
     * OBTENER PROYECTOS QUE TERMINAN ESTA SEMANA
     */
    public List<Project> getProjectsEndingThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate endOfWeek = today.plusDays(7);
        return projectRepository.findProjectsEndingThisWeek(today, endOfWeek);
    }

    /**
     * BUSCAR PROYECTOS CON FILTROS MÚLTIPLES
     * Método que usa la query personalizada del repository
     */
    public List<Project> searchProjects(String name, Long departmentId, ProjectStatus status,
                                        ProjectPriority priority, LocalDate startDate, LocalDate endDate,
                                        BigDecimal minBudget, BigDecimal maxBudget) {

        return projectRepository.findProjectsByFilters(
                name, departmentId, status, priority, startDate, endDate, minBudget, maxBudget
        );
    }

    // ========================================
    // VALIDACIONES Y REGLAS DE NEGOCIO
    // ========================================

    /**
     * VALIDAR SI SE PUEDE ELIMINAR UN PROYECTO
     *
     * REGLAS:
     * 1. Solo proyectos en estado PLANNED o CANCELLED
     * 2. El usuario debe tener permisos (esto se validará en Controller/Security)
     */
    public boolean canDeleteProject(Long id) {
        Project project = getProjectById(id);
        return project.isPlanned() || project.getStatus() == ProjectStatus.CANCELLED;
    }

    /**
     * ELIMINAR PROYECTO - Con validaciones
     */
    @Transactional
    public void deleteProject(Long id) {
        if (!canDeleteProject(id)) {
            throw new BusinessException("Solo se pueden eliminar proyectos en estado PLANNED o CANCELLED");
        }

        projectRepository.deleteById(id);
    }

    /**
     * VALIDAR FECHAS DEL PROYECTO (método privado de apoyo)
     */
    private void validateProjectDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BusinessException("Las fechas de inicio y fin son obligatorias");
        }

        if (startDate.isAfter(endDate)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        // REGLA: Un proyecto debe durar al menos 1 día
        if (startDate.equals(endDate)) {
            throw new BusinessException("Un proyecto debe tener una duración mínima de 1 día");
        }
    }

    // ========================================
    // ESTADÍSTICAS Y REPORTES
    // ========================================

    /**
     * OBTENER ESTADÍSTICAS DE PROYECTOS POR DEPARTAMENTO
     */
    public ProjectStatistics getProjectStatisticsByDepartment(Long departmentId) {
        long totalProjects = projectRepository.countByDepartmentId(departmentId);
        long activeProjects = projectRepository.countActiveProjectsByDepartment(departmentId);
        long urgentProjects = projectRepository.countUrgentActiveProjectsByDepartment(departmentId);
        BigDecimal totalBudget = projectRepository.getTotalBudgetByDepartment(departmentId);

        return new ProjectStatistics(totalProjects, activeProjects, urgentProjects, totalBudget);
    }

    /**
     * OBTENER RESUMEN GENERAL DE PROYECTOS
     */
    public ProjectSummary getProjectSummary() {
        long totalActive = projectRepository.countActiveProjects();
        long plannedCount = projectRepository.countByStatus(ProjectStatus.PLANNED);
        long inProgressCount = projectRepository.countByStatus(ProjectStatus.IN_PROGRESS);
        long completedCount = projectRepository.countByStatus(ProjectStatus.COMPLETED);
        BigDecimal totalBudget = projectRepository.getTotalActiveBudget();

        return new ProjectSummary(totalActive, plannedCount, inProgressCount,
                completedCount, totalBudget);
    }
}

// ========================================
// CLASES DE APOYO PARA ESTADÍSTICAS
// ========================================

/**
 * DTO para estadísticas de proyectos por departamento
 */
class ProjectStatistics {
    private long totalProjects;
    private long activeProjects;
    private long urgentProjects;
    private BigDecimal totalBudget;

    public ProjectStatistics(long totalProjects, long activeProjects,
                             long urgentProjects, BigDecimal totalBudget) {
        this.totalProjects = totalProjects;
        this.activeProjects = activeProjects;
        this.urgentProjects = urgentProjects;
        this.totalBudget = totalBudget;
    }

    // Getters...
    public long getTotalProjects() { return totalProjects; }
    public long getActiveProjects() { return activeProjects; }
    public long getUrgentProjects() { return urgentProjects; }
    public BigDecimal getTotalBudget() { return totalBudget; }
}

/**
 * DTO para resumen general de proyectos
 */
class ProjectSummary {
    private long totalActive;
    private long plannedCount;
    private long inProgressCount;
    private long completedCount;
    private BigDecimal totalBudget;

    public ProjectSummary(long totalActive, long plannedCount, long inProgressCount,
                          long completedCount, BigDecimal totalBudget) {
        this.totalActive = totalActive;
        this.plannedCount = plannedCount;
        this.inProgressCount = inProgressCount;
        this.completedCount = completedCount;
        this.totalBudget = totalBudget;
    }

    // Getters...
    public long getTotalActive() { return totalActive; }
    public long getPlannedCount() { return plannedCount; }
    public long getInProgressCount() { return inProgressCount; }
    public long getCompletedCount() { return completedCount; }
    public BigDecimal getTotalBudget() { return totalBudget; }
}