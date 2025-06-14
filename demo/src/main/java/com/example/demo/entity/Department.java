package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del departamento es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "department",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            fetch = FetchType.LAZY,
            orphanRemoval = false)
    @JsonManagedReference  // Evita referencias circulares en JSON
    private List<Project> projects = new ArrayList<>();


    @DecimalMin(value = "0.0", message = "El presupuesto no puede ser negativo")
    @Column(precision = 15, scale = 2)
    private BigDecimal budget;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Size(max = 200, message = "La ubicación no puede exceder 200 caracteres")
    @Column(length = 200)
    private String location;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructor vacío OBLIGATORIO para JPA
    public Department() {}

    // Constructor con campos esenciales
    public Department(String name, String description) {
        this.name = name;
        this.description = description;
        this.isActive = true;
    }

    // Constructor completo
    public Department(String name, String description, BigDecimal budget, String location) {
        this.name = name;
        this.description = description;
        this.budget = budget;
        this.location = location;
        this.isActive = true;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Métodos de negocio
    public void activate() {
        this.isActive = true;
    }


    public void deactivate() {
        this.isActive = false;
    }

    public boolean hasActiveBudget() {
        return this.budget != null && this.budget.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getTotalProjectsBudget() {
        return projects.stream()
                .filter(project -> project.getBudget() != null)
                .map(Project::getBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getProjectCount() {
        return projects.size();
    }

    public void addProject(Project project) {
        projects.add(project);
        project.setDepartment(this);
    }

    public void removeProject(Project project) {
        projects.remove(project);
        project.setDepartment(null);
    }


    // Métodos de JPA estándar
    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", budget=" + budget +
                ", isActive=" + isActive +
                ", location='" + location + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department)) return false;
        Department that = (Department) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}