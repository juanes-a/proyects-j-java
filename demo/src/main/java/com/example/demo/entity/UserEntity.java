package com.example.demo.entity;

import java.util.List;
import java.util.Set;

import com.example.demo.enums.Role;

import com.example.demo.enums.Role;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Column(unique = true)
    private String username;


    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 30)
    public Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UsersAsignation> asignaciones;

    // Campo para activar/desactivar usuarios
    @Builder.Default
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
