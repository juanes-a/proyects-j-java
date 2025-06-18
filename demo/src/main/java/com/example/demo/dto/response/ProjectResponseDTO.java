package com.example.demo.dto.response;

import com.example.demo.entity.Project;
import com.example.demo.enums.ProjectPriority;
import com.example.demo.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para respuestas de Project usando Lombok
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {

    private Long id;
    private String name;
    private String description;
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


    /**
     * Método estático para convertir desde Entity
     */
    public static ProjectResponseDTO fromEntity(Project project) {
        if (project == null) {
            return null;
        }

        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setStatus(project.getStatus());
        dto.setPriority(project.getPriority());
        dto.setBudget(project.getBudget());

        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());


        // Si tienes relación con Department
        if (project.getDepartment() != null) {
            dto.setDepartmentId(project.getDepartment().getId());
            dto.setDepartmentName(project.getDepartment().getName());
        }

        return dto;
    }


    public ProjectResponseDTO(Long id2, String name2, String description2, LocalDate type, Object object) {
        //TODO Auto-generated constructor stub
    }


    public ProjectResponseDTO(Long id2, String name2, String description2, Object object) {
        //TODO Auto-generated constructor stub
    }
}