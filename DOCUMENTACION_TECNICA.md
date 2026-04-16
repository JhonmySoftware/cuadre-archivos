# DOCUMENTACION TECNICA
## Cuadre de Archivos - File Comparator

---

## 1. INFORMACION GENERAL

| Campo | Valor |
|-------|-------|
| **Nombre** | Cuadre de Archivos |
| **Version** | 1.0.0 |
| **Empresa** | Equipo de Desarrollo |
| **Proposito** | Validacion y cruce de archivos Excel |
| **Java** | 1.8+ |
| **Repositorio** | https://github.com/JhonmySoftware/cuadre-archivos |

---

## 2. ESTRUCTURA DEL PROYECTO

```
cuadre-archivos/
├── src/main/java/com/banco/cuadre/
│   ├── CuadreApp.java        # GUI principal (Swing)
│   ├── LectorExcel.java      # Lectura de archivos Excel
│   ├── ValidadorCruce.java   # Logica de cruce de datos
│   └── EscritorExcel.java    # Generacion de Excel con estilos
├── src/main/resources/
│   └── logback.xml          # Configuracion de logs
├── src/test/java/com/banco/cuadre/
│   └── ValidadorCruceTest.java  # Pruebas unitarias
├── pom.xml                   # Configuracion Maven
├── run.bat                   # Script de ejecucion
├── README.md                 # Documentacion general
├── DOCUMENTACION_TECNICA.md  # Este documento
├── DOCUMENTACION_FUNCIONAL.md # Manual de usuario
├── CONTRIBUTING.md           # Guia de contribucion
├── LICENSE                   # Licencia MIT
├── .gitignore                # Archivos ignorados
├── .editorconfig             # Estandares de codigo
└── .github/workflows/
    └── java-ci.yml           # Pipeline CI/CD
```

---

## 3. DEPENDENCIAS

### 3.1 Dependencias Maven (pom.xml)

```xml
<properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <poi.version>5.2.5</poi.version>
    <slf4j.version>1.7.36</slf4j.version>
    <logback.version>1.2.12</logback.version>
    <junit.version>4.13.2</junit.version>
</properties>

<dependencies>
    <!-- Apache POI - Lectura/Escritura Excel -->
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
    
    <!-- Logging -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.36</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.12</version>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 3.2 Matriz de Compatibilidad

| Dependencia | Version | Java Minimo | Proposito |
|-------------|---------|-------------|-----------|
| Apache POI | 5.2.5 | Java 8 | Excel |
| SLF4J | 1.7.36 | Java 5 | Logging API |
| Logback | 1.2.12 | Java 6 | Logging Impl |
| JUnit | 4.13.2 | Java 5 | Testing |

---

## 4. ARQUITECTURA

### 4.1 Diagrama de Componentes

```
+------------------+     +------------------+     +------------------+
|   CuadreApp      | --> |   LectorExcel    | --> |   ValidadorCruce |
|   (GUI Swing)    |     |   (POI Reader)   |     |   (Logica)       |
+------------------+     +------------------+     +------------------+
         |                                                    |
         v                                                    v
+------------------+                              +------------------+
|   Logback.xml    |                              |   EscritorExcel  |
|   (Config Logs)  |                              |   (POI Writer)    |
+------------------+                              +------------------+
                                                           |
                                                           v
                                                  +------------------+
                                                  |   Resultado.xlsx |
                                                  |   (4 hojas)      |
                                                  +------------------+
```

### 4.2 Flujo de Ejecucion

```
1. Usuario selecciona Archivo A (Origen)
   └─> LectorExcel.leerArchivo() -> InfoArchivo
   
2. Usuario selecciona Archivo B (Destino)
   └─> LectorExcel.leerArchivo() -> InfoArchivo
   
3. Usuario configura relaciones (PK + Keys)
   └─> RelacionItem[] -> relaciones
   
4. Usuario ejecuta cruce
   └─> ValidadorCruce.generarResultadoConModo()
       ├─> Normalizar datos
       ├─> Buscar coincidencias (AND/OR)
       └─> Generar ResultadoCruce
   
5. Generar reporte Excel
   └─> EscritorExcel.guardarResultadoNuevo()
       ├─> Hoja 1: Resumen
       ├─> Hoja 2: Todos
       ├─> Hoja 3: Verificados
       └─> Hoja 4: No Encontrados
