package com.banco.cuadre;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.util.*;

public class EscritorExcel {
    private static final Logger logger = LoggerFactory.getLogger(EscritorExcel.class);
    
    private SXSSFWorkbook workbook;
    
    private CellStyle styleHeader;
    private CellStyle styleHeaderLeft;
    private CellStyle styleCelda;
    private CellStyle styleCeldaCenter;
    private CellStyle styleCeldaBold;
    private CellStyle styleCeldaOk;
    private CellStyle styleCeldaError;
    private CellStyle styleCeldaPK;
    private CellStyle styleHeaderPK;
    
    public interface ProgressCallback {
        void onProgress(int percent, String message);
    }
    
    private void crearEstilos() {
        styleHeader = crearEstilo(IndexedColors.RED.getIndex(), IndexedColors.WHITE.getIndex(), true, (short) 10, HorizontalAlignment.CENTER);
        styleHeaderLeft = crearEstilo(IndexedColors.RED.getIndex(), IndexedColors.WHITE.getIndex(), true, (short) 10, HorizontalAlignment.LEFT);
        styleHeaderPK = crearEstilo(IndexedColors.RED.getIndex(), IndexedColors.WHITE.getIndex(), true, (short) 10, HorizontalAlignment.LEFT);
        styleCelda = crearEstilo(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(), false, (short) 10, HorizontalAlignment.LEFT);
        styleCeldaCenter = crearEstilo(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(), false, (short) 10, HorizontalAlignment.CENTER);
        styleCeldaBold = crearEstilo(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(), true, (short) 10, HorizontalAlignment.LEFT);
        styleCeldaPK = crearEstilo(IndexedColors.ROSE.getIndex(), IndexedColors.RED.getIndex(), true, (short) 10, HorizontalAlignment.LEFT);
        styleCeldaOk = crearEstilo(IndexedColors.WHITE.getIndex(), IndexedColors.GREEN.getIndex(), true, (short) 10, HorizontalAlignment.CENTER);
        styleCeldaError = crearEstilo(IndexedColors.WHITE.getIndex(), IndexedColors.RED.getIndex(), true, (short) 10, HorizontalAlignment.CENTER);
    }
    
    private CellStyle crearEstilo(short bgColor, short fontColor, boolean bold, short fontSize, HorizontalAlignment align) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(bgColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        Font font = workbook.createFont();
        font.setBold(bold);
        font.setColor(fontColor);
        font.setFontHeightInPoints(fontSize);
        style.setFont(font);
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(align);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        
        return style;
    }
    
    public void guardarResultadoNuevo(
            Map<String, Object> resultado,
            String rutaSalida,
            ProgressCallback callback) throws Exception {
        
        logger.info("Guardando resultado: {}", rutaSalida);
        
        List<Map<String, String>> resultadoA = (List<Map<String, String>>) resultado.get("resultadoA");
        List<Map<String, String>> verificados = (List<Map<String, String>>) resultado.get("verificados");
        List<Map<String, String>> noEncontrados = (List<Map<String, String>>) resultado.get("noEncontrados");
        Map<String, String> estadisticas = (Map<String, String>) resultado.get("estadisticas");
        List<String> columnasClaveA = (List<String>) resultado.get("columnasClaveA");
        List<String> columnasClaveB = (List<String>) resultado.get("columnasClaveB");
        
        workbook = new SXSSFWorkbook(100);
        crearEstilos();
        workbook.setCompressTempFiles(true);
        
        if (callback != null) callback.onProgress(5, "Creando archivo...");
        
        crearHojaResumen(estadisticas, columnasClaveA, columnasClaveB);
        
        if (callback != null) callback.onProgress(15, "Escribiendo todos...");
        crearHojaTodos(resultadoA, columnasClaveA, callback);
        
        if (callback != null) callback.onProgress(50, "Escribiendo verificados...");
        crearHojaVerificados(verificados, columnasClaveA);
        
        if (callback != null) callback.onProgress(80, "Escribiendo no encontrados...");
        crearHojaNoEncontrados(noEncontrados, columnasClaveA);
        
        if (callback != null) callback.onProgress(95, "Guardando archivo...");
        
        try (FileOutputStream fos = new FileOutputStream(rutaSalida)) {
            workbook.write(fos);
        }
        
        workbook.dispose();
        workbook.close();
        
        if (callback != null) callback.onProgress(100, "Completado");
        logger.info("Archivo guardado exitosamente");
    }
    
