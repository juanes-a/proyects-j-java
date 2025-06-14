package com.example.demo.enums;

/**
 * ENUM PARA PRIORIDADES DEL PROYECTO
 *
 * Define los niveles de prioridad que puede tener un proyecto
 * para facilitar la gestión y ordenamiento por importancia.
 *
 * NIVELES:
 * - LOW: Prioridad baja, no urgente
 * - MEDIUM: Prioridad media, importancia moderada
 * - HIGH: Prioridad alta, importante
 * - CRITICAL: Prioridad crítica, máxima urgencia
 */
public enum ProjectPriority {
    LOW("Baja", 1),
    MEDIUM("Media", 2),
    HIGH("Alta", 3),
    CRITICAL("Crítica", 4);

    private final String displayName;
    private final int level;

    ProjectPriority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Verifica si esta prioridad es mayor que otra
     */
    public boolean isHigherThan(ProjectPriority other) {
        return this.level > other.level;
    }

    /**
     * Verifica si es prioridad crítica o alta
     */
    public boolean isUrgent() {
        return this == CRITICAL || this == HIGH;
    }
}