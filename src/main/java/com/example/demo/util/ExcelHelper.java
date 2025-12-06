package com.example.demo.util;

import com.example.demo.dto.request.project.ProjectCsvDTO;
import com.example.demo.dto.request.task.TaskCsvDTO; // <--- Importante
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    // ================= MÉTODO PARA PROYECTOS =================
    public static List<ProjectCsvDTO> excelToProjects(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            List<ProjectCsvDTO> projects = new ArrayList<>();

            // Iterar filas (saltando la cabecera en i=0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                ProjectCsvDTO project = new ProjectCsvDTO();
                // Orden asumido: name, description, objectives, priority, status, startDate, endDate, budget, departmentId
                project.setName(getCellValue(row.getCell(0)));
                project.setDescription(getCellValue(row.getCell(1)));
                project.setObjectives(getCellValue(row.getCell(2)));
                project.setPriority(getCellValue(row.getCell(3)));
                project.setStatus(getCellValue(row.getCell(4)));
                project.setStartDate(getCellValue(row.getCell(5)));
                project.setEndDate(getCellValue(row.getCell(6)));
                
                String budgetStr = getCellValue(row.getCell(7));
                if (!budgetStr.isEmpty()) project.setBudget(new BigDecimal(budgetStr));

                String deptIdStr = getCellValue(row.getCell(8));
                if (!deptIdStr.isEmpty()) {
                     // Excel devuelve números como 1.0, parseamos a double luego a long
                     double d = Double.parseDouble(deptIdStr);
                     project.setDepartmentId((long) d);
                }

                projects.add(project);
            }
            workbook.close();
            return projects;
        } catch (IOException e) {
            throw new RuntimeException("Fallo al parsear archivo Excel de proyectos: " + e.getMessage());
        }
    }

    // ================= MÉTODO PARA TAREAS (EL QUE FALTABA) =================
    public static List<TaskCsvDTO> excelToTasks(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            List<TaskCsvDTO> tasks = new ArrayList<>();

            // Iterar filas (saltando la cabecera en i=0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                TaskCsvDTO task = new TaskCsvDTO();
                // Orden asumido: name, description, status, priority, startDate, endDate, estimatedHours, projectId
                
                task.setName(getCellValue(row.getCell(0)));
                task.setDescription(getCellValue(row.getCell(1)));
                task.setStatus(getCellValue(row.getCell(2)));
                task.setPriority(getCellValue(row.getCell(3)));
                task.setStartDate(getCellValue(row.getCell(4)));
                task.setEndDate(getCellValue(row.getCell(5)));
                
                String hoursStr = getCellValue(row.getCell(6));
                if (!hoursStr.isEmpty()) {
                    try {
                        task.setEstimatedHours((int) Double.parseDouble(hoursStr));
                    } catch (NumberFormatException e) {
                        task.setEstimatedHours(0);
                    }
                }

                String projIdStr = getCellValue(row.getCell(7));
                if (!projIdStr.isEmpty()) {
                     try {
                        task.setProjectId((long) Double.parseDouble(projIdStr));
                     } catch (NumberFormatException e) {
                        // Manejar error si el ID no es número
                     }
                }

                tasks.add(task);
            }
            workbook.close();
            return tasks;
        } catch (IOException e) {
            throw new RuntimeException("Fallo al parsear archivo Excel de tareas: " + e.getMessage());
        }
    }

    // Método auxiliar para obtener valor de celda como String
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        
        // Manejo básico de tipos
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                // Si es una fecha formateada en Excel, intentamos devolverla como String ISO (YYYY-MM-DD)
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                yield String.valueOf(cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}