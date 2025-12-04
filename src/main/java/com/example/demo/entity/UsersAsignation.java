package com.example.demo.entity;

import java.time.LocalDateTime;

import com.example.demo.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users_asignation")
public class UsersAsignation {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true)
    private Department department;


    @ManyToOne
    @JoinColumn(name = "project_id", nullable = true)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = true)
    private TaskEntity task;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_asignado", length = 30) 
    private Role rolAsignado;

    private LocalDateTime dateAsignDateTime;

    @Override
    public String toString() {
        return "UsersAsignation{" +
                "id=" + id +
                ", user=" + (user != null ? user.getId() : null) +
                ", department=" + (department != null ? department.getId() : null) +
                ", project=" + (project != null ? project.getId() : null) +
                ", task=" + (task != null ? task.getId() : null) +
                ", rolAsignado=" + rolAsignado +
                ", dateAsignDateTime=" + dateAsignDateTime +
                '}';
    }

}
