package com.example.demo.dto.request.task;

import com.example.demo.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignUserToTaskRequest {
    private String usernameOrEmail;
    private Long taskId;
    private Role role;
}
