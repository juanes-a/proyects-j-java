package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Activity;
import com.example.demo.repository.ActivityRepository;

@Service
public class ActivityService {
    
    @Autowired
    private ActivityRepository activityRepository;
    
    /**
     * Crear una nueva actividad
     */
    public Activity createActivity(String type, String description) {
        Activity activity = new Activity();
        activity.setType(type);
        activity.setDescription(description);
        activity.setCreatedAt(LocalDateTime.now());
        return activityRepository.save(activity);
    }
    
    /**
     * Registrar actividad cuando se crea un departamento
     */
    public void logDepartmentCreated(String departmentName) {
        String description = String.format("Department '%s' was created", departmentName);
        createActivity("DEPARTMENT_CREATED", description);
    }
    
    /**
     * Registrar actividad cuando se actualiza un departamento
     */
    public void logDepartmentUpdated(String departmentName) {
        String description = String.format("Department '%s' was updated", departmentName);
        createActivity("DEPARTMENT_UPDATED", description);
    }
    
    /**
     * Registrar actividad cuando se elimina un departamento
     */
    public void logDepartmentDeleted(String departmentName) {
        String description = String.format("Department '%s' was deleted", departmentName);
        createActivity("DEPARTMENT_DELETED", description);
    }
    
    /**
     * Registrar actividad cuando se crea un proyecto
     */
    public void logProjectCreated(String projectName) {
        String description = String.format("Project '%s' was created", projectName);
        createActivity("PROJECT_CREATED", description);
    }
    
    /**
     * Registrar actividad cuando se actualiza un proyecto
     */
    public void logProjectUpdated(String projectName) {
        String description = String.format("Project '%s' was updated", projectName);
        createActivity("PROJECT_UPDATED", description);
    }
    
    /**
     * Registrar actividad cuando se añade un miembro al equipo
     */
    public void logTeamMemberAdded(String memberName) {
        String description = String.format("Team member '%s' was added", memberName);
        createActivity("TEAM_MEMBER_ADDED", description);
    }
    
    /**
     * Registrar actividad cuando se genera un reporte
     */
    public void logReportGenerated(String reportType) {
        String description = String.format("Report '%s' was generated", reportType);
        createActivity("REPORT_GENERATED", description);
    }
    
    /**
     * Registrar actividad personalizada
     */
    public void logCustomActivity(String type, String description) {
        createActivity(type, description);
    }
    
    /**
     * Obtener actividades recientes
     */
    public List<Activity> getRecentActivities() {
        return activityRepository.findTop5ByOrderByCreatedAtDesc();
    }
    
    /**
     * Crear datos de prueba para testing
     */
    public void createSampleActivities() {
        createActivity("DEPARTMENT_CREATED", "Marketing Department was created");
        createActivity("PROJECT_UPDATED", "Website Redesign project was updated");
        createActivity("TEAM_MEMBER_ADDED", "John Doe was added to the team");
        createActivity("REPORT_GENERATED", "Monthly budget report was generated");
        createActivity("DEPARTMENT_UPDATED", "IT Department budget was updated");
    }
}