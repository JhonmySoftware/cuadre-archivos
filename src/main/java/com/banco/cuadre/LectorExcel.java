package com.banco.cuadre;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * LectorExcel - Clase utilitaria para leer archivos Excel (.xlsx).
 * 
 * Esta clase proporciona metodos para cargar archivos Excel y extraer
 * su contenido en estructuras de datos manipulables.
 * 
 * Limitaciones:
 * - Tamanio maximo de archivo: 500 MB
 * - Maximo de filas por hoja: 1,000,000
 * - Maximo de columnas por hoja: 500
 * - Largo maximo de celda: 32,767 caracteres
 * 
 * @author Banco Davivienda
 * @version 1.0.0
 */
public class LectorExcel {
    
    private static final Logger logger = LoggerFactory.getLogger(LectorExcel.class);
    
    /** Tamanio maximo de archivo (500 MB) */
    private static final long MAX_FILE_SIZE_BYTES = 500 * 1024 * 1024;
    
    /** Maximo numero de filas por hoja */
    private static final int MAX_ROWS = 1_000_000;
    
    /** Maximo numero de columnas por hoja */
    private static final int MAX_COLUMNS = 500;
    
    /**
     * InfoHoja - Representa la informacion de una hoja de Excel.
     * 
     * Esta estructura contiene todos los datos de una hoja incluyendo
     * encabezados y filas de datos.
     */
    public static class InfoHoja {
        
        /** Nombre de la hoja (tab) */
        public String nombre;
        
        /** Numero total de filas con datos (excluyendo encabezado) */
        public int totalFilas;
        
        /** Numero total de columnas */
        public int totalColumnas;
        
        /** Lista de nombres de columnas (primera fila del Excel) */
        public List<String> encabezados;
        
        /** Lista de registros, donde cada Map representa una fila.
         *  La clave del Map es el nombre de la columna y el valor es el contenido */
        public List<Map<String, String>> datos;
        
        /**
         * Constructor que inicializa una hoja con su nombre.
         * 
         * @param nombre Nombre de la hoja de Excel
         */
        public InfoHoja(String nombre) {
            this.nombre = nombre;
            this.encabezados = new ArrayList<>();
            this.datos = new ArrayList<>();
        }
        
        /**
         * Obtiene el numero de registros en la hoja.
         * 
         * @return Numero de filas de datos
         */
        public int getNumeroRegistros() {
            return datos != null ? datos.size() : 0;
        }
        
        /**
         * Obtiene una representacion en texto de la hoja.
         * 
         * @return String con nombre, filas y columnas
         */
        @Override
        public String toString() {
            return String.format("%s [%d filas x %d columnas]", 
                nombre, totalFilas, totalColumnas);
        }
    }
    
    /**
     * InfoArchivo - Representa la informacion completa de un archivo Excel.
     * 
     * Esta estructura contiene todas las hojas del archivo con sus
     * respectivos datos.
     */
    public static class InfoArchivo {
        
        /** Ruta completa del archivo */
        public String ruta;
        
        /** Nombre del archivo (sin la ruta) */
        public String nombre;
        
        /** Lista de hojas contenidas en el archivo */
        public List<InfoHoja> hojas;
        
        /**
         * Constructor que inicializa un archivo con su ruta.
         * 
         * @param ruta Ruta completa del archivo Excel
         */
        public InfoArchivo(String ruta) {
            this.ruta = ruta;
            this.nombre = ruta.substring(ruta.lastIndexOf("\\") + 1);
            this.hojas = new ArrayList<>();
        }
        
        /**
         * Obtiene una hoja por su nombre.
         * 
         * @param nombreHoja Nombre de la hoja a buscar
         * @return InfoHoja si se encuentra, null si no existe
         */
        public InfoHoja getHojaPorNombre(String nombreHoja) {
            for (InfoHoja hoja : hojas) {
                if (hoja.nombre.equals(nombreHoja)) {
                    return hoja;
                }
            }
            return null;
        }
        
        /**
         * Obtiene una hoja por su indice (0-based).
         * 
         * @param indice Posicion de la hoja (comenzando en 0)
         * @return InfoHoja si el indice es valido, null si no
         */
        public InfoHoja getHojaPorIndice(int indice) {
            if (indice >= 0 && indice < hojas.size()) {
                return hojas.get(indice);
            }
            return null;
        }
        
        /**
         * Obtiene el numero total de hojas en el archivo.
         * 
         * @return Cantidad de hojas
         */
        public int getNumeroHojas() {
            return hojas != null ? hojas.size() : 0;
        }
        
        /**
         * Obtiene el numero total de registros en todas las hojas.
         * 
         * @return Sumatoria de registros en todas las hojas
         */
        public int getTotalRegistros() {
            int total = 0;
            for (InfoHoja hoja : hojas) {
                total += hoja.totalFilas;
            }
            return total;
        }
        
        /**
         * Obtiene una representacion en texto del archivo.
         * 
         * @return String con nombre y cantidad de hojas
         */
        @Override
        public String toString() {
            return String.format("%s [%d hojas, %d registros]", 
                nombre, getNumeroHojas(), getTotalRegistros());
        }
    }
    
    /**
     * Lee un archivo Excel completo y retorna su informacion.
     * 
     * Este metodo abre el archivo, lee todas sus hojas y extrae los datos
     * incluyendo encabezados y filas. La primera fila se considera como
     * encabezados de columna.
     * 
     * Ejemplo de uso:
     * <pre>
     * LectorExcel lector = new LectorExcel();
     * InfoArchivo archivo = lector.leerArchivo("C:\\datos\\archivo.xlsx");
     * 
     * for (InfoHoja hoja : archivo.hojas) {
     *     System.out.println("Hoja: " + hoja.nombre);
     *     System.out.println("Columnas: " + hoja.encabezados);
     *     System.out.println("Registros: " + hoja.datos.size());
     * }
     * </pre>
     * 
     * @param ruta Ruta completa del archivo Excel (.xlsx)
     * @return InfoArchivo con toda la informacion del archivo
     * @throws SecurityException si el archivo no existe, no se puede leer o es muy grande
     * @throws Exception si ocurre un error al procesar el Excel
     */
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
            throw new SecurityException("Archivo demasiado grande. Maximo: " 
                + (MAX_FILE_SIZE_BYTES / (1024*1024)) + " MB");
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
    
    /**
     * Lee el contenido de una hoja de Excel.
     * 
     * La primera fila se toma como encabezados de columna.
     * Las filas subsequentes se convierten en registros (Map de columnas a valores).
     * 
     * @param sheet Objeto Sheet de Apache POI a leer
     * @return InfoHoja con los datos de la hoja
     */
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
                logger.warn("Hoja '{}': Limite de {} filas alcanzado. Datos truncados.", 
                    nombre, MAX_ROWS);
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
    
    /**
     * Extrae el valor de una celda como texto.
     * 
     * Maneja diferentes tipos de datos de celdas:
     * - STRING: retorna el texto directo
     * - NUMERIC: convierte a numero (entero si no tiene decimal)
     * - BOOLEAN: convierte a "true" o "false"
     * - FORMULA: intenta obtener el valor calculado
     * - DATE: convierte a formato YYYY-MM-DD
     * 
     * @param cell Celda de Apache POI a leer
     * @return Valor de la celda como String, o cadena vacia si es null
     */
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
