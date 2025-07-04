package com.example.demo.service;

import com.example.demo.controller.ProjectController.DepartmentDTO;
import com.example.demo.controller.ProjectController.ProjectResponse;
import com.example.demo.dto.request.project.ProjectCreateDTO;
import com.example.demo.dto.request.project.ProjectUpdateDTO;
import com.example.demo.dto.response.ProjectResponseDTO;
import com.example.demo.entity.Project;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.UsersAsignation;
import com.example.demo.entity.Department;
import com.example.demo.enums.ProjectStatus;
import com.example.demo.enums.Role;
import com.example.demo.enums.ProjectPriority;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UsersAsignationRepository;

import jakarta.persistence.EntityNotFoundException;

import com.example.demo.repository.DepartmentRepository;
import com.example.demo.exception.ProjectNotFoundException;
import com.example.demo.exception.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Slf4j
@Service
@Transactional
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UsersAsignationRepository usersAsignationRepository;

    @Autowired
    private UserRepository userRepository;


    private final ModelMapper modelMapper;
    
    @Autowired
    public ProjectService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
            this.modelMapper = new ModelMapper();
        }
    public List<Project> getAllProjectEntities() {
        return projectRepository.findAll();
    }





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

    public Project updateProject(Long id, ProjectUpdateDTO updateDTO) {
        Project existingProject = projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));
        
        // Actualiza solo los campos permitidos
        existingProject.setName(updateDTO.getName());
        existingProject.setDescription(updateDTO.getDescription());
        existingProject.setObjectives(updateDTO.getObjectives());
        existingProject.setStartDate(updateDTO.getStartDate());
        existingProject.setEndDate(updateDTO.getEndDate());
        existingProject.setBudget(updateDTO.getBudget());
        existingProject.setPriority(updateDTO.getPriority());
        existingProject.setStatus(updateDTO.getStatus());

        
        // Validación de fechas adicional
        if (updateDTO.getStartDate().isAfter(updateDTO.getEndDate())) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        return projectRepository.save(existingProject);
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
    public List<Project> searchProjects(
            String name,
            Long departmentId,
            ProjectStatus status,
            ProjectPriority priority,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minBudget,
            BigDecimal maxBudget
    ) {
        return projectRepository.findAll().stream()
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> departmentId == null || (p.getDepartment() != null && p.getDepartment().getId().equals(departmentId)))
                .filter(p -> status == null || p.getStatus() == status)
                .filter(p -> priority == null || p.getPriority() == priority)
                .filter(p -> startDate == null || !p.getStartDate().isBefore(startDate))
                .filter(p -> endDate == null || !p.getEndDate().isAfter(endDate))
                .filter(p -> minBudget == null || p.getBudget().compareTo(minBudget) >= 0)
                .filter(p -> maxBudget == null || p.getBudget().compareTo(maxBudget) <= 0)
                .toList();
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


    public Project getProjectEntityById(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
    }


    // En Contro de proyectos
    public long countProjectsByDepartment(Long departmentId) {
        try {
            // Verifica que el departmentId no sea nulo
            if (departmentId == null) {
                throw new IllegalArgumentException("Department ID cannot be null");
            }
            return projectRepository.countByDepartmentId(departmentId);
        } catch (Exception e) {
            // Log del error para diagnóstico
            logger.error("Error counting projects for department {}: {}", departmentId, e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error counting projects", 
                e
            );
        }
    }


    public long countByDepartmentIdAndStatus(Long departmentId, ProjectStatus status) {
        try {
            if (departmentId == null || status == null) {
                throw new IllegalArgumentException("Department ID and Status cannot be null");
            }
            return projectRepository.countByDepartmentIdAndStatus(departmentId, status);
        } catch (Exception e) {
            logger.error("Error counting projects for department {} with status {}: {}", 
                departmentId, status, e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error counting projects by status", 
                e
            );
        }
    }

    public double getTotalBudgetUsedByDepartment(Long departmentId) {
        try {
            if (departmentId == null) {
                throw new IllegalArgumentException("Department ID cannot be null");
            }
            Double total = projectRepository.sumBudgetByDepartmentId(departmentId);
            return total != null ? total : 0.0;
        } catch (Exception e) {
            logger.error("Error calculating total budget used for department {}: {}", 
                departmentId, e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error calculating total budget used", 
                e
            );
        }
    }

    public List<ProjectResponseDTO> getProjectsByDepartment(Long departmentId) {
        List<Project> projects = projectRepository.findByDepartmentId(departmentId);
        
        return projects.stream()
            .map(project -> {
                ProjectResponseDTO dto = ProjectResponseDTO.fromEntity(project);
                // Asegurar que los campos del departamento están poblados
                if (dto.getDepartmentId() == null) {
                    dto.setDepartmentId(departmentId);
                    dto.setDepartmentName(project.getDepartment().getName());
                }
                return dto;
            })
            .collect(Collectors.toList());
    }



    public UsersAsignation assignUserToProject(String usernameOrEmail, Long projectId, Role role) {
        // Validar usuario
        UserEntity user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado: " + usernameOrEmail));

        // Validar proyecto
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BusinessException("Proyecto no encontrado con ID: " + projectId));

        // Asignar rol
        user.setRole(role);
        userRepository.save(user);

        // Verificar si ya está asignado al proyecto
        Optional<UsersAsignation> existingAssignment = usersAsignationRepository
            .findByUserAndProject(user, project);

        if (existingAssignment.isPresent()) {
            throw new BusinessException("El usuario ya está asignado a este proyecto.");
        }

        // Crear asignación
        UsersAsignation assignment = new UsersAsignation();
        assignment.setUser(user);
        assignment.setProject(project);
        assignment.setRolAsignado(role);
        assignment.setDateAsignDateTime(LocalDateTime.now());

        logger.info("Asignando usuario al proyecto {}...", project.getId());
        UsersAsignation saved = usersAsignationRepository.save(assignment);
        usersAsignationRepository.flush(); // Fuerza el INSERT
        logger.info("Asignación guardada: {}", saved);

        return saved;
    }

    public Optional<UsersAsignation> getUserProjectAssignment(String usernameOrEmail) {
        // 1. Validación básica del input
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            throw new IllegalArgumentException("Username/email no puede estar vacío");
        }

        // 2. Normalización del input
        String normalizedInput = usernameOrEmail.trim().toLowerCase();

        // 3. Buscar usuario (con mejor manejo de errores)
        UserEntity user = userRepository.findByUsernameOrEmail(normalizedInput, normalizedInput)
            .orElseThrow(() -> new EntityNotFoundException(
                "Usuario '" + normalizedInput + "' no encontrado"));

        // 4. Obtener asignación activa más reciente
        return usersAsignationRepository.findTopByUserAndProjectIsNotNullOrderByDateAsignDateTimeDesc(user);
    }




}





