# GUIA DE INTEGRACION - FILE COMPARATOR

Esta guia explica como usar las clases de cruce de datos en cualquier proyecto Java.

---

## 1. CAPACIDADES DEL PROYECTO

### Que puedes validar?

| Tipo de validacion | Ejemplo |
|--------------------|---------|
| Migracion de datos | Verificar que clientes迁移 correctamente |
| Exportaciones | Comparar CSV de diferentes sistemas |
| Archivos de bancos | Cruzar movimientos con extractos |
| Inventarios | Validar existencia vs. mouvements |
| Auditoria | Detectar cambios no autorizados |

### Caracteristicas clave:

- **Normalizacion inteligente**: Elimina ceros a la izquierda, normaliza mayusculas
- **Multi-columna**: Usa varias columnas para mayor precision
- **Modos flexibles**: AND (todas coinciden) u OR (al menos una)
- **Archivos grandes**: Hasta 500 MB por archivo
- **Multi-hoja**: Procesa cualquier hoja del Excel

---

## 2. CLASES DISPONIBLES

| Clase | Proposito | Metodo principal |
|-------|-----------|------------------|
| `LectorExcel` | Leer archivos Excel | `leerArchivo(ruta)` |
| `ValidadorCruce` | Cruzar datos | `generarResultadoConModo(...)` |
| `EscritorExcel` | Generar reportes | `guardarResultadoNuevo(...)` |

---

## 3. ESTRUCTURA DE DATOS

Los datos se manejan como `List<Map<String, String>>`:

```java
// Cada registro es un Map donde la clave es el nombre de la columna
Map<String, String> registro = new HashMap<>();
registro.put("id_cliente", "12345");
registro.put("nombre", "Juan Perez");
registro.put("saldo", "100000");

// La lista de datos es una List de estos Maps
List<Map<String, String>> datos = new ArrayList<>();
datos.add(registro);
```

---

## 4. PASO A PASO COMPLETO

### 4.1 Leer Archivos Excel

```java
// Crear instancia del lector
LectorExcel lector = new LectorExcel();

// Leer archivo origen (Archivo A)
LectorExcel.InfoArchivo archivoA = lector.leerArchivo("C:\\datos\\origen.xlsx");

// Leer archivo destino (Archivo B)
LectorExcel.InfoArchivo archivoB = lector.leerArchivo("C:\\datos\\destino.xlsx");

// Obtener la primera hoja (puede iterar por todas con archivo.hojas)
LectorExcel.InfoHoja hojaA = archivoA.hojas.get(0);
LectorExcel.InfoHoja hojaB = archivoB.hojas.get(0);

// Acceso a los datos
System.out.println("Columnas: " + hojaA.encabezados);  // ["col1", "col2", ...]
System.out.println("Registros: " + hojaA.datos.size()); // numero de filas
```

### 4.2 Configurar Columnas de Cruce

```java
// Definir que columnas usar para el cruce
// La primera columna es la PRIMARY KEY (obligatoria)
List<String> columnasA = Arrays.asList("numero_cuenta");
List<String> columnasB = Arrays.asList("numero_cuenta");

// Cruce con multiples columnas
List<String> columnasA = Arrays.asList("numero_cuenta", "fecha");
List<String> columnasB = Arrays.asList("num_cuenta", "fecha_trans");
```

### 4.3 Ejecutar el Cruce

```java
// Crear validador
ValidadorCruce validador = new ValidadorCruce();

// Ejecutar cruce
Map<String, Object> resultado = validador.generarResultadoConModo(
    hojaA.datos,      // Datos del archivo A (List<Map<String, String>>)
    columnasA,        // Columnas clave de A (List<String>)
    hojaB.datos,      // Datos del archivo B (List<Map<String, String>>)
    columnasB,        // Columnas clave de B (List<String>)
    null,             // Obligatorio (null = todas)
    false             // false = modo AND, true = modo OR
);
```

### 4.4 Generar Reporte Excel

```java
// Crear escritor
EscritorExcel escritor = new EscritorExcel();

// Generar archivo de salida con 4 hojas
escritor.guardarResultadoNuevo(
    resultado,                              // Resultado del cruce
    "C:\\reportes\\cruce_2024.xlsx",       // Ruta de salida
    null                                    // Callback de progreso (puede ser null)
);
```

---

## 5. MODOS DE CRUCE

### Modo AND (TODAS) - `matchAny = false`

Todas las columnas seleccionadas deben existir en el archivo B.

```java
// El registro se considera encontrado SOLO si:
// numero_cuenta EXISTE en B Y fecha EXISTE en B
validador.generarResultadoConModo(datosA, colsA, datosB, colsB, null, false);
```

### Modo OR (CUALQUIERA) - `matchAny = true`

Al menos una columna debe existir en el archivo B.

```java
// El registro se considera encontrado si:
// numero_cuenta EXISTE en B O fecha EXISTE en B
validador.generarResultadoConModo(datosA, colsA, datosB, colsB, null, true);
```

---

## 6. OBTENER RESULTADOS

### 6.1 Estadisticas

```java
Map<String, String> estadisticas = (Map<String, String>) resultado.get("estadisticas");

String totalA = estadisticas.get("Total registros Archivo A");
String encontrados = estadisticas.get("Cuentas ENCONTRADAS (unicas)");
String noEncontrados = estadisticas.get("Cuentas NO encontradas (unicas)");
String porcentaje = estadisticas.get("Porcentaje efectividad");
```

