# GUIA DE INTEGRACION - CUADRE DE ARCHIVOS

Esta guia explica como usar las clases de cruce de datos en cualquier proyecto Java.

---

## 1. CLASES DISPONIBLES

| Clase | Proposito |
|-------|-----------|
| `LectorExcel` | Leer archivos Excel (.xlsx) |
| `ValidadorCruce` | Cruzar datos entre dos conjuntos |
| `EscritorExcel` | Generar reporte Excel con resultados |

---

## 2. ESTRUCTURA DE DATOS

Los datos se manejan como `List<Map<String, String>>`:

```java
// Cada registro es un Map donde la clave es el nombre de la columna
Map<String, String> registro = new HashMap<>();
registro.put("numero_cuenta", "12345");
registro.put("nombre", "Juan Perez");
registro.put("saldo", "100000");

// La lista de datos es una List de estos Maps
List<Map<String, String>> datos = new ArrayList<>();
datos.add(registro);
```

---

## 3. PASO A PASO COMPLETO

### 3.1 Leer Archivos Excel

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

### 3.2 Configurar Columnas de Cruce

```java
// Definir que columnas usar para el cruce
// La primera columna es la PRIMARY KEY (obligatoria)
List<String> columnasA = Arrays.asList("numero_cuenta");
List<String> columnasB = Arrays.asList("numero_cuenta");

// Cruce con multiples columnas (todas deben existir en ambos archivos)
List<String> columnasA = Arrays.asList("numero_cuenta", "fecha");
List<String> columnasB = Arrays.asList("num_cuenta", "fecha_trans");
```

### 3.3 Ejecutar el Cruce

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

### 3.4 Generar Reporte Excel

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

## 4. MODOS DE CRUCE

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

## 5. OBTENER RESULTADOS

### 5.1 Estadisticas

```java
Map<String, String> estadisticas = (Map<String, String>) resultado.get("estadisticas");

String totalA = estadisticas.get("Total registros Archivo A");
String encontrados = estadisticas.get("Cuentas ENCONTRADAS (unicas)");
String noEncontrados = estadisticas.get("Cuentas NO encontradas (unicas)");
String porcentaje = estadisticas.get("Porcentaje efectividad");
```

### 5.2 Solo Encontrados

```java
List<Map<String, String>> verificados = (List<Map<String, String>>) resultado.get("verificados");

for (Map<String, String> registro : verificados) {
    String clave = registro.get("CRUCE_CLAVE");
    String estado = registro.get("CRUCE_ESTADO");
    System.out.println(clave + " -> " + estado);
}
```

### 5.3 Solo No Encontrados

```java
List<Map<String, String>> noEncontrados = (List<Map<String, String>>) resultado.get("noEncontrados");

for (Map<String, String> registro : noEncontrados) {
    String clave = registro.get("CRUCE_CLAVE");
    System.out.println("FALTA: " + clave);
}
```

### 5.4 Todos con Estado

```java
List<Map<String, String>> todos = (List<Map<String, String>>) resultado.get("resultadoA");

for (Map<String, String> registro : todos) {
    String estado = registro.get("CRUCE_ESTADO");  // "ENCONTRADO" o "NO ENCONTRADO"
    String clave = registro.get("CRUCE_CLAVE");
    String coincidencias = registro.get("COINCIDENCIAS_EN_B");
}
```

---

## 6. CALLBACK DE PROGRESO

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

## 7. EJEMPLO COMPLETO EN UNA SOLA CLASE

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

## 8. IMPORTAR EN OTRO PROYECTO

### 8.1 Copiar clases necesarias

```
src/main/java/com/banco/cuadre/
├── LectorExcel.java       ← Copiar
├── ValidadorCruce.java    ← Copiar
└── EscritorExcel.java      ← Copiar
```

### 8.2 Agregar dependencia Maven

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

### 8.3 Compilar

```bash
mvn clean compile
```

---

## 9. LIMITACIONES Y NOTAS

- Solo soporta archivos `.xlsx` (Excel 2007+)
- Tamanio maximo de archivo: 500 MB
- Maximo de filas por hoja: 1,000,000
- Maximo de columnas: 500
- La normalizacion elimina ceros a la izquierda en numeros

---

**Version**: 1.0.0
