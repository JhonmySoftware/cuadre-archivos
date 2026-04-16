# CUADRE DE ARCHIVOS - DOCUMENTACION TECNICA
## Banco Davivienda

---

## 1. INFORMACION GENERAL

| Campo | Valor |
|-------|-------|
| **Nombre** | Cuadre de Archivos |
| **Version** | 1.0 |
| **Empresa** | Banco Davivienda |
| **Proposito** | Validacion y cruce de archivos Excel |
| **Java** | 1.8+ (requiere POI 5.x) |

---

## 2. ESTRUCTURA DEL PROYECTO

```
cuadre-archivos/
├── src/main/java/com/banco/cuadre/
│   ├── CuadreApp.java        # GUI principal (Swing)
│   ├── LectorExcel.java      # Lectura de archivos Excel
│   ├── ValidadorCruce.java   # Logica de cruce de datos
│   └── EscritorExcel.java    # Generacion de Excel con estilos
├── pom.xml                   # Configuracion Maven
├── cuadre.bat               # Script de ejecucion
├── target/
│   └── cuadre-archivos-1.0.jar  # JAR ejecutable
└── README.md
```

---

## 3. CLASES Y PAQUETES

### 3.1 Paquetes Requeridos

**Dependencias Maven (pom.xml):**
```xml
<dependencies>
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.5</version>
    </dependency>
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi</artifactId>
        <version>5.2.5</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.11</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.14</version>
    </dependency>
</dependencies>
```

### 3.2 Clases a Migrar

Para migrar a otro proyecto, copie:

1. **CuadreApp.java** - Interfaz grafica
   - Maneja la UI con Swing
   - Estados: `infoArchivo1`, `infoArchivo2`
   - Colecciones: `relaciones` (Lista de RelacionItem)
   - Metodos principales: `ejecutarCruce()`, `actualizarRelaciones()`

2. **LectorExcel.java** - Lectura de datos
   - Clase interna `InfoArchivo` - Informacion del archivo
   - Clase interna `InfoHoja` - Informacion de la hoja
   - Metodo: `leerArchivo(ruta)` - Retorna InfoArchivo

3. **ValidadorCruce.java** - Logica de negocio
   - Metodo: `generarResultadoConModo()` - Cruce con modo AND/OR
   - Metodo: `normalizarValor()` - Normaliza ceros izquierda

4. **EscritorExcel.java** - Generacion de reportes
   - Metodo: `guardarResultadoNuevo()` - Genera Excel de 4 hojas

### 3.3 Clase Main

```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        CuadreApp app = new CuadreApp();
        app.setVisible(true);
    });
}
```

---

## 4. ARQUITECTURA

### 4.1 Flujo de Datos

```
[Archivo A] --> [LectorExcel] --> [ValidadorCruce] --> [EscritorExcel] --> [Resultado.xlsx]
                        |                    |
                        v                    v
                   InfoArchivo         Map<String, Object>
                        |                    |
                        v                    v
                   InfoHoja          - resultadoA (List)
                        |                    - verificados (List)
                        v                    - noEncontrados (List)
                   datos (List)             - estadisticas (Map)
                                          - columnasClaveA/B (List)
```

### 4.2 Modelo de Datos

```java
// Informacion de Archivo
InfoArchivo {
    String ruta;
    String nombre;
    List<InfoHoja> hojas;
}

// Informacion de Hoja
InfoHoja {
    String nombre;
    int totalFilas;
    int totalColumnas;
    List<String> encabezados;
    List<Map<String, String>> datos;  // Cada Map es una fila
}

// Resultado del Cruce
Map<String, Object> {
    "resultadoA": List<Map<String, String>>,
    "verificados": List<Map<String, String>>,
    "noEncontrados": List<Map<String, String>>,
    "estadisticas": Map<String, String>,
    "columnasClaveA": List<String>,
    "columnasClaveB": List<String>
}
```

---

## 5. CONFIGURACION DE ESTILOS EXCEL

### 5.1 Colores Davivienda

| Color | IndexedColors | Uso |
|-------|--------------|-----|
| Rojo | RED | Headers, PK |
| Rosa | ROSE | Columnas Primary Key |
| Verde | GREEN | Estado: Encontrado |
| Rojo | RED | Estado: No encontrado |
| Gris | GREY_25_PERCENT | Secciones |
| Blanco | WHITE | Datos |

### 5.2 Estilos Definidos

