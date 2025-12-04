package com.example.demo.dto.request.department;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DepartmentUpdateDTO {
    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @DecimalMin("0.0")
    private BigDecimal budget;

    @Size(max = 200)
    private String location;

    private Boolean isActive;
}