package com.example.demo.dto.response;

import com.example.demo.entity.Project;
import com.example.demo.enums.ProjectPriority;
import com.example.demo.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String objectives; // Agregado basado en tu entidad
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private ProjectPriority priority;
    private BigDecimal budget;
    private BigDecimal actualCost;
    private Integer progress;
    private Long departmentId;
    private String departmentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean hasAssignees;
    private boolean overdue; // Campo calculado útil para el frontend

    public static ProjectResponseDTO fromEntity(Project project) {
        if (project == null) return null;

        return ProjectResponseDTO.builder()
            .id(project.getId())
            .name(project.getName())
            .description(project.getDescription())
            .objectives(project.getObjectives())
            .startDate(project.getStartDate())
            .endDate(project.getEndDate())
            .status(project.getStatus())
            .priority(project.getPriority())
            .budget(project.getBudget())
            .actualCost(calculateActualCost(project)) // Método a implementar
            .progress(calculateProgress(project)) // Método a implementar
            .departmentId(project.getDepartment() != null ? project.getDepartment().getId() : null)
            .departmentName(project.getDepartment() != null ? project.getDepartment().getName() : null)
            .createdAt(project.getCreatedAt())
            .updatedAt(project.getUpdatedAt())
            .hasAssignees(project.getAsignaciones() != null && !project.getAsignaciones().isEmpty())
            .overdue(project.isOverdue()) // Usando el método de tu entidad
            .build();
    }

    // Elimina los constructores redundantes que no hacen nada

    private static BigDecimal calculateActualCost(Project project) {
        // Implementa lógica para calcular costos reales
        return BigDecimal.ZERO; // Ejemplo
    }

    private static Integer calculateProgress(Project project) {
        // Implementa lógica para calcular progreso
        return 0; // Ejemplo
    }
}