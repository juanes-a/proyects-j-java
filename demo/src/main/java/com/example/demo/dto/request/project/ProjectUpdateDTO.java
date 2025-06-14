package com.example.demo.dto.request.project;

import com.example.demo.enums.ProjectPriority;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
@Data

public class ProjectUpdateDTO {

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;

    @Size(max = 1000, message = "Los objetivos no pueden exceder 1000 caracteres")
    private String objectives;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate endDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "El presupuesto debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El presupuesto debe tener máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal budget;

    @NotNull(message = "La prioridad es obligatoria")
    private ProjectPriority priority;
}