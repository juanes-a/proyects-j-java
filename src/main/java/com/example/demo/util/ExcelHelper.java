package com.example.demo.util;

import com.example.demo.dto.request.project.ProjectCsvDTO;
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

    public static List<ProjectCsvDTO> excelToProjects(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            List<ProjectCsvDTO> projects = new ArrayList<>();

            // Iterar filas (saltando la cabecera)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                ProjectCsvDTO project = new ProjectCsvDTO();
                // Asumiendo orden: name, description, objectives, priority, status, startDate, endDate, budget, departmentId
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
                // Manejar si viene como 1.0 (double) desde excel
                if (!deptIdStr.isEmpty()) {
                     double d = Double.parseDouble(deptIdStr);
                     project.setDepartmentId((long) d);
                }

                projects.add(project);
            }
            workbook.close();
            return projects;
        } catch (IOException e) {
            throw new RuntimeException("Fallo al parsear archivo Excel: " + e.getMessage());
        }
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}