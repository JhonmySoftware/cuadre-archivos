package com.banco.cuadre;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class LectorExcel {
    private static final Logger logger = LoggerFactory.getLogger(LectorExcel.class);
    
    private static final long MAX_FILE_SIZE_BYTES = 500 * 1024 * 1024; // 500 MB
    private static final int MAX_ROWS = 1_000_000;
    private static final int MAX_COLUMNS = 500;
    
    public static class InfoHoja {
        public String nombre;
        public int totalFilas;
        public int totalColumnas;
        public List<String> encabezados;
        public List<Map<String, String>> datos;
        
        public InfoHoja(String nombre) {
            this.nombre = nombre;
            this.encabezados = new ArrayList<>();
            this.datos = new ArrayList<>();
        }
    }
    
    public static class InfoArchivo {
        public String ruta;
        public String nombre;
        public List<InfoHoja> hojas;
        
        public InfoArchivo(String ruta) {
            this.ruta = ruta;
            this.nombre = ruta.substring(ruta.lastIndexOf("\\") + 1);
            this.hojas = new ArrayList<>();
        }
    }
    
    public InfoArchivo leerArchivo(String ruta) throws Exception {
        File archivo = new File(ruta);
        
        if (!archivo.exists()) {
            throw new SecurityException("El archivo no existe: " + archivo.getName());
        }
        
        if (!archivo.canRead()) {
            throw new SecurityException("Sin permisos de lectura para: " + archivo.getName());
        }
        
        long fileSize = archivo.length();
        if (fileSize > MAX_FILE_SIZE_BYTES) {
            throw new SecurityException("Archivo demasiado grande. Maximo: " + (MAX_FILE_SIZE_BYTES / (1024*1024)) + " MB");
        }
        
        logger.info("Leyendo archivo: {} ({} KB)", archivo.getName(), fileSize / 1024);
        InfoArchivo info = new InfoArchivo(ruta);
        
        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            int numHojas = workbook.getNumberOfSheets();
            if (numHojas > 50) {
                logger.warn("Archivo con {} hojas - Procesando todas", numHojas);
            }
            
            for (int i = 0; i < numHojas; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                InfoHoja infoHoja = leerHoja(sheet);
                info.hojas.add(infoHoja);
            }
        }
        
        return info;
    }
    
    private InfoHoja leerHoja(Sheet sheet) {
        String nombre = sheet.getSheetName();
        InfoHoja info = new InfoHoja(nombre);
        
        int rowCount = 0;
        Iterator<Row> rowIterator = sheet.iterator();
        boolean primeraFila = true;
        
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            rowCount++;
            
            if (rowCount > MAX_ROWS) {
                logger.warn("Hoja '{}': Limite de {} filas alcanzado. Datos truncados.", nombre, MAX_ROWS);
                break;
            }
            
            Iterator<Cell> cellIterator = row.iterator();
            
            List<String> fila = new ArrayList<>();
            int colCount = 0;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                colCount++;
                if (colCount > MAX_COLUMNS) {
                    logger.warn("Hoja '{}': Limite de {} columnas alcanzado.", nombre, MAX_COLUMNS);
                    fila.add("[COLUMNA_TRUNCADA]");
                    break;
                }
                fila.add(getValorCelda(cell));
            }
            
            if (primeraFila) {
                info.encabezados = new ArrayList<>(fila);
                primeraFila = false;
            } else {
                Map<String, String> registro = new LinkedHashMap<>();
                for (int i = 0; i < info.encabezados.size() && i < fila.size(); i++) {
                    String valor = fila.get(i);
                    if (valor != null && valor.length() > 32767) {
                        valor = valor.substring(0, 32767);
                    }
                    registro.put(info.encabezados.get(i), valor);
                }
                info.datos.add(registro);
            }
        }
        
        info.totalFilas = info.datos.size();
        info.totalColumnas = info.encabezados.size();
        
        logger.info("Hoja '{}': {} filas, {} columnas", nombre, info.totalFilas, info.totalColumnas);
        
        return info;
    }
    
    private String getValorCelda(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString().substring(0, 10);
                }
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num)) {
                    return String.valueOf((long) num);
                }
                return String.valueOf(num);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return "";
                }
            default:
                return "";
        }
    }
}
