package com.example.demo.entity;

import com.example.demo.enums.ProjectPriority;
import com.example.demo.enums.ProjectStatus;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ENTIDAD PROJECT - PROYECTO
 *
 * Representa un proyecto dentro del sistema gestor de proyectos.
 * Cada proyecto pertenece a un departamento y tiene un ciclo de vida
 * definido por estados (PLANNED → IN_PROGRESS → COMPLETED/CANCELLED).
 *
 * RELACIONES:
 * - ManyToOne con Department: Un departamento puede tener muchos proyectos
 *
 * CARACTERÍSTICAS PRINCIPALES:
 * - Gestión de estados del proyecto
 * - Control de fechas de inicio y fin
 * - Presupuesto asignado
 * - Objetivos definidos
 * - Timestamps automáticos
 * - Validaciones de negocio integradas
 */
@Entity
@Table(name = "projects",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "department_id"}))
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========================================
    // CAMPOS BÁSICOS DEL PROYECTO
    // ========================================

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ProjectPriority priority = ProjectPriority.MEDIUM;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String description;

    @Size(max = 1000, message = "Los objetivos no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String objectives;

    // ========================================
    // RELACIÓN CON DEPARTAMENTO
    // ========================================
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @JsonBackReference
    @NotNull(message = "El proyecto debe estar asignado a un departamento")
    private Department department;

    // ========================================
    // ESTADO DEL PROYECTO
    // ========================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.PLANNED;

    // ========================================
    // GESTIÓN DE FECHAS
    // ========================================

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // ========================================
    // PRESUPUESTO
    // ========================================

    @DecimalMin(value = "0.0", message = "El presupuesto no puede ser negativo")
    @Column(precision = 15, scale = 2)
    private BigDecimal budget;

    // ========================================
    // TIMESTAMPS AUTOMÁTICOS
    // ========================================

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========================================
    // CONSTRUCTORES
    // ========================================

    /**
     * Constructor vacío OBLIGATORIO para JPA
     */
    public Project() {}

    /**
     * Constructor con campos esenciales
     * Para crear un proyecto básico rápidamente
     */
    public Project(String name, Department department, LocalDate startDate, LocalDate endDate, ProjectPriority priority) {
        this.name = name;
        this.department = department;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = ProjectStatus.PLANNED;
        this.priority=priority;
    }

    /**
     * Constructor completo
     * Para crear un proyecto con todos los detalles
     */
    public Project(String name, String description, String objectives, Department department,
                   LocalDate startDate, LocalDate endDate, BigDecimal budget, ProjectPriority priority,ProjectStatus status) {
        this.name = name;
        this.description = description;
        this.objectives = objectives;
        this.department = department;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority=priority;
        this.budget = budget;
        this.status = status != null ? status : ProjectStatus.PLANNED;
    }

    // ========================================
    // GETTERS Y SETTERS
    // ========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ========================================
    // MÉTODOS DE NEGOCIO
    // ========================================

    /**
     * Verifica si el proyecto está activo
     * Un proyecto está activo si NO está cancelado
     */
    public boolean isActive() {
        return this.status != ProjectStatus.CANCELLED;
    }

    /**
     * Verifica si el proyecto está en progreso
     */
    public boolean isInProgress() {
        return this.status == ProjectStatus.IN_PROGRESS;
    }

    /**
     * Verifica si el proyecto está completado
     */
    public boolean isCompleted() {
        return this.status == ProjectStatus.COMPLETED;
    }

    /**
     * Verifica si el proyecto está planificado (aún no iniciado)
     */
    public boolean isPlanned() {
        return this.status == ProjectStatus.PLANNED;
    }

    /**
     * Verifica si el proyecto está atrasado
     * Un proyecto está atrasado si la fecha actual es posterior a la fecha de fin
     * y el proyecto sigue en progreso
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(this.endDate) && this.status == ProjectStatus.IN_PROGRESS;
    }

    /**
     * Inicia el proyecto (cambia estado a IN_PROGRESS)
     * Solo se puede iniciar si está en estado PLANNED
     */
    public void start() {
        if (this.status == ProjectStatus.PLANNED) {
            this.status = ProjectStatus.IN_PROGRESS;
        } else {
            throw new IllegalStateException("Solo se puede iniciar un proyecto en estado PLANNED");
        }
    }

    /**
     * Completa el proyecto (cambia estado a COMPLETED)
     * Solo se puede completar si está en estado IN_PROGRESS
     */
    public void complete() {
        if (this.status == ProjectStatus.IN_PROGRESS) {
            this.status = ProjectStatus.COMPLETED;
        } else {
            throw new IllegalStateException("Solo se puede completar un proyecto en estado IN_PROGRESS");
        }
    }

    /**
     * Cancela el proyecto (cambia estado a CANCELLED)
     * Se puede cancelar desde cualquier estado excepto COMPLETED
     */
    public void cancel() {
        if (this.status != ProjectStatus.COMPLETED) {
            this.status = ProjectStatus.CANCELLED;
        } else {
            throw new IllegalStateException("No se puede cancelar un proyecto ya completado");
        }
    }

    /**
     * Verifica si el proyecto tiene presupuesto asignado
     */
    public boolean hasBudget() {
        return this.budget != null && this.budget.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Calcula la duración del proyecto en días
     */
    public long getDurationInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(this.startDate, this.endDate);
    }

    /**
     * Verifica si el proyecto tiene objetivos definidos
     */
    public boolean hasObjectives() {
        return this.objectives != null && !this.objectives.trim().isEmpty();
    }

    // ========================================
    // MÉTODOS DE JPA ESTÁNDAR
    // ========================================

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", objectives='" + objectives + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", budget=" + budget +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return id != null && id.equals(project.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public ProjectPriority getPriority() {
        return priority;
    }

    public void setPriority(ProjectPriority priority) {
        this.priority = priority;
    }
}

/**
 * ENUM PARA ESTADOS DEL PROYECTO
 *
 * PLANNED: Proyecto planificado, aún no iniciado
 * IN_PROGRESS: Proyecto en ejecución
 * COMPLETED: Proyecto finalizado exitosamente
 * CANCELLED: Proyecto cancelado (actúa como "inactivo")
 */
