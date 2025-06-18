package com.example.demo.dto.request.project;

import com.example.demo.enums.ProjectPriority;
import com.example.demo.enums.ProjectStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para crear nuevos proyectos
 * Compatible con ProjectService.createProject()
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateDTO {
    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
    
    @NotNull(message = "El departamento es obligatorio")
    private Long departmentId;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio debe ser hoy o en el futuro")
    private LocalDate startDate;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser en el futuro")
    private LocalDate endDate;
    
    @DecimalMin(value = "0.0", message = "El presupuesto no puede ser negativo")
    private Double budget;
    
    @NotNull(message = "La prioridad es obligatoria")
    private ProjectPriority priority;
    
    @NotNull(message = "El estado es obligatorio")
    private ProjectStatus status;
}