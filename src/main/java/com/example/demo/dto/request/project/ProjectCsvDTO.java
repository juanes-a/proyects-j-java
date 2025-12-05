package com.example.demo.dto.request.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCsvDTO {
    private String name;
    private String description;
    private String objectives;
    private String priority;
    private String status;
    private String startDate;
    private String endDate;
    private BigDecimal budget;
    private Long departmentId; // Dato clave para vincular al departamento
}