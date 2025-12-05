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
import com.example.demo.dto.request.task.TaskCsvDTO;
import com.example.demo.dto.request.project.ProjectCsvDTO;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvHelper {

// Método para Proyectos
    public static List<ProjectCsvDTO> parseProjectsDto(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<ProjectCsvDTO> projects = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                ProjectCsvDTO dto = ProjectCsvDTO.builder()
                        .name(csvRecord.get("name"))
                        .description(csvRecord.get("description"))
                        .objectives(csvRecord.get("objectives"))
                        .priority(csvRecord.get("priority"))
                        .status(csvRecord.get("status"))
                        .startDate(csvRecord.get("startDate"))
                        .endDate(csvRecord.get("endDate"))
                        .budget(new BigDecimal(csvRecord.get("budget")))
                        .departmentId(Long.parseLong(csvRecord.get("departmentId"))) // Importante
                        .build();
                projects.add(dto);
            }
            return projects;
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear CSV de proyectos: " + e.getMessage());
        }
    }

// Método para Tareas
    public static List<TaskCsvDTO> parseTasksDto(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<TaskCsvDTO> tasks = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                TaskCsvDTO dto = TaskCsvDTO.builder()
                        .name(csvRecord.get("name"))
                        .description(csvRecord.get("description"))
                        .status(csvRecord.get("status"))
                        .priority(csvRecord.get("priority"))
                        .startDate(csvRecord.isMapped("startDate") ? csvRecord.get("startDate") : null)
                        .endDate(csvRecord.isMapped("endDate") ? csvRecord.get("endDate") : null)
                        .estimatedHours(Integer.parseInt(csvRecord.get("estimatedHours")))
                        .projectId(Long.parseLong(csvRecord.get("projectId"))) // Importante
                        .build();
                tasks.add(dto);
            }
            return tasks;
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear CSV de tareas: " + e.getMessage());
        }
    }

    public static boolean hasCSVFormat(MultipartFile file) {
        return "text/csv".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType());
    }




}