    private void crearHojaResumen(Map<String, String> estadisticas,
            List<String> columnasClaveA, List<String> columnasClaveB) {
        
        Sheet sheet = workbook.createSheet("1-Resumen");
        
        CellStyle estiloTitulo = crearEstilo(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(), true, (short) 20, HorizontalAlignment.LEFT);
        CellStyle estiloSubtitulo = crearEstilo(IndexedColors.WHITE.getIndex(), IndexedColors.GREY_50_PERCENT.getIndex(), false, (short) 11, HorizontalAlignment.LEFT);
        CellStyle estiloSeccion = crearEstilo(IndexedColors.GREY_25_PERCENT.getIndex(), IndexedColors.BLACK.getIndex(), true, (short) 12, HorizontalAlignment.LEFT);
        CellStyle estiloLabel = crearEstilo(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(), false, (short) 11, HorizontalAlignment.LEFT);
        CellStyle estiloValor = crearEstilo(IndexedColors.WHITE.getIndex(), IndexedColors.RED.getIndex(), true, (short) 14, HorizontalAlignment.RIGHT);
        CellStyle estiloRelacion = crearEstilo(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(), false, (short) 11, HorizontalAlignment.LEFT);
        CellStyle estiloPK = crearEstilo(IndexedColors.ROSE.getIndex(), IndexedColors.RED.getIndex(), true, (short) 11, HorizontalAlignment.LEFT);
        
        int row = 0;
        
        Row rowLogo = sheet.createRow(row++);
        Cell cellLogo = rowLogo.createCell(0);
        cellLogo.setCellValue("BANCO DAVIVIENDA");
        cellLogo.setCellStyle(estiloTitulo);
        
        Row rowSubtitulo = sheet.createRow(row++);
        Cell cellSub = rowSubtitulo.createCell(0);
        cellSub.setCellValue("Informe de Validacion - Cruce de Archivos");
        cellSub.setCellStyle(estiloSubtitulo);
        
        row++;
        
        Row rowStatsTitulo = sheet.createRow(row++);
        Cell cellStatsTitulo = rowStatsTitulo.createCell(0);
        cellStatsTitulo.setCellValue("Estadisticas");
        cellStatsTitulo.setCellStyle(estiloSeccion);
        
        for (Map.Entry<String, String> entry : estadisticas.entrySet()) {
            Row statRow = sheet.createRow(row++);
            
            Cell cellLabel = statRow.createCell(0);
            cellLabel.setCellValue(entry.getKey());
            cellLabel.setCellStyle(estiloLabel);
            
            Cell cellValue = statRow.createCell(1);
            cellValue.setCellValue(entry.getValue());
            
            String keyLower = entry.getKey().toLowerCase();
            if (keyLower.contains("encontrad") || keyLower.contains("porcentaje")) {
                cellValue.setCellStyle(estiloValor);
            } else {
                cellValue.setCellStyle(estiloLabel);
            }
        }
        
        row++;
        
        Row rowRelTitulo = sheet.createRow(row++);
        Cell cellRelTitulo = rowRelTitulo.createCell(0);
        cellRelTitulo.setCellValue("Relaciones de Columnas");
        cellRelTitulo.setCellStyle(estiloSeccion);
        
        for (int i = 0; i < Math.max(columnasClaveA.size(), columnasClaveB.size()); i++) {
            Row relRow = sheet.createRow(row++);
            
            boolean esPK = (i == 0);
            
            Cell numCell = relRow.createCell(0);
            numCell.setCellValue((i + 1) + ".");
            numCell.setCellStyle(crearEstilo(IndexedColors.WHITE.getIndex(), IndexedColors.GREY_50_PERCENT.getIndex(), false, (short) 10, HorizontalAlignment.CENTER));
            
            Cell aCell = relRow.createCell(1);
            aCell.setCellValue(i < columnasClaveA.size() ? columnasClaveA.get(i) : "-");
            aCell.setCellStyle(esPK ? estiloPK : estiloRelacion);
            
            Cell flechaCell = relRow.createCell(2);
            flechaCell.setCellValue(">");
            flechaCell.setCellStyle(crearEstilo(IndexedColors.WHITE.getIndex(), IndexedColors.GREY_50_PERCENT.getIndex(), false, (short) 11, HorizontalAlignment.CENTER));
            
            Cell bCell = relRow.createCell(3);
            bCell.setCellValue(i < columnasClaveB.size() ? columnasClaveB.get(i) : "-");
            bCell.setCellStyle(esPK ? estiloPK : estiloRelacion);
        }
        
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 2000);
        sheet.setColumnWidth(3, 5000);
    }
    
    private void crearHojaTodos(List<Map<String, String>> datos, List<String> columnasPK, ProgressCallback callback) {
        Sheet sheet = workbook.createSheet("2-Todos");
        
        if (datos.isEmpty()) return;
        
        Set<String> todasLasColumnas = new LinkedHashSet<>();
        for (Map<String, String> registro : datos) {
            todasLasColumnas.addAll(registro.keySet());
        }
        List<String> columnas = new ArrayList<>(todasLasColumnas);
        Set<String> pkSet = new HashSet<>(columnasPK);
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnas.size(); i++) {
            String col = columnas.get(i);
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(col);
            cell.setCellStyle(pkSet.contains(col) ? styleHeaderPK : (i == 0 ? styleHeaderLeft : styleHeader));
        }
        
        int totalFilas = datos.size();
        int rowNum = 1;
        
        for (Map<String, String> registro : datos) {
            Row row = sheet.createRow(rowNum++);
            String estado = registro.get("CRUCE_ESTADO");
            boolean esEncontrado = "ENCONTRADO".equals(estado);
            
            for (int i = 0; i < columnas.size(); i++) {
                String col = columnas.get(i);
                String valor = registro.get(col);
                Cell cell = row.createCell(i);
                cell.setCellValue(valor != null ? valor : "");
                
                if (col.equals("CRUCE_ESTADO")) {
                    cell.setCellStyle(esEncontrado ? styleCeldaOk : styleCeldaError);
                } else if (pkSet.contains(col)) {
                    cell.setCellStyle(styleCeldaPK);
                } else if (col.equals("COINCIDENCIAS_EN_B")) {
                    cell.setCellStyle(styleCeldaCenter);
                } else {
                    cell.setCellStyle(rowNum % 2 == 0 ? styleCelda : styleCelda);
                }
            }
            
            if (callback != null && rowNum % 2000 == 0) {
                int progress = 15 + (int)((rowNum * 35.0) / totalFilas);
                callback.onProgress(progress, "Escribiendo " + rowNum + "/" + totalFilas);
            }
        }
        
        for (int i = 0; i < columnas.size(); i++) {
            sheet.setColumnWidth(i, 3500);
        }
    }
    
    private void crearHojaVerificados(List<Map<String, String>> datos, List<String> columnasPK) {
        Sheet sheet = workbook.createSheet("3-Verificados");
        
        if (datos == null || datos.isEmpty()) {
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("No hay registros verificados");
            cell.setCellStyle(styleCelda);
            return;
        }
        
        Set<String> todasLasColumnas = new LinkedHashSet<>();
        for (Map<String, String> registro : datos) {
            todasLasColumnas.addAll(registro.keySet());
        }
        List<String> columnas = new ArrayList<>(todasLasColumnas);
        Set<String> pkSet = new HashSet<>(columnasPK);
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnas.size(); i++) {
            String col = columnas.get(i);
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(col);
            cell.setCellStyle(pkSet.contains(col) ? styleHeaderPK : (i == 0 ? styleHeaderLeft : styleHeader));
        }
        
        int rowNum = 1;
        for (Map<String, String> registro : datos) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < columnas.size(); i++) {
                String col = columnas.get(i);
                String valor = registro.get(col);
                Cell cell = row.createCell(i);
                cell.setCellValue(valor != null ? valor : "");
                
                if (col.equals("CRUCE_ESTADO")) {
                    cell.setCellStyle(styleCeldaOk);
                } else if (pkSet.contains(col)) {
                    cell.setCellStyle(styleCeldaPK);
                } else {
                    cell.setCellStyle(styleCelda);
                }
            }
        }
        
        for (int i = 0; i < columnas.size(); i++) {
            sheet.setColumnWidth(i, 3500);
        }
    }
    
    private void crearHojaNoEncontrados(List<Map<String, String>> datos, List<String> columnasPK) {
        Sheet sheet = workbook.createSheet("4-No Encontrados");
        
        if (datos == null || datos.isEmpty()) {
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("No hay registros sin coincidencia");
            cell.setCellStyle(styleCelda);
            return;
        }
        
        Set<String> todasLasColumnas = new LinkedHashSet<>();
        for (Map<String, String> registro : datos) {
            todasLasColumnas.addAll(registro.keySet());
        }
        List<String> columnas = new ArrayList<>(todasLasColumnas);
        Set<String> pkSet = new HashSet<>(columnasPK);
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnas.size(); i++) {
            String col = columnas.get(i);
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(col);
            cell.setCellStyle(pkSet.contains(col) ? styleHeaderPK : (i == 0 ? styleHeaderLeft : styleHeader));
        }
        
        int rowNum = 1;
        for (Map<String, String> registro : datos) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < columnas.size(); i++) {
                String col = columnas.get(i);
                String valor = registro.get(col);
                Cell cell = row.createCell(i);
                cell.setCellValue(valor != null ? valor : "");
                
                if (col.equals("CRUCE_ESTADO")) {
                    cell.setCellStyle(styleCeldaError);
                } else if (pkSet.contains(col)) {
                    cell.setCellStyle(styleCeldaPK);
                } else {
                    cell.setCellStyle(styleCelda);
                }
            }
        }
        
        for (int i = 0; i < columnas.size(); i++) {
            sheet.setColumnWidth(i, 3500);
        }
    }
}
