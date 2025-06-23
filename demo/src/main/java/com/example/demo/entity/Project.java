package com.example.demo.entity;

import com.example.demo.enums.ProjectPriority;
import com.example.demo.enums.ProjectStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "projects", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "department_id"}))
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String description;

    @Size(max = 1000, message = "Los objetivos no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String objectives;

    @NotNull(message = "La prioridad del proyecto es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ProjectPriority priority = ProjectPriority.MEDIUM;

    @NotNull(message = "El estado del proyecto es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.PLANNED;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @DecimalMin(value = "0.0", message = "El presupuesto no puede ser negativo")
    @Column(precision = 15, scale = 2)
    private BigDecimal budget;

    @JsonBackReference
    @NotNull(message = "El proyecto debe estar asignado a un departamento")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    
@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonManagedReference("project-tasks")
private List<TaskEntity> tasks;

    

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== Métodos de negocio ==========

    public boolean isActive() {
        return !isCancelled();
    }

    public boolean isInProgress() {
        return this.status == ProjectStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return this.status == ProjectStatus.COMPLETED;
    }

    public boolean isPlanned() {
        return this.status == ProjectStatus.PLANNED;
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(this.endDate) && this.status == ProjectStatus.IN_PROGRESS;
    }

    public void start() {
        if (this.status == ProjectStatus.PLANNED) {
            this.status = ProjectStatus.IN_PROGRESS;
        } else {
            throw new IllegalStateException("Solo se puede iniciar un proyecto en estado PLANNED");
        }
    }

    public void complete() {
        if (this.status == ProjectStatus.IN_PROGRESS) {
            this.status = ProjectStatus.COMPLETED;
        } else {
            throw new IllegalStateException("Solo se puede completar un proyecto en estado IN_PROGRESS");
        }
    }

    public void cancel() {
        if (this.status != ProjectStatus.COMPLETED) {
            this.status = ProjectStatus.CANCELLED;
        } else {
            throw new IllegalStateException("No se puede cancelar un proyecto ya completado");
        }
    }

    public boolean hasBudget() {
        return this.budget != null && this.budget.compareTo(BigDecimal.ZERO) > 0;
    }

    public long getDurationInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(this.startDate, this.endDate);
    }

    public boolean hasObjectives() {
        return this.objectives != null && !this.objectives.trim().isEmpty();
    }

    // ========== Métodos estándar ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id != null && id.equals(project.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Builder
    public Project(String name, String description, String objectives, 
              ProjectPriority priority, ProjectStatus status,
              LocalDate startDate, LocalDate endDate, 
              BigDecimal budget, Department department)  {
        //TODO Auto-generated constructor stub
    }

    public boolean isCancelled() {
        return this.status == ProjectStatus.CANCELLED;
    }
}