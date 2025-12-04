package com.example.demo.dto.response;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DepartmentResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal budget;
    private String location;
    private Boolean isActive;
    private List<ProjectResponseDTO> projects;
}