package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la tarea es obligatorio")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String description;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatus status = TaskStatus.PENDING;

    @NotNull(message = "La prioridad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Min(value = 0, message = "Las horas estimadas no pueden ser negativas")
    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Min(value = 0, message = "Las horas reales no pueden ser negativas")
    @Column(name = "actual_hours")
    private Integer actualHours;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // Relación  con Project
    @JsonIgnore
    @NotNull(message = "El proyecto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @JsonProperty("projectId")
    public Long ExposeProjectId() {
        return project != null ? project.getId() : null;
    }
    @JsonProperty("projectName")
    public String getProjectName() {
        return project != null ? project.getName() : null;
    }

    // Usuario asignado a la tarea (opcional)
    @JsonIgnore // Esto oculta el objeto completo UserEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private UserEntity assignedUser;

    // 👇 AGREGA ESTOS DOS MÉTODOS PARA QUE EL FRONTEND VEA LA ASIGNACIÓN 👇
    
    // 👇 AGREGA ESTO PARA PODER ASIGNAR DESDE EL EDITAR (PUT) 👇
    @JsonProperty("assignedUserId")
    public void setAssignedUserId(Long userId) {
        if (userId != null) {
            // Crea un usuario temporal solo con el ID para hacer la relación
            this.assignedUser = UserEntity.builder().id(userId).build();
        } else {
            this.assignedUser = null;
        }
    }
    @JsonProperty("assignedUserName")
    public String getAssignedUserName() {
        // Devuelve el username o email, lo que prefieras mostrar
        return assignedUser != null ? assignedUser.getUsername() : null; 
    }
    
    @JsonProperty("assignedUserEmail")
    public String getAssignedUserEmail() {
        return assignedUser != null ? assignedUser.getEmail() : null;
    }

    // ===== ENUMS =====
    public enum TaskStatus {
        PENDING("Pendiente"),
        IN_PROGRESS("En Progreso"),
        IN_REVIEW("En Revisión"),
        COMPLETED("Completada"),
        CANCELLED("Cancelada");

        private final String displayName;

        TaskStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum TaskPriority {
        LOW("Baja"),
        MEDIUM("Media"),
        HIGH("Alta"),
        URGENT("Urgente");

        private final String displayName;

        TaskPriority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
