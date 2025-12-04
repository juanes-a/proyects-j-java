package com.example.demo.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentStats {
    private Long totalDepartments = 0L;
    private Long activeDepartments = 0L;
    private BigDecimal totalBudget = BigDecimal.ZERO;
}

