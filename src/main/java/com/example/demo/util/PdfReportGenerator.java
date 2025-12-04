package com.example.demo.util;

import com.example.demo.entity.TaskEntity;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfReportGenerator {

    public static ByteArrayInputStream generateTaskReport(List<TaskEntity> tasks) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Nombre del sistema
            com.lowagie.text.Font appFont = new com.lowagie.text.Font(Font.HELVETICA, 14, Font.BOLDITALIC, Color.GRAY);
            Paragraph appName = new Paragraph("ProjectS-J", appFont);
            appName.setAlignment(Element.ALIGN_CENTER);
            appName.setSpacingAfter(5f);
            document.add(appName);

            // Fecha de generación
            com.lowagie.text.Font dateFont = new com.lowagie.text.Font(Font.HELVETICA, 10, Font.ITALIC, Color.DARK_GRAY);
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            Paragraph date = new Paragraph("Generado el: " + fecha, dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            date.setSpacingAfter(15f);
            document.add(date);

            // Título del reporte
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(Font.HELVETICA, 18, Font.BOLD, Color.BLACK);
            Paragraph title = new Paragraph("Reporte de Tareas", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Gráfico de tareas por estado
            document.add(generatePieChartImage("Tareas por Estado", buildDataByStatus(tasks)));

            // Gráfico de tareas por prioridad
            document.add(generatePieChartImage("Tareas por Prioridad", buildDataByPriority(tasks)));

            // Tabla de tareas
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{3, 2, 2, 2, 3, 3});

            com.lowagie.text.Font headerFont = new com.lowagie.text.Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
            Color headerBg = new Color(0, 121, 182);

            addTableHeader(table, "Nombre", headerFont, headerBg);
            addTableHeader(table, "Estado", headerFont, headerBg);
            addTableHeader(table, "Prioridad", headerFont, headerBg);
            addTableHeader(table, "Horas Est.", headerFont, headerBg);
            addTableHeader(table, "Inicio", headerFont, headerBg);
            addTableHeader(table, "Fin", headerFont, headerBg);

            com.lowagie.text.Font bodyFont = new com.lowagie.text.Font(Font.HELVETICA, 10);

            for (TaskEntity task : tasks) {
                table.addCell(new Phrase(task.getName(), bodyFont));
                table.addCell(new Phrase(task.getStatus().getDisplayName(), bodyFont));
                table.addCell(new Phrase(task.getPriority().getDisplayName(), bodyFont));
                table.addCell(new Phrase(task.getEstimatedHours() != null ? task.getEstimatedHours().toString() : "-", bodyFont));
                table.addCell(new Phrase(task.getStartDate() != null ? task.getStartDate().toString() : "-", bodyFont));
                table.addCell(new Phrase(task.getEndDate() != null ? task.getEndDate().toString() : "-", bodyFont));
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private static void addTableHeader(PdfPTable table, String title, com.lowagie.text.Font font, Color bgColor) {
        PdfPCell header = new PdfPCell(new Phrase(title, font));
        header.setBackgroundColor(bgColor);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(5);
        table.addCell(header);
    }

    private static Map<String, Integer> buildDataByStatus(List<TaskEntity> tasks) {
        Map<String, Integer> map = new HashMap<>();
        for (TaskEntity task : tasks) {
            String key = task.getStatus().getDisplayName();
            map.put(key, map.getOrDefault(key, 0) + 1);
        }
        return map;
    }

    private static Map<String, Integer> buildDataByPriority(List<TaskEntity> tasks) {
        Map<String, Integer> map = new HashMap<>();
        for (TaskEntity task : tasks) {
            String key = task.getPriority().getDisplayName();
            map.put(key, map.getOrDefault(key, 0) + 1);
        }
        return map;
    }

    private static com.lowagie.text.Image generatePieChartImage(String title, Map<String, Integer> data) throws Exception {
        // Crear gráfico de pastel con XChart
        PieChart chart = new PieChartBuilder()
                .width(600)
                .height(450)
                .title(title)
                .build();

        // Configurar estilo moderno y profesional
        PieStyler styler = chart.getStyler();

        // Fondo y tema general
        styler.setChartBackgroundColor(Color.WHITE);
        styler.setPlotBackgroundColor(Color.WHITE);
        styler.setPlotBorderVisible(false);
        styler.setChartTitleVisible(true);
        styler.setChartTitleFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 20));
        styler.setChartTitlePadding(15);

        // Configurar colores modernos y profesionales
        Color[] colors = {
                new Color(99, 102, 241),   // Índigo vibrante
                new Color(16, 185, 129),   // Esmeralda
                new Color(245, 158, 11),   // Ámbar
                new Color(239, 68, 68),    // Rojo coral
                new Color(139, 92, 246),   // Violeta
                new Color(6, 182, 212),    // Cian
                new Color(34, 197, 94),    // Verde
                new Color(251, 146, 60)    // Naranja
        };
        styler.setSeriesColors(colors);

        // Configurar etiquetas
        styler.setLabelType(PieStyler.LabelType.NameAndPercentage);
        styler.setLabelsVisible(true);
        styler.setLabelsFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
        styler.setLabelsFontColor(new Color(55, 65, 81));
        styler.setLabelsDistance(1.15);

        // Configurar leyenda
        styler.setLegendVisible(true);
        styler.setLegendPosition(Styler.LegendPosition.OutsideE);
        styler.setLegendSeriesLineLength(12);
        styler.setLegendPadding(10);
        styler.setLegendFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));

        // Configurar apariencia del gráfico
        styler.setPlotContentSize(0.85);
        styler.setStartAngleInDegrees(90);
        styler.setCircular(true);
        styler.setDonutThickness(0.0); // Gráfico completo, no donut

        // Efectos visuales modernos
        styler.setSumVisible(false);


        // Agregar datos al gráfico
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            chart.addSeries(entry.getKey(), entry.getValue());
        }

        // Convertir a imagen
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        BitmapEncoder.saveBitmap(chart, chartOut, BitmapEncoder.BitmapFormat.PNG);

        com.lowagie.text.Image chartImg = com.lowagie.text.Image.getInstance(chartOut.toByteArray());
        chartImg.setAlignment(com.lowagie.text.Image.ALIGN_CENTER);
        chartImg.setSpacingAfter(20f);

        // Escalar la imagen para que se vea bien en el PDF
        chartImg.scalePercent(75f);

        return chartImg;
    }
}