package com.example.demo.dto.request.project;

import com.example.demo.enums.ProjectPriority;
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
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
    private String name;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;

    // ¡CAMPO FALTANTE! Tu service lo requiere
    @Size(max = 1000, message = "Los objetivos no pueden exceder 1000 caracteres")
    private String objectives;

    @NotNull(message = "El departamento es obligatorio")
    private Long departmentId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede ser en el pasado")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    // Cambié @Future por @FutureOrPresent para ser menos restrictivo
    @FutureOrPresent(message = "La fecha de fin debe ser presente o futura")
    private LocalDate endDate;

    @NotNull(message = "El presupuesto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El presupuesto debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El presupuesto debe tener máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal budget;

    // Prioridad puede ser null - tu service asigna MEDIUM por defecto
    private ProjectPriority priority;

    /**
     * Validación personalizada para fechas
     */
    @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
    public boolean isEndDateAfterStartDate() {
        if (startDate == null || endDate == null) {
            return true; // Las validaciones @NotNull se encargarán de esto
        }
        return endDate.isAfter(startDate);
    }
}