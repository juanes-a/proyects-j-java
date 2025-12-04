package com.example.demo.dto.request.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DepartmentRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @DecimalMin("0.0")
    private BigDecimal budget;

    @Size(max = 200)
    private String location;
}