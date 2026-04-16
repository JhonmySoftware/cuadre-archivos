package com.banco.cuadre;

import java.util.*;

/**
 * ValidadorCruce - Logica de negocio para cruce de archivos de datos.
 * 
 * Esta clase implementa los algoritmos para comparar registros entre dos
 * conjuntos de datos (Archivo A y Archivo B) y determinar coincidencias
 * basado en columnas clave configurables.
 * 
 * Caracteristicas principales:
 * - Soporte para multiples columnas clave
 * - Normalizacion automatica de datos (ceros izquierda, mayusculas/minusculas)
 * - Modo AND (todas las claves deben coincidir)
 * - Modo OR (al menos una clave debe coincidir)
 * - Generacion de estadisticas de cruce
 * 
 * Ejemplo de uso basico:
 * <pre>
 * ValidadorCruce validador = new ValidadorCruce();
 * 
 * List&lt;String&gt; columnasA = Arrays.asList("numero_cuenta");
 * List&lt;String&gt; columnasB = Arrays.asList("numero_cuenta");
 * 
 * Map&lt;String, Object&gt; resultado = validador.generarResultadoConModo(
 *     datosA, columnasA, datosB, columnasB, null, false);
 * </pre>
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
public class ValidadorCruce {
    
    /**
     * ResultadoCruce - Estructura que contiene los resultados de un cruce simple.
     * 
     * Esta clase almacena las estadisticas y detalles del cruce
     * cuando se usa el metodo cruzarPorColumnas.
     */
    public static class ResultadoCruce {
        
        /** Total de registros procesados del Archivo A */
        public int totalRegistros;
        
        /** Numero de registros encontrados en Archivo B */
        public int encontrados;
        
        /** Numero de registros NO encontrados en Archivo B */
        public int noEncontrados;
        
        /** Lista de detalles por cada registro procesado */
        public List<Map<String, Object>> detalles;
        
        /**
         * Constructor que inicializa la lista de detalles.
         */
        public ResultadoCruce() {
            this.detalles = new ArrayList<>();
        }
        
        /**
         * Genera un resumen textual del resultado del cruce.
         * 
         * @return String con formato: "Total: X | Encontrados: Y | No encontrados: Z (P%)"
         */
        public String getResumen() {
            return String.format("Total: %d | Encontrados: %d | No encontrados: %d (%.1f%%)",
                totalRegistros, encontrados, noEncontrados,
                totalRegistros > 0 ? (100.0 * encontrados / totalRegistros) : 0);
        }
        
        /**
         * Calcula el porcentaje de registros encontrados.
         * 
         * @return Porcentaje como numero decimal (0.0 a 100.0)
         */
        public double getPorcentajeEncontrados() {
            return totalRegistros > 0 ? (100.0 * encontrados / totalRegistros) : 0;
        }
    }
    
    /**
     * Normaliza un valor de texto para comparacion.
     * 
     * Reglas de normalizacion:
     * 1. Elimina espacios al inicio y final
     * 2. Si es numerico: elimina ceros a la izquierda (ej: "00123" -> "123")
     * 3. Si es texto: convierte a minusculas
     * 
     * @param valor El valor a normalizar (puede ser null)
     * @return Valor normalizado o cadena vacia si es null/vacio
     * 
     * @Ejemplos
     * normalizarValor("00123")    = "123"
     * normalizarValor(" ABC ")    = "abc"
     * normalizarValor(null)       = ""
     * normalizarValor("123.45")   = "123.45" (decimales no se normalizan)
     */
    public static String normalizarValor(String valor) {
        if (valor == null) return "";
        String v = valor.trim();
        if (v.isEmpty()) return "";
        if (esNumerico(v)) {
            try {
                return Long.toString(Long.parseLong(v));
            } catch (NumberFormatException e) {
                return v;
            }
        }
        return v.toLowerCase();
    }
    
    /**
     * Verifica si una cadena representa un numero entero.
     * 
     * @param str Cadena a verificar
     * @return true si es un numero entero valido, false en caso contrario
     */
    private static boolean esNumerico(String str) {
        if (str == null || str.isEmpty()) return false;
        String s = str.trim();
        int i = 0;
        if (s.startsWith("-") || s.startsWith("+")) {
            if (s.length() == 1) return false;
            i = 1;
        }
        for (; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Cruza dos conjuntos de datos por columnas clave y retorna resultado detallado.
     * 
     * Este metodo compara cada registro del primer conjunto con el segundo,
     * buscando coincidencias basadas en las columnas especificadas.
     * 
     * @param datos1 Lista de registros del Archivo A (cada Map es una fila con {columna -> valor})
     * @param datos2 Lista de registros del Archivo B para comparar
     * @param columnasClave Lista de nombres de columnas a usar como clave de cruce
     * @param columnasValidar Lista de columnas adicionales a validar (opcional, puede ser null)
     * @return ResultadoCruce con estadisticas y detalles de cada registro
     * 
     * @Ejemplo
     * <pre>
     * List&lt;Map&lt;String, String&gt;&gt; datosA = lector.leerArchivo("a.xlsx").hojas.get(0).datos;
     * List&lt;Map&lt;String, String&gt;&gt; datosB = lector.leerArchivo("b.xlsx").hojas.get(0).datos;
     * 
     * ValidadorCruce.ResultadoCruce resultado = validador.cruzarPorColumnas(
     *     datosA, datosB,
     *     Arrays.asList("numero_cuenta"),
     *     Arrays.asList("fecha", "monto")
     * );
     * 
     * System.out.println(resultado.getResumen());
     * </pre>
     */
    public ResultadoCruce cruzarPorColumnas(
            List<Map<String, String>> datos1,
            List<Map<String, String>> datos2,
            List<String> columnasClave,
            List<String> columnasValidar) {
        
        ResultadoCruce resultado = new ResultadoCruce();
        resultado.totalRegistros = datos1.size();
        
        Map<String, List<Map<String, String>>> indice2 = new HashMap<>();
        for (Map<String, String> registro2 : datos2) {
            String clave = generarClaveNormalizada(registro2, columnasClave);
            if (!clave.isEmpty()) {
                indice2.computeIfAbsent(clave, k -> new ArrayList<>()).add(registro2);
            }
        }
        
        for (Map<String, String> registro1 : datos1) {
            String claveOriginal = generarClave(registro1, columnasClave);
            String claveNormalizada = generarClaveNormalizada(registro1, columnasClave);
            
            List<Map<String, String>> coincidencias = indice2.get(claveNormalizada);
            
            Map<String, Object> detalle = new LinkedHashMap<>();
            detalle.put("REGISTRO", claveOriginal);
            detalle.put("COLUMNAS_CLAVE", columnasClave.toString());
            
            if (coincidencias == null || coincidencias.isEmpty()) {
                detalle.put("CRUCE_ESTADO", "NO ENCONTRADO");
                detalle.put("COINCIDENCIAS_ENCONTRADAS", 0);
                detalle.put("CRUCE_VALIDACIONES", 0);
                detalle.put("CRUCE_CORRECTOS", 0);
                detalle.put("CRUCE_PORCENTAJE", "0%");
                resultado.noEncontrados++;
            } else {
                detalle.put("CRUCE_ESTADO", "ENCONTRADO");
                detalle.put("COINCIDENCIAS_ENCONTRADAS", coincidencias.size());
                resultado.encontrados++;
                
                if (columnasValidar != null && !columnasValidar.isEmpty()) {
                    int validados = 0;
                    int correctos = 0;
                    
                    for (String columna : columnasValidar) {
                        String val1 = registro1.get(columna);
                        String val1Norm = normalizarValor(val1);
                        boolean todosCorrectos = true;
                        
                        for (Map<String, String> coincidencia : coincidencias) {
                            String val2 = coincidencia.get(columna);
                            String val2Norm = normalizarValor(val2);
                            if (!val1Norm.equals(val2Norm)) {
                                todosCorrectos = false;
                                break;
                            }
                        }
                        
                        validados++;
                        if (todosCorrectos) {
                            correctos++;
                        }
                    }
                    
                    detalle.put("CRUCE_VALIDACIONES", validados);
                    detalle.put("CRUCE_CORRECTOS", correctos);
                    detalle.put("CRUCE_PORCENTAJE", validados > 0 ? 
                        String.format("%.0f%%", 100.0 * correctos / validados) : "0%");
                } else {
                    detalle.put("CRUCE_VALIDACIONES", 0);
                    detalle.put("CRUCE_CORRECTOS", 0);
                    detalle.put("CRUCE_PORCENTAJE", "100%");
                }
            }
            
            for (String columna : registro1.keySet()) {
                detalle.put("ORIG_" + columna, registro1.get(columna));
            }
            
            resultado.detalles.add(detalle);
        }
        
        return resultado;
    }
    
    /**
     * Agrega columnas de resultado de cruce a los datos originales.
     * 
     * Agrega las siguientes columnas al resultado:
     * - CRUCE_ESTADO: "ENCONTRADO" o "NO ENCONTRADO"
     * - CRUCE_CLAVE: Valor de la clave de cruce
     * - COINCIDENCIAS: Numero de registros coincidentes en B
     * - CRUCE_VALIDACIONES: Total de validaciones realizadas
     * - CRUCE_CORRECTOS: Validaciones que coincidieron
     * - CRUCE_PORCENTAJE: Porcentaje de coincidencias
     * 
     * @param datos Lista de registros del Archivo A
     * @param columnasClave Columnas a usar como clave
     * @param datosComparar Datos del Archivo B
     * @param columnasValidar Columnas adicionales a validar (opcional)
     * @return Lista de registros con columnas de cruce agregadas
     */
    public List<Map<String, String>> agregarResultadoCruce(
            List<Map<String, String>> datos,
            List<String> columnasClave,
            List<Map<String, String>> datosComparar,
            List<String> columnasValidar) {
        
        List<Map<String, String>> resultado = new ArrayList<>();
        
        Map<String, List<Map<String, String>>> indice = new HashMap<>();
        for (Map<String, String> reg2 : datosComparar) {
            String clave = generarClaveNormalizada(reg2, columnasClave);
            if (!clave.isEmpty()) {
                indice.computeIfAbsent(clave, k -> new ArrayList<>()).add(reg2);
            }
        }
        
        for (Map<String, String> registro : datos) {
            Map<String, String> nuevoRegistro = new LinkedHashMap<>(registro);
            String claveOriginal = generarClave(registro, columnasClave);
            String claveNormalizada = generarClaveNormalizada(registro, columnasClave);
            
            List<Map<String, String>> coincidencias = indice.get(claveNormalizada);
            
            if (coincidencias == null || coincidencias.isEmpty()) {
                nuevoRegistro.put("CRUCE_ESTADO", "NO ENCONTRADO");
                nuevoRegistro.put("CRUCE_CLAVE", claveOriginal);
                nuevoRegistro.put("COINCIDENCIAS", "0");
                nuevoRegistro.put("CRUCE_VALIDACIONES", "0");
                nuevoRegistro.put("CRUCE_CORRECTOS", "0");
                nuevoRegistro.put("CRUCE_PORCENTAJE", "0%");
            } else {
                nuevoRegistro.put("CRUCE_ESTADO", "ENCONTRADO");
                nuevoRegistro.put("CRUCE_CLAVE", claveOriginal);
                nuevoRegistro.put("COINCIDENCIAS", coincidencias.size() + "");
                
                if (columnasValidar != null && !columnasValidar.isEmpty()) {
                    int validados = 0;
                    int correctos = 0;
                    
                    for (String columna : columnasValidar) {
                        String val1 = registro.get(columna);
                        String val1Norm = normalizarValor(val1);
                        boolean todosCorrectos = true;
                        
                        for (Map<String, String> coincidencia : coincidencias) {
                            String val2 = coincidencia.get(columna);
                            String val2Norm = normalizarValor(val2);
                            if (!val1Norm.equals(val2Norm)) {
                                todosCorrectos = false;
                                break;
                            }
                        }
                        
                        validados++;
                        if (todosCorrectos) {
                            correctos++;
                        }
                    }
                    
                    nuevoRegistro.put("CRUCE_VALIDACIONES", validados + "");
                    nuevoRegistro.put("CRUCE_CORRECTOS", correctos + "");
                    nuevoRegistro.put("CRUCE_PORCENTAJE", validados > 0 ? 
                        String.format("%.0f%%", 100.0 * correctos / validados) : "0%");
                } else {
                    nuevoRegistro.put("CRUCE_VALIDACIONES", "0");
                    nuevoRegistro.put("CRUCE_CORRECTOS", "0");
                    nuevoRegistro.put("CRUCE_PORCENTAJE", "100%");
                }
            }
            
            resultado.add(nuevoRegistro);
        }
        
        return resultado;
    }
    
    /**
     * Genera una clave unica a partir de los valores de las columnas especificadas.
     * 
     * @param registro Map con los datos del registro
     * @param columnas Lista de columnas a incluir en la clave
     * @return Clave formateada como valores separados por "|"
     * 
     * @Ejemplo
     * registro = {nombre="Juan", edad="30"}
     * columnas = ["nombre", "edad"]
     * resultado = "Juan|30"
     */
    private String generarClave(Map<String, String> registro, List<String> columnas) {
        List<String> valores = new ArrayList<>();
        for (String columna : columnas) {
            String valor = registro.get(columna);
            valores.add(valor != null ? valor.trim() : "");
        }
        return String.join("|", valores);
    }
    
    /**
     * Genera una clave normalizada (para comparacion).
     * 
     * @param registro Map con los datos del registro
     * @param columnas Lista de columnas a incluir
     * @return Clave normalizada (valores en minuscula, numeros sin ceros izquierda)
     */
    private String generarClaveNormalizada(Map<String, String> registro, List<String> columnas) {
        List<String> valores = new ArrayList<>();
        for (String columna : columnas) {
            String valor = registro.get(columna);
            valores.add(normalizarValor(valor));
        }
        return String.join("|", valores);
    }
    
    /**
     * Detecta automaticamente columnas candidatas para ser claves de cruce.
     * 
     * Busca columnas cuyo nombre contenga terminos como: cuenta, numero,
     * identificacion, documento, cedula, nit, referencia, secuencia.
     * Ademas, la columna debe tener valores en al menos el 50% de los registros.
     * 
     * @param datos Lista de registros a analizar
     * @return Lista de nombres de columnas candidatas
     */
    public List<String> detectarColumnasClave(List<Map<String, String>> datos) {
        if (datos == null || datos.isEmpty()) return new ArrayList<>();
        
        Map<String, Integer> frecuencia = new HashMap<>();
        for (Map<String, String> registro : datos) {
            for (String columna : registro.keySet()) {
                String valor = registro.get(columna);
                if (valor != null && !valor.trim().isEmpty()) {
                    frecuencia.put(columna, frecuencia.getOrDefault(columna, 0) + 1);
                }
            }
        }
        
        List<String> candidatas = new ArrayList<>();
        int maxFrecuencia = datos.size() / 2;
        
        for (Map.Entry<String, Integer> entry : frecuencia.entrySet()) {
            if (entry.getValue() > maxFrecuencia) {
                String col = entry.getKey().toLowerCase();
                if (col.contains("cuenta") || col.contains("numero") || 
                    col.contains("ident") || col.contains("documento") ||
                    col.contains("cedula") || col.contains("nit") ||
                    col.contains("referencia") || col.contains("secuencia")) {
                    candidatas.add(entry.getKey());
                }
            }
        }
        
        return candidatas;
    }
    
    /**
     * Agrega resultado de cruce con mapeo de columnas entre archivos.
     * 
     * Permite que las columnas clave tengan nombres diferentes en cada archivo.
     * 
     * @param datosA Registros del Archivo A
     * @param columnasClaveA Columnas clave del Archivo A
     * @param datosB Registros del Archivo B
     * @param columnasClaveB Columnas clave del Archivo B (pueden tener nombres diferentes)
     * @return Registros de A con columnas de resultado
     */
    public List<Map<String, String>> agregarResultadoCruceMapeado(
            List<Map<String, String>> datosA,
            List<String> columnasClaveA,
            List<Map<String, String>> datosB,
            List<String> columnasClaveB) {
        
        List<Map<String, String>> resultado = new ArrayList<>();
        
        Map<String, List<Map<String, String>>> indiceB = new HashMap<>();
        for (Map<String, String> regB : datosB) {
            String claveB = generarClaveNormalizada(regB, columnasClaveB);
            if (!claveB.isEmpty()) {
                indiceB.computeIfAbsent(claveB, k -> new ArrayList<>()).add(regB);
            }
        }
        
        for (Map<String, String> regA : datosA) {
            Map<String, String> nuevoRegistro = new LinkedHashMap<>(regA);
            String claveOriginalA = generarClave(regA, columnasClaveA);
            String claveNormalizadaA = generarClaveNormalizada(regA, columnasClaveA);
            
            List<Map<String, String>> coincidencias = indiceB.get(claveNormalizadaA);
            
            if (coincidencias == null || coincidencias.isEmpty()) {
                nuevoRegistro.put("CRUCE_ESTADO", "NO ENCONTRADO");
                nuevoRegistro.put("CRUCE_CLAVE_A", claveOriginalA);
                nuevoRegistro.put("COINCIDENCIAS", "0");
                nuevoRegistro.put("CRUCE_PORCENTAJE", "0%");
            } else {
                nuevoRegistro.put("CRUCE_ESTADO", "ENCONTRADO");
                nuevoRegistro.put("CRUCE_CLAVE_A", claveOriginalA);
                nuevoRegistro.put("COINCIDENCIAS", coincidencias.size() + "");
                nuevoRegistro.put("CRUCE_PORCENTAJE", "100%");
            }
            
            resultado.add(nuevoRegistro);
        }
        
        return resultado;
    }
    
    /**
     * Version completa del cruce mapeado que incluye datos del archivo B en el resultado.
     * 
     * Agrega los datos de la primera coincidencia en B prefixed con "B_".
     * 
     * @param datosA Registros del Archivo A
     * @param columnasClaveA Columnas clave del Archivo A
     * @param datosB Registros del Archivo B
     * @param columnasClaveB Columnas clave del Archivo B
     * @return Registros de A con datos de A y B combinados
     */
    public List<Map<String, String>> agregarResultadoCruceMapeadoCompleto(
            List<Map<String, String>> datosA,
            List<String> columnasClaveA,
            List<Map<String, String>> datosB,
            List<String> columnasClaveB) {
        
        List<Map<String, String>> resultado = new ArrayList<>();
        
        Map<String, List<Map<String, String>>> indiceB = new HashMap<>();
        for (Map<String, String> regB : datosB) {
            String claveB = generarClaveNormalizada(regB, columnasClaveB);
            if (!claveB.isEmpty()) {
                indiceB.computeIfAbsent(claveB, k -> new ArrayList<>()).add(regB);
            }
        }
        
        for (Map<String, String> regA : datosA) {
            Map<String, String> nuevoRegistro = new LinkedHashMap<>();
            
            String claveOriginalA = generarClave(regA, columnasClaveA);
            String claveNormalizadaA = generarClaveNormalizada(regA, columnasClaveA);
            
            for (Map.Entry<String, String> entry : regA.entrySet()) {
                nuevoRegistro.put("A_" + entry.getKey(), entry.getValue());
            }
            
            List<Map<String, String>> coincidencias = indiceB.get(claveNormalizadaA);
            
            if (coincidencias == null || coincidencias.isEmpty()) {
                nuevoRegistro.put("CRUCE_ESTADO", "NO ENCONTRADO");
                nuevoRegistro.put("CRUCE_CLAVE_A", claveOriginalA);
                nuevoRegistro.put("COINCIDENCIAS_B", "0");
                nuevoRegistro.put("CRUCE_PORCENTAJE", "0%");
                nuevoRegistro.put("B_FILA_ENCONTRADA", "N/A");
                for (String col : columnasClaveB) {
                    nuevoRegistro.put("B_" + col, "");
                }
            } else {
                Map<String, String> mejorCoincidencia = coincidencias.get(0);
                nuevoRegistro.put("CRUCE_ESTADO", "ENCONTRADO");
                nuevoRegistro.put("CRUCE_CLAVE_A", claveOriginalA);
                nuevoRegistro.put("COINCIDENCIAS_B", String.valueOf(coincidencias.size()));
                nuevoRegistro.put("CRUCE_PORCENTAJE", "100%");
                nuevoRegistro.put("B_FILA_ENCONTRADA", String.valueOf(datosB.indexOf(mejorCoincidencia) + 2));
                
                for (Map.Entry<String, String> entry : mejorCoincidencia.entrySet()) {
                    nuevoRegistro.put("B_" + entry.getKey(), entry.getValue());
                }
            }
            
            resultado.add(nuevoRegistro);
        }
        
        return resultado;
    }
    
    /**
     * Genera un resultado completo con todos los datos de A y B.
     * 
     * Estructura del Map retornado:
     * - datosA: {encontrados: [], noEncontrados: []}
     * - datosB: {todos: []}
     * - resumen: Lista de detalles
     * - columnasClaveA / columnasClaveB
     * - estadisticas: {clave -> valor}
     * 
     * @param datosA Registros del Archivo A
     * @param columnasClaveA Columnas clave de A
     * @param datosB Registros del Archivo B
     * @param columnasClaveB Columnas clave de B
     * @return Map con estructura completa de resultados
     */
    public Map<String, Object> generarResultadoCompleto(
            List<Map<String, String>> datosA,
            List<String> columnasClaveA,
            List<Map<String, String>> datosB,
            List<String> columnasClaveB) {
        
        Map<String, Object> resultado = new LinkedHashMap<>();
        
        List<Map<String, Object>> resumen = new ArrayList<>();
        List<Map<String, String>> noEncontrados = new ArrayList<>();
        List<Map<String, String>> datosAEncontrados = new ArrayList<>();
        
        int encontrados = 0;
        int noHallados = 0;
        
        Map<String, List<Map<String, String>>> indiceB = new HashMap<>();
        for (Map<String, String> regB : datosB) {
            String claveB = generarClaveNormalizada(regB, columnasClaveB);
            if (!claveB.isEmpty()) {
                indiceB.computeIfAbsent(claveB, k -> new ArrayList<>()).add(regB);
            }
        }
        
        for (Map<String, String> regA : datosA) {
            Map<String, Object> detalle = new LinkedHashMap<>();
            
            String claveOriginalA = generarClave(regA, columnasClaveA);
            String claveNormalizadaA = generarClaveNormalizada(regA, columnasClaveA);
            
            List<Map<String, String>> coincidencias = indiceB.get(claveNormalizadaA);
            
            if (coincidencias == null || coincidencias.isEmpty()) {
                detalle.put("ESTADO", "NO ENCONTRADO");
                detalle.put("CLAVE", claveOriginalA);
                detalle.put("COINCIDENCIAS", 0);
                noHallados++;
                noEncontrados.add(regA);
            } else {
                detalle.put("ESTADO", "ENCONTRADO");
                detalle.put("CLAVE", claveOriginalA);
                detalle.put("COINCIDENCIAS", coincidencias.size());
                encontrados++;
                datosAEncontrados.add(regA);
                
                Map<String, String> mejorCoincidencia = coincidencias.get(0);
                
                for (String col : columnasClaveA) {
                    String valA = regA.get(col);
                    String valB = mejorCoincidencia.get(columnasClaveB.get(columnasClaveA.indexOf(col)));
                    detalle.put("COL_" + col + "_A", valA);
                    detalle.put("COL_" + col + "_B", valB);
                }
                
                for (Map.Entry<String, String> entry : regA.entrySet()) {
                    detalle.put("A_" + entry.getKey(), entry.getValue());
                }
                
                for (Map.Entry<String, String> entry : mejorCoincidencia.entrySet()) {
                    detalle.put("B_" + entry.getKey(), entry.getValue());
                }
            }
            
            resumen.add(detalle);
        }
        
        final int totalA = datosA.size();
        final int totalB = datosB.size();
        final int encontradosFinal = encontrados;
        final int noHalladosFinal = noHallados;
        
        Map<String, Object> datosA_Map = new HashMap<>();
        datosA_Map.put("encontrados", datosAEncontrados);
        datosA_Map.put("noEncontrados", noEncontrados);
        
        Map<String, Object> datosB_Map = new HashMap<>();
        datosB_Map.put("todos", datosB);
        
        Map<String, String> estadisticasMap = new LinkedHashMap<>();
        estadisticasMap.put("Total registros Archivo A", String.valueOf(totalA));
        estadisticasMap.put("Total registros Archivo B", String.valueOf(totalB));
        estadisticasMap.put("Encontrados", String.valueOf(encontradosFinal));
        estadisticasMap.put("No encontrados", String.valueOf(noHalladosFinal));
        estadisticasMap.put("Porcentaje encontrado", String.format("%.1f%%", 
            totalA == 0 ? 0 : (100.0 * encontradosFinal / totalA)));
        
        resultado.put("datosA", datosA_Map);
        resultado.put("datosB", datosB_Map);
        resultado.put("resumen", resumen);
        resultado.put("columnasClaveA", columnasClaveA);
        resultado.put("columnasClaveB", columnasClaveB);
        resultado.put("estadisticas", estadisticasMap);
        
        return resultado;
    }
    
    /**
     * Genera un resultado de cruce simplificado (todas las claves son obligatorias).
     * 
     * Este es un atajo para el metodo generarResultadoConModo donde todas
     * las columnas clave se consideran obligatorias y el modo es AND.
     * 
     * @param datosA Registros del Archivo A
     * @param columnasClaveA Columnas clave de A
     * @param datosB Registros del Archivo B
     * @param columnasClaveB Columnas clave de B
     * @return Map con listas de verificados, no encontrados y estadisticas
     */
    public Map<String, Object> generarResultadoNuevo(
            List<Map<String, String>> datosA,
            List<String> columnasClaveA,
            List<Map<String, String>> datosB,
            List<String> columnasClaveB) {
        
        List<Boolean> todasObligatorias = new ArrayList<>();
        for (int i = 0; i < columnasClaveA.size(); i++) {
            todasObligatorias.add(true);
        }
        
        return generarResultadoConModo(datosA, columnasClaveA, datosB, columnasClaveB, todasObligatorias, false);
    }
    
    /**
     * Genera resultado de cruce con modo configurable (AND/OR).
     * 
     * Este es el metodo principal para realizar cruces de datos.
     * 
     * MODOS DE CRUCE:
     * - matchAny = false (AND/TODAS): Todas las columnas clave deben coincidir
     * - matchAny = true (OR/CUALQUIERA): Al menos una columna clave debe coincidir
     * 
     * ESTRUCTURA DEL RESULTADO:
     * <pre>
     * {
     *     "resultadoA": [...],           // Todos los registros de A con estado
     *     "verificados": [...],          // Solo registros ENCONTRADOS
     *     "noEncontrados": [...],        // Solo registros NO ENCONTRADOS
     *     "estadisticas": {...},         // Map con estadisticas
     *     "columnasClaveA": [...],       // Columnas usadas de A
     *     "columnasClaveB": [...]        // Columnas usadas de B
     * }
     * </pre>
     * 
     * COLUMNAS AGREGADAS A CADA REGISTRO:
     * - CRUCE_ESTADO: "ENCONTRADO" o "NO ENCONTRADO"
     * - CRUCE_CLAVE: Valor de la clave de cruce
     * - COINCIDENCIAS_EN_B: Numero de registros coincidentes
     * - CRUCE_MODO: "AND" o "OR"
     * 
     * @param datosA Lista de registros del Archivo A
     * @param columnasClaveA Nombres de columnas clave en A
     * @param datosB Lista de registros del Archivo B
     * @param columnasClaveB Nombres de columnas clave en B
     * @param obligatorio Lista de Boolean indicando si cada columna es obligatoria (null = todas obligatorias)
     * @param matchAny false = modo AND (todas deben coincidir), true = modo OR (al menos una)
     * @return Map con listas separadas de verificados, no encontrados y estadisticas
     * 
     * @Ejemplo AND (TODAS):
     * <pre>
     * // Ambos (numero_cuenta Y fecha) deben existir en B
     * Map&lt;String, Object&gt; resultado = validador.generarResultadoConModo(
     *     datosA, Arrays.asList("numero_cuenta", "fecha"),
     *     datosB, Arrays.asList("numero_cuenta", "fecha"),
     *     null, false
     * );
     * </pre>
     * 
     * @Ejemplo OR (CUALQUIERA):
     * <pre>
     * // Al menos numero_cuenta O fecha debe existir en B
     * Map&lt;String, Object&gt; resultado = validador.generarResultadoConModo(
     *     datosA, Arrays.asList("numero_cuenta", "fecha"),
     *     datosB, Arrays.asList("numero_cuenta", "fecha"),
     *     null, true
     * );
     * </pre>
     */
    public Map<String, Object> generarResultadoConModo(
            List<Map<String, String>> datosA,
            List<String> columnasClaveA,
            List<Map<String, String>> datosB,
            List<String> columnasClaveB,
            List<Boolean> obligatorio,
            boolean matchAny) {
        
        Map<String, Object> resultado = new LinkedHashMap<>();
        
        List<Map<String, String>> resultadoA = new ArrayList<>();
        List<Map<String, String>> noEncontrados = new ArrayList<>();
        List<Map<String, String>> verificados = new ArrayList<>();
        Set<String> clavesVerificadosUnicas = new HashSet<>();
        
        int encontrados = 0;
        int noHallados = 0;
        int coincidenciasParciales = 0;
        
        Map<String, List<Map<String, String>>> indiceB = new HashMap<>();
        for (Map<String, String> regB : datosB) {
            String claveB = generarClaveNormalizada(regB, columnasClaveB);
            if (!claveB.isEmpty()) {
                indiceB.computeIfAbsent(claveB, k -> new ArrayList<>()).add(regB);
            }
        }
        
        for (Map<String, String> regA : datosA) {
            Map<String, String> nuevoRegistro = new LinkedHashMap<>();
            
            for (Map.Entry<String, String> entry : regA.entrySet()) {
                nuevoRegistro.put(entry.getKey(), entry.getValue());
            }
            
            String claveOriginalA = generarClave(regA, columnasClaveA);
            String claveNormalizadaA = generarClaveNormalizada(regA, columnasClaveA);
            
            List<Map<String, String>> coincidencias = indiceB.get(claveNormalizadaA);
            
            boolean coincide = coincidencias != null && !coincidencias.isEmpty();
            
            if (coincide) {
                nuevoRegistro.put("CRUCE_ESTADO", "ENCONTRADO");
                nuevoRegistro.put("CRUCE_CLAVE", claveOriginalA);
                nuevoRegistro.put("COINCIDENCIAS_EN_B", String.valueOf(coincidencias.size()));
                nuevoRegistro.put("CRUCE_MODO", matchAny ? "OR" : "AND");
                encontrados++;
                if (clavesVerificadosUnicas.add(claveNormalizadaA)) {
                    verificados.add(nuevoRegistro);
                }
            } else {
                nuevoRegistro.put("CRUCE_ESTADO", "NO ENCONTRADO");
                nuevoRegistro.put("CRUCE_CLAVE", claveOriginalA);
                nuevoRegistro.put("COINCIDENCIAS_EN_B", "0");
                nuevoRegistro.put("CRUCE_MODO", matchAny ? "OR" : "AND");
                noHallados++;
                noEncontrados.add(nuevoRegistro);
            }
            
            resultadoA.add(nuevoRegistro);
        }
        
        Map<String, String> estadisticas = new LinkedHashMap<>();
        estadisticas.put("Total registros Archivo A", String.valueOf(datosA.size()));
        estadisticas.put("Total registros Archivo B", String.valueOf(datosB.size()));
        estadisticas.put("Modo de cruce", matchAny ? "CUALQUIERA (OR)" : "TODAS (AND)");
        estadisticas.put("Relaciones definidas", String.valueOf(columnasClaveA.size()));
        estadisticas.put("Cuentas ENCONTRADAS (unicas)", String.valueOf(encontrados));
        estadisticas.put("Cuentas NO encontradas (unicas)", String.valueOf(noHallados));
        estadisticas.put("Porcentaje efectividad", String.format("%.1f%%", 
            datosA.isEmpty() ? 0 : (100.0 * encontrados / datosA.size())));
        
        resultado.put("resultadoA", resultadoA);
        resultado.put("verificados", verificados);
        resultado.put("noEncontrados", noEncontrados);
        resultado.put("estadisticas", estadisticas);
        resultado.put("columnasClaveA", columnasClaveA);
        resultado.put("columnasClaveB", columnasClaveB);
        
        return resultado;
    }
}
