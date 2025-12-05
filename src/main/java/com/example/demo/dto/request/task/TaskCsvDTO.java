package main.java.com.example.demo.dto.request.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCsvDTO {
    private String name;
    private String description;
    private String status;        // Lo recibimos como String y lo convertimos en el servicio
    private String priority;      // Lo recibimos como String y lo convertimos en el servicio
    private String startDate;     // Lo recibimos como String y lo parseamos en el servicio
    private String endDate;       // Lo recibimos como String y lo parseamos en el servicio
    private Integer estimatedHours;
    private Long projectId;       // Este es el dato clave para vincular la tarea
}