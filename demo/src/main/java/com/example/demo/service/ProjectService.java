package com.example.demo.service;

import com.example.demo.controller.ProjectController.DepartmentDTO;
import com.example.demo.controller.ProjectController.ProjectRequest;
import com.example.demo.controller.ProjectController.ProjectResponse;
import com.example.demo.dto.*;
import com.example.demo.dto.request.project.ProjectCreateDTO;
import com.example.demo.dto.request.project.ProjectUpdateDTO;
import com.example.demo.entity.Project;
import com.example.demo.entity.Department;
import com.example.demo.enums.ProjectStatus;
import com.example.demo.enums.ProjectPriority;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.exception.ProjectNotFoundException;
import com.example.demo.exception.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProjectService {



    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // ============== Operaciones CRUD ==============

    @Transactional
    public ProjectResponse createProject(ProjectCreateDTO dto) {
        // 1. Validar departamento
        Department department = departmentRepository.findById(dto.getDepartmentId())
            .orElseThrow(() -> new BusinessException("Departamento no encontrado"));
        
        // 2. Validar fechas
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new BusinessException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        
        // 3. Crear entidad
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setDepartment(department); // Asegurar que department no sea null
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setBudget(dto.getBudget() != null ? BigDecimal.valueOf(dto.getBudget()) : null);
        project.setPriority(dto.getPriority());
        project.setStatus(dto.getStatus());
        
        // 4. Guardar
        Project savedProject = projectRepository.save(project);
        return mapToResponse(savedProject);
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectUpdateDTO dto) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));

        if (project.isCompleted()) {
            throw new BusinessException("Cannot modify a completed project");
        }

        if (!project.getName().equals(dto.getName())) {
            validateProjectName(dto.getName(), project.getDepartment().getId());
        }

        validateProjectDates(dto.getStartDate(), dto.getEndDate());

        if (project.isInProgress() && !project.getStartDate().equals(dto.getStartDate())) {
            throw new BusinessException("Cannot change start date of a project in progress");
        }

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setBudget(dto.getBudget());
        project.setPriority(dto.getPriority());
        
        // No actualizamos el status aquí, tiene sus propios endpoints
        // No actualizamos departmentId en updates (requeriría lógica adicional)

        return mapToResponse(projectRepository.save(project));
    }


    // ============== Gestión de estado ==============

    @Transactional
    public ProjectResponse startProject(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));

        if (!project.isPlanned()) {
            throw new BusinessException("Only planned projects can be started");
        }

        if (project.getStartDate().isAfter(LocalDate.now())) {
            throw new BusinessException("Cannot start project before its start date");
        }

        if (!project.getDepartment().getIsActive()) {
            throw new BusinessException("Cannot start project in an inactive department");
        }

        project.start();
        return mapToResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse completeProject(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));

        project.complete();
        return mapToResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse cancelProject(Long id, String reason) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));

        project.cancel();
        return mapToResponse(projectRepository.save(project));
    }

    // ============== Consultas especiales ==============
    public List<Project> getOverdueProjects() {
        return projectRepository.findByEndDateBeforeAndStatusNot(
            LocalDate.now(),
            ProjectStatus.COMPLETED
        );
    }

    public List<ProjectResponse> getUrgentProjects() {
        try {
            List<ProjectPriority> urgentPriorities = List.of(
                ProjectPriority.HIGH, 
                ProjectPriority.CRITICAL
            );
            
            return projectRepository.findByPriorityInAndStatusNot(
                urgentPriorities, 
                ProjectStatus.CANCELLED
            ).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        } catch (Exception e) {
            throw new BusinessException("Error fetching urgent projects: " + e.getMessage());
        }
    }

    public List<ProjectResponse> getProjectsEndingThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate endOfWeek = today.plusDays(7);
        return projectRepository.findByEndDateBetween(today, endOfWeek)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // ============== Métodos de apoyo ==============

    private Department validateDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new BusinessException("Department not found with id: " + departmentId));

        if (!department.getIsActive()) {
            throw new BusinessException("Cannot create project in inactive department");
        }

        return department;
    }

    private void validateProjectName(String name, Long departmentId) {
        if (projectRepository.existsByNameIgnoreCaseAndDepartmentId(name, departmentId)) {
            throw new BusinessException("Project name already exists in this department");
        }
    }

    private void validateProjectDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BusinessException("Start and end dates are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date cannot be after end date");
        }

        if (startDate.equals(endDate)) {
            throw new BusinessException("Project must last at least 1 day");
        }
    }

    private void validateStartDateNotInPast(LocalDate startDate) {
        if (startDate.isBefore(LocalDate.now())) {
            throw new BusinessException("Start date cannot be in the past");
        }
    }

    private ProjectResponse mapToResponse(Project project) {
        return new ProjectResponse(
            project.getId(),
            project.getName(),
            project.getDescription(),
            new DepartmentDTO(project.getDepartment().getId(), project.getDepartment().getName()),
            project.getStatus().name(),
            project.getPriority().name(),
            project.getStartDate(),
            project.getEndDate(),
            project.getBudget() != null ? project.getBudget().doubleValue() : null
        );
    }

     @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));

        if (!project.isPlanned() && !project.isCancelled()) {
            throw new BusinessException("Only planned or cancelled projects can be deleted");
        }

        projectRepository.delete(project);
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
        return mapToResponse(project);
    }
}