```

---

## 5. CLASES PRINCIPALES

### 5.1 CuadreApp.java (GUI)

**Responsabilidad**: Interfaz grafica de usuario.

**Constantes de Color**:
```java
private static final Color DAVI_ROJO = new Color(210, 20, 30);
private static final Color DAVI_ROJO_OSCURO = new Color(160, 15, 25);
private static final Color DAVI_ROJO_CLARO = new Color(220, 40, 50);
```

**Metodos Principales**:
| Metodo | Descripcion |
|--------|-------------|
| `crearPanelPrincipal()` | Construye la interfaz Swing completa |
| `panelSelectorArchivo(int num)` | Crea selector de archivo/hoja |
| `actualizarRelaciones()` | Actualiza panel de relaciones |
| `ejecutarCruce()` | Ejecuta el cruce y genera reporte |
| `cargarHojas(String archivo)` | Carga hojas del Excel seleccionado |

**Estado de la Aplicacion**:
```java
private LectorExcel.InfoArchivo infoArchivo1;  // Archivo Origen
private LectorExcel.InfoArchivo infoArchivo2;  // Archivo Destino
private List<RelacionItem> relaciones;          // Relaciones configuradas
private JRadioButton rbMatchAll;               // Modo AND
private JRadioButton rbMatchAny;               // Modo OR
```

### 5.2 LectorExcel.java

**Responsabilidad**: Lectura de archivos Excel (.xlsx).

**Clase InfoArchivo**:
```java
public static class InfoArchivo {
    String ruta;
    String nombre;
    List<InfoHoja> hojas;
}
```

**Clase InfoHoja**:
```java
public static class InfoHoja {
    String nombre;
    int totalFilas;
    int totalColumnas;
    List<String> encabezados;
    List<Map<String, String>> datos;  // {nombreColumna -> valor}
}
```

**Metodos Principales**:
| Metodo | Descripcion |
|--------|-------------|
| `leerArchivo(String ruta)` | Lee archivo Excel completo |
| `leerHoja(Sheet hoja, int totalCols)` | Lee una hoja especifica |
| `obtenerDato(Cell cell)` | Extrae valor de celda |

### 5.3 ValidadorCruce.java

**Responsabilidad**: Logica de negocio para cruce de datos.

**Clase ResultadoCruce**:
```java
public static class ResultadoCruce {
    int totalRegistros;
    int encontrados;
    int noEncontrados;
    List<Map<String, Object>> detalles;
}
```

**Metodos Principales**:
| Metodo | Descripcion |
|--------|-------------|
| `generarResultadoConModo(...)` | Cruce con modo AND/OR |
| `normalizarValor(String)` | Normaliza datos (ceros izq) |

### 5.4 EscritorExcel.java

**Responsabilidad**: Generacion de reportes Excel estilizados.

**Metodos Principales**:
| Metodo | Descripcion |
|--------|-------------|
| `guardarResultadoNuevo(...)` | Genera Excel de 4 hojas |
| `crearHojaResumen(...)` | Hoja 1: Estadisticas |
| `crearHojaTodos(...)` | Hoja 2: Todos registros |
| `crearHojaVerificados(...)` | Hoja 3: Solo encontrados |
| `crearHojaNoEncontrados(...)` | Hoja 4: Solo no encontrados |

---

## 6. MODELO DE DATOS

### 6.1 Relaciones de Cruce

```java
public static class RelacionItem {
    JComboBox<String> cmbA;      // Columna Archivo A
    JComboBox<String> cmbB;      // Columna Archivo B
    JButton btnEliminar;         // Eliminar relacion
    boolean esClavePrimaria;     // true = PK (obligatoria)
}
```

### 6.2 Resultado del Cruce

```java
Map<String, Object> resultado = {
    "resultadoA": List<Map<String, String>>,  // Todos registros A
    "verificados": List<Map<String, String>>,   // Encontrados en B
    "noEncontrados": List<Map<String, String>>, // No encontrados
    "estadisticas": Map<String, String>,        // {clave -> valor}
    "columnasClaveA": List<String>,             // Columnas usadas de A
    "columnasClaveB": List<String>              // Columnas usadas de B
}
```

---

## 7. LOGICA DE CRUCE

### 7.1 Modo TODAS (AND)

Todas las columnas seleccionadas deben coincidir:

```
CRUCE = (PK_A == PK_B) AND (Key1_A == Key1_B) AND (Key2_A == Key2_B)
```

**Ejemplo**: Si se seleccionan `numero_cuenta` y `fecha`, ambos valores deben existir tanto en A como en B.

### 7.2 Modo CUALQUIERA (OR)

Al menos una columna debe coincidir:

```
CRUCE = (PK_A == PK_B) OR (Key1_A == Key1_B) OR (Key2_A == Key2_B)
```

**Ejemplo**: Si se seleccionan `numero_cuenta` y `fecha`, el registro se considera encontrado si coincide cualquiera de los dos.

### 7.3 Normalizacion de Datos

| Entrada | Salida | Ejemplo |
|---------|--------|---------|
| Numerico con ceros | Sin ceros izquierda | "00123" → "123" |
| Mayusculas | Minusculas | "ABC" → "abc" |
| Con espacios | Sin espacios | " abc " → "abc" |
| Decimal | Entero | "001" → "1" |
| Null/Vacio | Cadena vacia | null → "" |

---

## 8. ESTILOS EXCEL (OUTPUT)

### 8.1 Colores

| Color | Hex | RGB | Uso |
|-------|-----|-----|-----|
| Rojo Principal | #D2141E | (210, 20, 30) | Headers |
| Rosa PK | #FFC0CB | (255, 192, 203) | Columnas Primary Key |
| Verde | #008000 | (0, 128, 0) | Estado: Encontrado |
| Rojo Error | #FF0000 | (255, 0, 0) | Estado: No encontrado |
| Blanco | #FFFFFF | (255, 255, 255) | Fondo datos |

### 8.2 Estructura del Excel Generado

**Hoja 1: Resumen**
```
+------------------------------------------+
|  BANCO DAVIVIENDA                        |
|  Informe de Validacion - Cruce de Archivos|
+------------------------------------------+
| Estadisticas                              |
|   Total registros: 1000                   |
|   Encontrados: 980                       |
|   No encontrados: 20                      |
|   Porcentaje: 98.0%                      |
+------------------------------------------+
| Relaciones configuradas                   |
|   numero_cuenta > numero_cuenta (PK)     |
|   fecha > fecha                          |
+------------------------------------------+
```

**Hoja 2: Todos** - Todos los registros de A con estado
**Hoja 3: Verificados** - Solo registros encontrados en B
**Hoja 4: No Encontrados** - Solo registros no hallados en B

---

## 9. SEGURIDAD Y VALIDACIONES

### 9.1 Validaciones de Entrada

| Validacion | Limite | Accion |
|------------|--------|--------|
| Extension archivo | .xlsx | Solo se acepta Excel 2007+ |
| Tamano archivo | 500 MB | Rechazo con mensaje |
| Filas por hoja | 1,000,000 | Warning, procesamiento parcial |
| Columnas | 500 | Soporte completo |
| Tamano celda | 32,767 chars | Truncado automatico |

### 9.2 Buenas Practicas Implementadas

- No se registran datos sensibles en logs
- Validacion de permisos de lectura antes de procesar
- Excepciones controladas sin exposicion de stack traces
- No se almacenan datos en cache persistentes

### 9.3 Configuracion de Logs

Archivo: `src/main/resources/logback.xml`

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </appender>
    
    <logger name="com.banco.cuadre" level="INFO" />
    <logger name="org.apache.poi" level="WARN" />
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

---

## 10. COMPILACION Y DISTRIBUCION

### 10.1 Compilar

```bash
mvn clean package
```

Resultado: `target/cuadre-archivos-1.0.0.jar` (~19 MB, uber-jar con todas las dependencias)

### 10.2 Ejecutar

```batch
java -jar cuadre-archivos-1.0.0.jar
```

O usando el script:
```batch
run.bat
```

### 10.3 Con Mas Memoria (Archivos Grandes)

```batch
java -Xmx2g -jar cuadre-archivos-1.0.0.jar
```

### 10.4 Requisitos del Sistema

| Recurso | Minimo | Recomendado |
|---------|--------|-------------|
| Java | JRE 8+ | JRE 11+ |
| RAM | 2 GB | 4 GB |
| Disco | 100 MB | 200 MB |
| OS | Windows 7 | Windows 10/11 |

---

## 11. CI/CD - GITHUB ACTIONS

El proyecto incluye pipeline de integracion continua en `.github/workflows/java-ci.yml`:

```yaml
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: '8'
      - run: mvn clean package
      - run: mvn test
```

---

## 12. PRUEBAS UNITARIAS

Ubicacion: `src/test/java/com/banco/cuadre/ValidadorCruceTest.java`

Ejecutar:
```bash
mvn test
```

Pruebas implementadas:
- Normalizacion de numeros con ceros izquierda
- Normalizacion de texto (mayusculas/minusculas)
- Comparacion de valores normalizados

---

## 13. MIGRACION A OTRO PROYECTO

Para incluir esta aplicacion en otro proyecto Java:

1. **Copiar clases**:
   - `src/main/java/com/banco/cuadre/*.java`
   - `src/main/resources/logback.xml`

2. **Agregar dependencias** (seccion 3.1)

3. **Compilar como JAR**:
   ```bash
   mvn clean package
   ```

4. **Ejecutar**:
   ```bash
   java -jar target/cuadre-archivos-1.0.0.jar
   ```

---

**Version**: 1.0.0  
**Ultima actualizacion**: 2026-04-16
