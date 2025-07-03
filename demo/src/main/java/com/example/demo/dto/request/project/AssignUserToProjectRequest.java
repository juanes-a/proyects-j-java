package com.example.demo.dto.request.project;

import com.example.demo.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignUserToProjectRequest {
    private String usernameOrEmail;
    private Long projectId;
    private Role role;
}
