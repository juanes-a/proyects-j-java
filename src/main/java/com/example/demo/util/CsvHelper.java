package main.java.com.example.demo.util;

import com.example.demo.entity.Project;
import com.example.demo.entity.TaskEntity;
import com.example.demo.enums.ProjectPriority;
import com.example.demo.enums.ProjectStatus;
import com.example.demo.entity.TaskEntity.TaskPriority;
import com.example.demo.entity.TaskEntity.TaskStatus;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvHelper {

    public static List<Project> csvToProjects(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<Project> projects = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Project project = new Project();
                project.setName(csvRecord.get("name"));
                project.setDescription(csvRecord.get("description"));
                project.setObjectives(csvRecord.get("objectives"));
                project.setPriority(ProjectPriority.valueOf(csvRecord.get("priority").toUpperCase()));
                project.setStatus(ProjectStatus.valueOf(csvRecord.get("status").toUpperCase()));
                project.setStartDate(LocalDate.parse(csvRecord.get("startDate"))); // Formato YYYY-MM-DD
                project.setEndDate(LocalDate.parse(csvRecord.get("endDate")));
                project.setBudget(new BigDecimal(csvRecord.get("budget")));
                
                // Nota: Asignaremos el Department ID temporalmente o lo manejaremos en el servicio
                // Aquí solo devolvemos la estructura básica. Necesitas un DTO intermedio si quieres pasar el ID limpio.
                // Para este ejemplo, asumiremos que tienes un DTO o manejas la lógica de ID en el servicio.
                
                projects.add(project);
            }
            return projects;
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear CSV de proyectos: " + e.getMessage());
        }
    }

    public static List<TaskEntity> csvToTasks(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<TaskEntity> tasks = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                TaskEntity task = new TaskEntity();
                task.setName(csvRecord.get("name"));
                task.setDescription(csvRecord.get("description"));
                task.setStatus(TaskStatus.valueOf(csvRecord.get("status").toUpperCase()));
                task.setPriority(TaskPriority.valueOf(csvRecord.get("priority").toUpperCase()));
                
                // Las tareas usan LocalDateTime
                if(csvRecord.isMapped("startDate") && !csvRecord.get("startDate").isEmpty())
                    task.setStartDate(LocalDateTime.parse(csvRecord.get("startDate"))); 
                
                if(csvRecord.isMapped("endDate") && !csvRecord.get("endDate").isEmpty())
                    task.setEndDate(LocalDateTime.parse(csvRecord.get("endDate")));

                task.setEstimatedHours(Integer.parseInt(csvRecord.get("estimatedHours")));
                
                tasks.add(task);
            }
            return tasks;
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear CSV de tareas: " + e.getMessage());
        }
    }
}