### 6.2 Solo Encontrados

```java
List<Map<String, String>> verificados = (List<Map<String, String>>) resultado.get("verificados");

for (Map<String, String> registro : verificados) {
    String clave = registro.get("CRUCE_CLAVE");
    String estado = registro.get("CRUCE_ESTADO");
    System.out.println(clave + " -> " + estado);
}
```

### 6.3 Solo No Encontrados

```java
List<Map<String, String>> noEncontrados = (List<Map<String, String>>) resultado.get("noEncontrados");

for (Map<String, String> registro : noEncontrados) {
    String clave = registro.get("CRUCE_CLAVE");
    System.out.println("FALTA: " + clave);
}
```

### 6.4 Todos con Estado

```java
List<Map<String, String>> todos = (List<Map<String, String>>) resultado.get("resultadoA");

for (Map<String, String> registro : todos) {
    String estado = registro.get("CRUCE_ESTADO");  // "ENCONTRADO" o "NO ENCONTRADO"
    String clave = registro.get("CRUCE_CLAVE");
    String coincidencias = registro.get("COINCIDENCIAS_EN_B");
}
```

---

## 7. CALLBACK DE PROGRESO

Para archivos grandes, puedes mostrar progreso:

```java
escritor.guardarResultadoNuevo(resultado, "salida.xlsx", new EscritorExcel.ProgressCallback() {
    @Override
    public void onProgress(int percent, String message) {
        System.out.println("[" + percent + "%] " + message);
    }
});
```

---

## 8. NORMALIZACION DE DATOS

El sistema normaliza automaticamente:

| Entrada | Salida | Ejemplo |
|---------|--------|---------|
| Numerico con ceros | Sin ceros izquierda | "00123" → "123" |
| Mayusculas | Minusculas | "ABC" → "abc" |
| Espacios | Eliminados | " abc " → "abc" |
| Decimal | Entero | "001" → "1" |

---

## 9. ESTRUCTURA DEL REPORTE GENERADO

El archivo Excel contiene 4 hojas:

### Hoja 1: Resumen
- Estadisticas generales
- Porcentaje de efectividad
- Relaciones configuradas

### Hoja 2: Todos
- Todos los registros del archivo A
- Columnas de estado agregadas

### Hoja 3: Verificados
- Solo registros encontrados en B

### Hoja 4: No Encontrados
- Solo registros NO encontrados en B

---

## 10. COLUMNAS AGREGADAS POR EL CRUCE

A cada registro se le agregan estas columnas:

| Columna | Descripcion |
|---------|------------|
| CRUCE_ESTADO | "ENCONTRADO" o "NO ENCONTRADO" |
| CRUCE_CLAVE | Valor de la clave de cruce |
| COINCIDENCIAS_EN_B | Numero de coincidencias en archivo B |
| CRUCE_MODO | "AND" o "OR" |

---

## 11. EJEMPLO COMPLETO EN UNA SOLA CLASE

```java
package com.miproyecto;

import com.banco.cuadre.LectorExcel;
import com.banco.cuadre.ValidadorCruce;
import com.banco.cuadre.EscritorExcel;
import java.util.*;

public class MiCruce {
    public static void main(String[] args) {
        try {
            // 1. Leer archivos
            LectorExcel lector = new LectorExcel();
            LectorExcel.InfoArchivo archivoA = lector.leerArchivo("origen.xlsx");
            LectorExcel.InfoArchivo archivoB = lector.leerArchivo("destino.xlsx");
            
            // 2. Obtener datos de la primera hoja
            List<Map<String, String>> datosA = archivoA.hojas.get(0).datos;
            List<Map<String, String>> datosB = archivoB.hojas.get(0).datos;
            
            // 3. Definir columnas de cruce
            List<String> colsA = Arrays.asList("numero_cuenta");
            List<String> colsB = Arrays.asList("numero_cuenta");
            
            // 4. Cruzar datos (modo AND)
            ValidadorCruce validador = new ValidadorCruce();
            Map<String, Object> resultado = validador.generarResultadoConModo(
                datosA, colsA, datosB, colsB, null, false
            );
            
            // 5. Mostrar estadisticas
            Map<String, String> stats = (Map<String, String>) resultado.get("estadisticas");
            System.out.println("Encontrados: " + stats.get("Cuentas ENCONTRADAS (unicas)"));
            System.out.println("No encontrados: " + stats.get("Cuentas NO encontradas (unicas)"));
            
            // 6. Generar reporte
            EscritorExcel escritor = new EscritorExcel();
            escritor.guardarResultadoNuevo(resultado, "resultado_cruce.xlsx", null);
            
            System.out.println("Cruce completado!");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

---

## 12. IMPORTAR EN OTRO PROYECTO

### 12.1 Copiar clases necesarias

```
src/main/java/com/banco/cuadre/
├── LectorExcel.java       ← Copiar
├── ValidadorCruce.java    ← Copiar
└── EscritorExcel.java      ← Copiar
```

### 12.2 Agregar dependencia Maven

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

### 12.3 Compilar

```bash
mvn clean compile
```

---

## 13. LIMITACIONES

- Solo soporta archivos `.xlsx` (Excel 2007+)
- Tamanio maximo de archivo: 500 MB
- Maximo de filas por hoja: 1,000,000
- Maximo de columnas: 500

---

**Version**: 1.0.0
