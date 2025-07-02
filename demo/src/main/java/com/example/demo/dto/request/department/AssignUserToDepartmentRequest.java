package com.example.demo.dto.request.department;

import com.example.demo.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignUserToDepartmentRequest {
    private String usernameOrEmail;
    private Long departmentId;
    private Role role;
}