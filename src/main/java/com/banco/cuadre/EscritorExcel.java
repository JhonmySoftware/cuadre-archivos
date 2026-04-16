package com.banco.cuadre;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.util.*;

/**
 * EscritorExcel - Generador de reportes Excel estilizados para resultados de cruce.
 * 
 * Esta clase crea archivos Excel con 4 hojas que contienen el analisis completo
 * del cruce de datos, incluyendo estadisticas, registros verificados y no encontrados.
 * 
 * Colores institucionales aplicados:
 * - Headers: Fondo rojo (#D2141E), texto blanco
 * - Primary Key: Fondo rosa (#FFC0CB), texto rojo
 * - Estados: Verde para encontrados, rojo para no encontrados
 * 
 * Estructura del archivo generado:
 * - Hoja 1: Resumen - Estadisticas y relaciones configuradas
 * - Hoja 2: Todos - Todos los registros con estado de cruce
 * - Hoja 3: Verificados - Solo registros encontrados
 * - Hoja 4: No Encontrados - Solo registros no hallados
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
public class EscritorExcel {
    
    private static final Logger logger = LoggerFactory.getLogger(EscritorExcel.class);
    
    /** Workbook de Apache POI con streaming para archivos grandes */
    private SXSSFWorkbook workbook;
    
    /** Estilo para headers de columna (fondo rojo, texto blanco) */
    private CellStyle styleHeader;
    
    /** Estilo para headers alineados a la izquierda */
    private CellStyle styleHeaderLeft;
    
    /** Estilo para celdas de datos normales */
    private CellStyle styleCelda;
    
    /** Estilo para celdas de datos centradas */
    private CellStyle styleCeldaCenter;
    
    /** Estilo para celdas en negrita */
    private CellStyle styleCeldaBold;
    
    /** Estilo para estado ENCONTRADO (verde) */
    private CellStyle styleCeldaOk;
    
    /** Estilo para estado NO ENCONTRADO (rojo) */
    private CellStyle styleCeldaError;
    
    /** Estilo para columnas Primary Key (rosa) */
    private CellStyle styleCeldaPK;
    
    /** Estilo para header de Primary Key */
    private CellStyle styleHeaderPK;
    
    /**
     * Interfaz de callback para reportar progreso de generacion.
     * 
     * Implementar esta interfaz para recibir actualizaciones
     * del porcentaje de progreso durante la generacion del archivo.
     */
    public interface ProgressCallback {
        /**
         * Called durante la generacion del archivo.
         * 
         * @param percent Porcentaje de completado (0-100)
         * @param message Mensaje descriptivo del estado actual
         */
        void onProgress(int percent, String message);
    }
    
    /**
     * Constructor. Inicializa los estilos que seran usados en el Excel.
     */
    public EscritorExcel() {
    }
    
    /**
     * Crea todos los estilos de celda utilizados en el reporte.
     * 
     * Estilos creados:
     * - styleHeader: Fondo rojo, texto blanco, centrado (para headers)
     * - styleHeaderLeft: Fondo rojo, texto blanco, izquierda
     * - styleHeaderPK: Fondo rojo, texto blanco para columna PK
     * - styleCelda: Fondo blanco, texto negro (datos normales)
     * - styleCeldaCenter: Fondo blanco, texto negro, centrado
     * - styleCeldaBold: Fondo blanco, texto negro, negrita
     * - styleCeldaPK: Fondo rosa, texto rojo, negrita (columnas clave)
     * - styleCeldaOk: Fondo blanco, texto verde (ENCONTRADO)
     * - styleCeldaError: Fondo blanco, texto rojo (NO ENCONTRADO)
     */
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
    
    /**
     * Crea un estilo de celda con configuracion personalizada.
     * 
     * @param bgColor Color de fondo (IndexedColors)
     * @param fontColor Color del texto (IndexedColors)
     * @param bold Si el texto debe ser negrita
     * @param fontSize Tamanio de fuente en puntos
     * @param align Alineacion horizontal del texto
     * @return CellStyle configurado listo para usar
     */
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
    
    /**
     * Guarda el resultado del cruce en un archivo Excel.
     * 
     * Genera un archivo Excel con 4 hojas que contienen:
     * 1. Resumen: Estadisticas y relaciones configuradas
     * 2. Todos: Todos los registros del Archivo A con estado
     * 3. Verificados: Solo registros encontrados en Archivo B
     * 4. No Encontrados: Solo registros NO hallados en Archivo B
     * 
     * @param resultado Mapa con la estructura de resultado del cruce.
     *                  Debe contener:
     *                  - resultadoA: List<Map<String, String>> con todos los registros
     *                  - verificados: List<Map<String, String>> con registros encontrados
     *                  - noEncontrados: List<Map<String, String>> con registros no hallados
     *                  - estadisticas: Map<String, String> con estadisticas
     *                  - columnasClaveA: List<String> con columnas clave de A
     *                  - columnasClaveB: List<String> con columnas clave de B
     * @param rutaSalida Ruta completa del archivo de salida (.xlsx)
     * @param callback Interfaz para reportar progreso (puede ser null)
     * @throws Exception si ocurre un error al crear el archivo
     * 
     * @Ejemplo
     * <pre>
     * EscritorExcel escritor = new EscritorExcel();
     * escritor.guardarResultadoNuevo(resultado, "C:\\reportes\\cruce_2024.xlsx", 
     *     (percent, msg) -> System.out.println(msg));
     * </pre>
     */
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
    
    /**
     * Crea la hoja de Resumen con estadisticas y relaciones.
     * 
     * Contenido de la hoja:
     * - Titulo: "BANCO DAVIVIENDA"
     * - Subtitulo: "Informe de Validacion - Cruce de Archivos"
     * - Seccion Estadisticas: Total, encontrados, no encontrados, porcentaje
     * - Seccion Relaciones: Lista de columnas clave configuradas
     * 
     * @param estadisticas Mapa con pares {etiqueta -> valor}
     * @param columnasClaveA Columnas clave del Archivo A
     * @param columnasClaveB Columnas clave del Archivo B
     */
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
    
    /**
     * Crea la hoja "2-Todos" con todos los registros y su estado.
     * 
     * Incluye todas las columnas del archivo original mas las columnas
     * de resultado del cruce (CRUCE_ESTADO, CRUCE_CLAVE, etc.)
     * 
     * @param datos Lista de registros con columnas de resultado
     * @param columnasPK Columnas que son Primary Key (resaltadas en rosa)
     * @param callback Callback para reportar progreso
     */
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
    
    /**
     * Crea la hoja "3-Verificados" solo con registros encontrados.
     * 
     * Mismo formato que crearHojaTodos pero solo incluye registros
     * cuyo CRUCE_ESTADO sea "ENCONTRADO".
     * 
     * @param datos Lista de registros verificados
     * @param columnasPK Columnas Primary Key para resaltar
     */
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
    
    /**
     * Crea la hoja "4-No Encontrados" solo con registros no hallados.
     * 
     * Mismo formato que crearHojaTodos pero solo incluye registros
     * cuyo CRUCE_ESTADO sea "NO ENCONTRADO".
     * 
     * @param datos Lista de registros no encontrados
     * @param columnasPK Columnas Primary Key para resaltar
     */
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
