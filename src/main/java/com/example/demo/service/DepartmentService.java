package com.example.demo.service;

import com.example.demo.entity.Department;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.UsersAsignation;
import com.example.demo.enums.ProjectStatus;
import com.example.demo.enums.Role;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UsersAsignationRepository;

import jakarta.persistence.EntityNotFoundException;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    private UsersAsignationRepository usersAsignationRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;
    
    
    @Autowired
    private DepartmentRepository departmentRepository;


    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }


    /**
     * CREAR NUEVO DEPARTAMENTO
     * - Valida que el nombre no exista
     * - Valida datos de entrada
     * - Guarda en base de datos
     */
    public Department createDepartment(Department department) {
        // VALIDACIÓN: Verificar que el nombre no exista
        if (departmentRepository.existsByNameIgnoreCase(department.getName())) {
            throw new BusinessException("Ya existe un departamento con el nombre: " + department.getName());
        }

        // VALIDACIÓN: Nombre no puede estar vacío
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            throw new BusinessException("El nombre del departamento es obligatorio");
        }

        // VALIDACIÓN: Presupuesto no puede ser negativo
        if (department.getBudget() != null && department.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El presupuesto no puede ser negativo");
        }

        // LÓGICA DE NEGOCIO: Asegurar que está activo por defecto
        if (department.getIsActive() == null) {
            department.setIsActive(true);
        }

        // GUARDAR en base de datos
        return departmentRepository.save(department);
    }

    /**
     * OBTENER DEPARTAMENTO POR ID
     * - Busca por ID
     * - Lanza excepción si no existe
     */
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con ID: " + id));
    }

    /**
     * OBTENER DEPARTAMENTO POR NOMBRE
     * - Busca por nombre (ignora mayúsculas)
     * - Retorna Optional para manejo seguro
     */
    public Optional<Department> getDepartmentByName(String name) {
        return departmentRepository.findByNameIgnoreCase(name);
    }

    /**
     * OBTENER TODOS LOS DEPARTAMENTOS
     * - Sin filtros, devuelve todo
     */
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    /**
     * OBTENER SOLO DEPARTAMENTOS ACTIVOS
     * - Filtro por isActive = true
     */
    public List<Department> getActiveDepartments() {
        return departmentRepository.findByIsActiveTrue();
    }

    /**
     * OBTENER DEPARTAMENTOS CON PRESUPUESTO ACTIVO
     * - Solo los que tienen presupuesto > 0 y están activos
     */
    public List<Department> getDepartmentsWithActiveBudget() {
        return departmentRepository.findActiveDepartmentsWithBudget();
    }

    /**
     * ACTUALIZAR DEPARTAMENTO
     * - Verifica que existe
     * - Valida cambios
     * - Actualiza solo campos modificados
     */
    public Department updateDepartment(Long id, Department updatedDepartment) {
        // BUSCAR departamento existente
        Department existingDepartment = getDepartmentById(id);

        // VALIDACIÓN: Si cambia el nombre, verificar que no exista otro con ese nombre
        if (!existingDepartment.getName().equalsIgnoreCase(updatedDepartment.getName())) {
            if (departmentRepository.existsByNameIgnoreCase(updatedDepartment.getName())) {
                throw new BusinessException("Ya existe otro departamento con el nombre: " + updatedDepartment.getName());
            }
            existingDepartment.setName(updatedDepartment.getName());
        }

        // ACTUALIZAR campos si vienen en la petición
        if (updatedDepartment.getDescription() != null) {
            existingDepartment.setDescription(updatedDepartment.getDescription());
        }

        if (updatedDepartment.getBudget() != null) {
            // VALIDACIÓN: Presupuesto no negativo
            if (updatedDepartment.getBudget().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("El presupuesto no puede ser negativo");
            }
            existingDepartment.setBudget(updatedDepartment.getBudget());
        }

        if (updatedDepartment.getLocation() != null) {
            existingDepartment.setLocation(updatedDepartment.getLocation());
        }

        // GUARDAR cambios
        return departmentRepository.save(existingDepartment);
    }

    /**
     * ACTIVAR DEPARTAMENTO
     * - Cambia isActive a true
     * - Usa método de la entidad
     */
    public Department activateDepartment(Long id) {
        Department department = getDepartmentById(id);
        department.activate(); // Método de la entidad
        return departmentRepository.save(department);
    }

    /**
     * DESACTIVAR DEPARTAMENTO (SOFT DELETE)
     * - Cambia isActive a false
     * - NO borra de la base de datos
     * - Mantiene historial y relaciones
     */
    public Department deactivateDepartment(Long id) {
        Department department = getDepartmentById(id);
        department.deactivate(); // Método de la entidad
        return departmentRepository.save(department);
    }

    /**
     * ELIMINAR PERMANENTEMENTE (HARD DELETE)
     * - Solo para casos extremos
     * - Borra físicamente de la base de datos
     */
    public void deleteDepartmentPermanently(Long id) {
        Department department = getDepartmentById(id);

        // VALIDACIÓN DE NEGOCIO: Solo eliminar si está inactivo
        if (department.getIsActive()) {
            throw new BusinessException("No se puede eliminar un departamento activo. Desactívalo primero.");
        }

        departmentRepository.delete(department);
    }

    /**
     * BUSCAR DEPARTAMENTOS POR FILTROS
     * - Búsqueda flexible con múltiples criterios
     * - Parámetros opcionales (pueden ser null)
     */
    public List<Department> searchDepartments(String name, String location, Boolean isActive,
                                              BigDecimal minBudget, BigDecimal maxBudget) {
        return departmentRepository.findDepartmentsByFilters(name, location, isActive, minBudget, maxBudget);
    }

    /**
     * BUSCAR POR TEXTO EN NOMBRE O DESCRIPCIÓN
     * - Búsqueda de texto libre
     */
    public List<Department> searchByText(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllDepartments();
        }
        return departmentRepository.findByNameOrDescriptionContaining(searchTerm.trim());
    }

    /**
     * OBTENER ESTADÍSTICAS
     * - Información agregada del sistema
     */
    public DepartmentStats getDepartmentStatistics() {
        long totalDepartments = departmentRepository.count();
        long activeDepartments = departmentRepository.countByIsActiveTrue();
        BigDecimal totalBudget = departmentRepository.getTotalActiveBudget();

        return new DepartmentStats(totalDepartments, activeDepartments, totalBudget);
    }

    /**
     * VERIFICAR SI DEPARTAMENTO TIENE PRESUPUESTO SUFICIENTE
     * - Lógica de negocio para validar operaciones
     */
    public boolean hasSufficientBudget(Long departmentId, BigDecimal requiredAmount) {
        Department department = getDepartmentById(departmentId);

        if (!department.getIsActive()) {
            return false;
        }

        if (!department.hasActiveBudget()) {
            return false;
        }

        return department.getBudget().compareTo(requiredAmount) >= 0;
    }

    /**
     * CLASE INTERNA PARA ESTADÍSTICAS
     * - DTO simple para devolver datos agregados
     */
    public static class DepartmentStats {
        private final long totalDepartments;
        private final long activeDepartments;
        private final BigDecimal totalBudget;

        public DepartmentStats(long totalDepartments, long activeDepartments, BigDecimal totalBudget) {
            this.totalDepartments = totalDepartments;
            this.activeDepartments = activeDepartments;
            this.totalBudget = totalBudget != null ? totalBudget : BigDecimal.ZERO;
        }

        public long getTotalDepartments() { return totalDepartments; }
        public long getActiveDepartments() { return activeDepartments; }
        public BigDecimal getTotalBudget() { return totalBudget; }

        public void setActiveDepartments(long l) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'setActiveDepartments'");
        }

        public void setTotalBudget(Object object) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'setTotalBudget'");
        }

        public void setTotalDepartments(long l) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'setTotalDepartments'");
        }
    }



     /**
     * Asigna un usuario a un departamento con un rol específico
     */
    public UsersAsignation assignUserToDepartment(String usernameOrEmail, Long departmentId, Role role) {
        // Validar que el usuario existe usando username o email
        UserEntity user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado: " + usernameOrEmail));
        
        // Validar que el departamento existe
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new BusinessException("Departamento no encontrado con ID: " + departmentId));
        
        // Validar que el rol es válido para departamento
        user.setRole(role); // <-- Esta es la línea clave
        userRepository.save(user);

        // Verificar si ya existe una asignación de departamento para este usuario
        Optional<UsersAsignation> existingAssignment = usersAsignationRepository
            .findByUserAndDepartmentIsNotNull(user);
        
        if (existingAssignment.isPresent()) {
            throw new BusinessException("El usuario " + usernameOrEmail + " ya tiene un departamento asignado. " +
                "Solo se permite una asignación de departamento por usuario.");
        }
        
        // Crear nueva asignación
        UsersAsignation assignment = new UsersAsignation();
        assignment.setUser(user);
        assignment.setDepartment(department);
        assignment.setRolAsignado(role);
        assignment.setDateAsignDateTime(LocalDateTime.now());
        
        return usersAsignationRepository.save(assignment);
    }

    

    /**
     * Actualiza el rol de un usuario en su departamento asignado
     */
    public UsersAsignation updateUserDepartmentRole(String usernameOrEmail, Role newRole) {
        UserEntity user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado: " + usernameOrEmail));
        
        UsersAsignation assignment = usersAsignationRepository
            .findByUserAndDepartmentIsNotNull(user)
            .orElseThrow(() -> new BusinessException("El usuario " + usernameOrEmail + " no tiene un departamento asignado"));
        
        if (!isValidDepartmentRole(newRole)) {
            throw new BusinessException("El rol " + newRole + " no es válido para asignación de departamento");
        }
        
        assignment.setRolAsignado(newRole);
        assignment.setDateAsignDateTime(LocalDateTime.now());
        
        return usersAsignationRepository.save(assignment);
    }

    /**
     * Remueve la asignación de departamento de un usuario
     */
    public void removeUserFromDepartment(String usernameOrEmail) {
        UserEntity user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado: " + usernameOrEmail));
        
        UsersAsignation assignment = usersAsignationRepository
            .findByUserAndDepartmentIsNotNull(user)
            .orElseThrow(() -> new BusinessException("El usuario " + usernameOrEmail + " no tiene un departamento asignado"));
        
        usersAsignationRepository.delete(assignment);
    }

    /**
     * Obtiene la asignación de departamento de un usuario
     */
    public Optional<UsersAsignation> getUserDepartmentAssignment(String usernameOrEmail) {
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
        return usersAsignationRepository.findTopByUserAndDepartmentIsNotNullOrderByDateAsignDateTimeDesc(user);
    }

    /**
     * Obtiene todos los usuarios asignados a un departamento
     */
    public List<UsersAsignation> getUsersByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new BusinessException("Departamento no encontrado con ID: " + departmentId));
        
        return usersAsignationRepository.findByDepartment(department);
    }

        /**
     * Verifica si un usuario tiene acceso a un departamento específico
     */
    public boolean hasAccessToDepartment(String usernameOrEmail, Long departmentId) {
        Optional<UsersAsignation> assignment = getUserDepartmentAssignment(usernameOrEmail);
        return assignment.isPresent() && 
               assignment.get().getDepartment().getId().equals(departmentId);
    }

    /**
     * Obtiene todos los usuarios con rol ADMIN_DEPT en un departamento específico
     */
    public List<UsersAsignation> getDepartmentAdmins(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new BusinessException("Departamento no encontrado con ID: " + departmentId));
        
        return usersAsignationRepository.findByDepartmentAndRolAsignado(department, Role.ADMIN_DEPT);
    }



    /**
     * Verifica si un rol es válido para asignación de departamento
     */
    private boolean isValidDepartmentRole(Role role) {
        return role == Role.ADMIN_DEPT || role == Role.ADMIN_COLLAB || role == Role.COLLAB;
    }

    /**
     * Cambia un usuario de departamento (remueve del actual y asigna al nuevo)
     */
    public UsersAsignation transferUserToDepartment(String usernameOrEmail, Long newDepartmentId, Role role) {
        // Remover asignación actual si existe
        try {
            removeUserFromDepartment(usernameOrEmail);
        } catch (BusinessException e) {
            // Si no tiene departamento asignado, continúa con la asignación
        }
        
        // Asignar al nuevo departamento
        return assignUserToDepartment(usernameOrEmail, newDepartmentId, role);
    }


        public String calculateProductivity(Long departmentId) {
        // Implementa tu lógica de productividad aquí
        int completedProjects = projectRepository.countByDepartmentIdAndStatus(departmentId, ProjectStatus.COMPLETED);
        
        long totalProjects = projectRepository.countByDepartmentId(departmentId);
        
        if (totalProjects == 0) return "N/A";
        
        double percentage = (completedProjects * 100.0) / totalProjects;
        
        if (percentage > 80) return "High";
        if (percentage > 50) return "Medium";
        return "Low";
    }



}