```java
// Headers
styleHeader = crearEstilo(RED, WHITE, true, 10, CENTER);
styleHeaderLeft = crearEstilo(RED, WHITE, true, 10, LEFT);

// Celdas datos
styleCelda = crearEstilo(WHITE, BLACK, false, 10, LEFT);
styleCeldaPK = crearEstilo(ROSE, RED, true, 10, LEFT);

// Estados
styleCeldaOk = crearEstilo(WHITE, GREEN, true, 10, CENTER);
styleCeldaError = crearEstilo(WHITE, RED, true, 10, CENTER);
```

---

## 6. SEGURIDAD

### 6.1 Validaciones Implementadas

| Validacion | Limite | Descripcion |
|------------|--------|-------------|
| Tamano archivo | 500 MB | Maximo por archivo de entrada |
| Filas por hoja | 1,000,000 | Limite de procesamiento |
| Columnas | 500 | Maximo por hoja |
| Tamano celda | 32,767 chars | Truncado automatico |
| Permisos | Verificacion | Lectura antes de procesar |

### 6.2 Buenas Practicas

- No se registran datos sensibles en logs
- Validacion de extension (.xlsx unicamente)
- Excepciones controladas sin exposicion de stack traces
- Memoria gestionada con streaming para archivos grandes

---

## 7. INSTALACION Y DISTRIBUCION

### 7.1 Compilar

```bash
cd cuadre-archivos
mvn clean package
```

### 7.2 Estructura de Distribucion

```
distribucion/
├── cuadre-archivos-1.0.jar    # JAR sombreado (todo en uno)
├── cuadre.bat                   # Script de ejecucion
└── README.txt                   # Guia rapida
```

### 7.3 Requisitos del Sistema

- Java 8 o superior (JRE)
- Windows 7/10/11, Linux, macOS
- 4 GB RAM minimo (8 GB recomendado)
- 100 MB espacio en disco

### 7.4 Ejecucion

```bash
# Opcion 1: JAR directo
java -jar cuadre-archivos-1.0.jar

# Opcion 2: Script BAT
cuadre.bat

# Opcion 3: Con mas memoria
java -Xmx2g -jar cuadre-archivos-1.0.jar
```

---

## 8. ESTRUCTURA DEL EXCEL GENERADO

### 8.1 Hoja 1: Resumen

```
+------------------------------------------+
| BANCO DAVIVIENDA                          |
| Informe de Validacion - Cruce de Archivos |
+------------------------------------------+
| Estadisticas                               |
|   Total registros Archivo A: 1000        |
|   Cuentas ENCONTRADAS: 980              |
|   Cuentas NO encontradas: 20             |
|   Porcentaje efectividad: 98.0%          |
+------------------------------------------+
| Relaciones de Columnas                     |
|   1. numero_cuenta > numero_cuenta       |
|   2. fecha > fecha                      |
+------------------------------------------+
```

### 8.2 Hojas 2-4: Datos

| Columna | Descripcion |
|---------|-------------|
| [Datos originales] | Todas las columnas del Archivo A |
| CRUCE_ESTADO | ENCONTRADO / NO ENCONTRADO |
| CRUCE_CLAVE | Valor de la clave de cruce |
| COINCIDENCIAS_EN_B | Numero de coincidencias |

### 8.3 Resaltado Visual

- **Headers**: Fondo rojo, texto blanco
- **Primary Key**: Fondo rosa, texto rojo negrita
- **ENCONTRADO**: Texto verde negrita
- **NO ENCONTRADO**: Texto rojo negrita

---

## 9. MODO DE CRUCE

### 9.1 Logica AND (TODAS)

Todas las columnas seleccionadas deben coincidir para considerar el registro como "encontrado".

```
PK (obligatorio) AND Key1 AND Key2 AND ...
```

### 9.2 Logica OR (CUALQUIERA)

Al menos una columna debe coincidir.

```
PK (obligatorio) OR Key1 OR Key2 OR ...
```

---

## 10. NORMALIZACION DE DATOS

### 10.1 Reglas

| Entrada | Salida | Ejemplo |
|---------|--------|---------|
| Con ceros izquierda | Sin ceros | "00123" → "123" |
| Mayusculas | Minusculas | "ABC" → "abc" |
| Espacios | Eliminados | " abc " → "abc" |
| Numericos | Enteros | "001" → "1" |

---

## 11. CONTACTOS Y SOPORTE

Para soporte tecnico o reporte de errores, contacte al equipo de desarrollo.

**Version**: 1.0.0  
**Ultima actualizacion**: 2026
