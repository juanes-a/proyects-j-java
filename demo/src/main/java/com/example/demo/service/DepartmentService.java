package com.example.demo.service;

import com.example.demo.entity.Department;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

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
    }
}
