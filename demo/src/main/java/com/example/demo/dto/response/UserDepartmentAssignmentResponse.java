package com.example.demo.dto.response;

import java.time.LocalDateTime;
import com.example.demo.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class UserDepartmentAssignmentResponse {
    private Long assignmentId;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long departmentId;
    private String departmentName;
    private Role role;
    private LocalDateTime assignedDate;
     
}
