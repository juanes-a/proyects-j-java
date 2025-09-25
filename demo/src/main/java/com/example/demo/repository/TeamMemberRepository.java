package com.example.demo.repository;

import com.example.demo.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    
    // Find team members by department ID
    List<TeamMember> findByDepartmentId(Long departmentId);
    
    // Find team member by email
    Optional<TeamMember> findByEmail(String email);
    
    // Find team members by role
    List<TeamMember> findByRole(String role);
    
    // Custom query to find team members by department name
    @Query("SELECT tm FROM TeamMember tm JOIN tm.department d WHERE d.name = :departmentName")
    List<TeamMember> findByDepartmentName(@Param("departmentName") String departmentName);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Count team members by department
    long countByDepartmentId(Long departmentId);
}
