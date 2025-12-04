package com.example.demo.util;

import com.example.demo.entity.Project;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfProjectReportGenerator {

    public static ByteArrayInputStream generate(List<Project> projects) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Cabecera
            com.lowagie.text.Font appFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLDITALIC, Color.GRAY);
            Paragraph appName = new Paragraph("ProjectS-J", appFont);
            appName.setAlignment(Element.ALIGN_CENTER);
            appName.setSpacingAfter(5f);
            document.add(appName);

            com.lowagie.text.Font dateFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.ITALIC, Color.DARK_GRAY);
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            Paragraph date = new Paragraph("Generado el: " + fecha, dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            date.setSpacingAfter(15f);
            document.add(date);

            // Título
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD, Color.BLACK);
            Paragraph title = new Paragraph("Reporte de Proyectos", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Gráficas
            document.add(generateChartImage("Proyectos por Estado", buildDatasetByStatus(projects)));
            document.add(generateChartImage("Proyectos por Prioridad", buildDatasetByPriority(projects)));
            document.add(generateChartImage("Presupuesto Total por Prioridad", buildDatasetByBudget(projects)));

            // Tabla de Proyectos
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{3, 2, 2, 2, 3, 3});

            com.lowagie.text.Font headerFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.BOLD, Color.WHITE);
            Color headerBg = new Color(0, 121, 182);

            addTableHeader(table, "Nombre", headerFont, headerBg);
            addTableHeader(table, "Prioridad", headerFont, headerBg);
            addTableHeader(table, "Estado", headerFont, headerBg);
            addTableHeader(table, "Presupuesto", headerFont, headerBg);
            addTableHeader(table, "Inicio", headerFont, headerBg);
            addTableHeader(table, "Fin", headerFont, headerBg);

            com.lowagie.text.Font bodyFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10);
            for (Project project : projects) {
                table.addCell(new Phrase(project.getName(), bodyFont));
                table.addCell(new Phrase(project.getPriority().getDisplayName(), bodyFont));
                table.addCell(new Phrase(project.getStatus().getDisplayName(), bodyFont));
                BigDecimal budget = project.getBudget();
                table.addCell(new Phrase(budget != null ? "$" + budget : "-", bodyFont));
                table.addCell(new Phrase(project.getStartDate().toString(), bodyFont));
                table.addCell(new Phrase(project.getEndDate().toString(), bodyFont));
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

    private static DefaultCategoryDataset buildDatasetByStatus(List<Project> projects) {
        Map<String, Integer> map = new HashMap<>();
        for (Project project : projects) {
            String key = project.getStatus().getDisplayName();
            map.put(key, map.getOrDefault(key, 0) + 1);
        }
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        map.forEach((k, v) -> dataset.addValue(v, "Proyectos", k));
        return dataset;
    }

    private static DefaultCategoryDataset buildDatasetByPriority(List<Project> projects) {
        Map<String, Integer> map = new HashMap<>();
        for (Project project : projects) {
            String key = project.getPriority().getDisplayName();
            map.put(key, map.getOrDefault(key, 0) + 1);
        }
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        map.forEach((k, v) -> dataset.addValue(v, "Proyectos", k));
        return dataset;
    }

    private static DefaultCategoryDataset buildDatasetByBudget(List<Project> projects) {
        Map<String, BigDecimal> map = new HashMap<>();
        for (Project project : projects) {
            String key = project.getPriority().getDisplayName();
            BigDecimal budget = project.getBudget() != null ? project.getBudget() : BigDecimal.ZERO;
            map.put(key, map.getOrDefault(key, BigDecimal.ZERO).add(budget));
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        map.forEach((k, v) -> dataset.addValue(v, "Presupuesto", k));
        return dataset;
    }

    private static Image generateChartImage(String title, DefaultCategoryDataset dataset) throws Exception {
        JFreeChart chart = ChartFactory.createBarChart(
                title,
                "", "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        // Configuración del título del gráfico
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        chart.getTitle().setPaint(new Color(45, 45, 45));

        // Fondo del gráfico
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);

        CategoryPlot plot = chart.getCategoryPlot();

        // Configuración del área de ploteo
        plot.setBackgroundPaint(new Color(248, 249, 250));
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(false);

        // Configuración de los ejes
        plot.getDomainAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.getDomainAxis().setTickLabelFont(new Font("Arial", Font.PLAIN, 11));
        plot.getDomainAxis().setLabelPaint(new Color(80, 80, 80));
        plot.getDomainAxis().setTickLabelPaint(new Color(100, 100, 100));

        plot.getRangeAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("Arial", Font.PLAIN, 11));
        plot.getRangeAxis().setLabelPaint(new Color(80, 80, 80));
        plot.getRangeAxis().setTickLabelPaint(new Color(100, 100, 100));

        // Configuración del renderizador de barras
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Paleta de colores profesional
        Color[] colors = {
                new Color(52, 152, 219),   // Azul
                new Color(26, 188, 156),   // Verde agua
                new Color(241, 196, 15),   // Amarillo
                new Color(231, 76, 60),    // Rojo
                new Color(142, 68, 173),   // Púrpura
                new Color(230, 126, 34),   // Naranja
                new Color(95, 39, 205),    // Violeta
                new Color(22, 160, 133)    // Verde
        };

        // Aplicar colores y efectos de gradiente
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            Color baseColor = colors[i % colors.length];
            Color lighterColor = new Color(
                    Math.min(255, baseColor.getRed() + 30),
                    Math.min(255, baseColor.getGreen() + 30),
                    Math.min(255, baseColor.getBlue() + 30)
            );

            GradientPaint gradient = new GradientPaint(
                    0, 0, lighterColor,
                    0, 300, baseColor
            );
            renderer.setSeriesPaint(i, gradient);
        }

        // Configuraciones adicionales de las barras
        renderer.setDrawBarOutline(false);
        renderer.setMaximumBarWidth(0.12);
        renderer.setItemMargin(0.05);
        renderer.setShadowVisible(false);

        // Añadir sombra suave a las barras
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());

        BufferedImage chartImage = chart.createBufferedImage(700, 400);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        ImageIO.write(chartImage, "png", chartOut);

        Image chartImg = Image.getInstance(chartOut.toByteArray());
        chartImg.setAlignment(Image.ALIGN_CENTER);
        chartImg.setSpacingAfter(25f);
        chartImg.scaleToFit(550, 320);
        return chartImg;
    }
}