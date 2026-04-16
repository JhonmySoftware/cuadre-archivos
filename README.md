# Cuadre de Archivos - Banco Davivienda

## Descripcion

Aplicacion de escritorio para el cruce y validacion de archivos Excel en el area funcional de Banco Davivienda. Permite verificar que la informacion de un archivo A (Origen) haya sido correctamente transferida al archivo B (Destino).

## Requisitos del Sistema

- **Java**: Version 1.8 o superior (JDK 8+)
- **Sistema Operativo**: Windows 7/10/11, macOS, Linux
- **Memoria RAM**: Minimo 4 GB recomendado (8 GB para archivos grandes)
- **Espacio en disco**: 100 MB para instalacion

## Caracteristicas Principales

### 1. Seleccion de Archivos
- Seleccion de archivos Excel (.xlsx) para archivo A (Origen) y B (Destino)
- Soporte para multiples hojas por archivo
- Auto-deteccion de hojas disponibles

### 2. Relaciones de Columnas
- Sistema de relaciones uno-a-uno entre columnas
- Boton "+ Agregar relacion" para crear nuevas relaciones
- Boton "+ Auto-detectar relaciones" para coincidencia automatica
- Buscador/filtro en cada lista de columnas
- Eliminacion de relaciones individuales con boton "X"

### 3. Normalizacion Inteligente
- Eliminacion de ceros a la izquierda en valores numericos
- Comparacion case-insensitive
- Manejo de espacios en blanco

### 4. Resultados en Excel (4 Hojas)
- **1-Resumen**: Estadisticas generales y relacion de columnas usadas
- **2-Todos**: Todos los registros con estado de cruce
- **3-Verificados**: Registros unicos encontrados (sin duplicados)
- **4-No Encontrados**: Registros que no tienen coincidencia

## Colores Institucionales Davivienda

| Color | Hexadecimal | Uso |
|-------|-------------|-----|
| Rojo | #D2141E | Estados de error, titulos negativos |
| Amarillo | #FFC800 | Destacados, marcas |
| Verde | #008C50 | Estados exitosos, verificados |
| Azul | #1E3250 | Encabezados, informacion general |

## Medidas de Seguridad

### Validaciones Implementadas

1. **Tamano de archivo**: Limite maximo de 500 MB por archivo de entrada
2. **Limite de filas**: Maximo 1,000,000 de filas por hoja
3. **Limite de columnas**: Maximo 500 columnas por hoja
4. **Tamano de celda**: Truncado automatico a 32,767 caracteres
5. **Permisos de archivo**: Verificacion de lectura antes de procesar
6. **Ruta de salida**: Creacion segura del archivo de resultado

### Buenas Practicas de Seguridad

- No se registran datos sensibles en logs
- Validacion de extensiones de archivo (.xlsx unicamente)
- Excepciones controladas sin exposicion de informacion interna
- Memoria gestionada con streaming para archivos grandes

## Estructura del Proyecto

```
cuadre-archivos/
├── pom.xml                          # Configuracion Maven
├── src/main/java/com/banco/cuadre/
│   ├── CuadreApp.java              # Interfaz grafica Swing
│   ├── LectorExcel.java             # Lectura de archivos Excel
│   ├── ValidadorCruce.java          # Logica de cruce de datos
│   └── EscritorExcel.java           # Generacion de Excel con Davivienda
├── target/
│   └── cuadre-archivos-1.0.jar      # JAR ejecutable ( shaded )
└── README.md                        # Este archivo
```

## Compilacion y Ejecucion

### Compilar desde codigo fuente

```bash
cd cuadre-archivos
mvn clean package
```

### Ejecutar la aplicacion

```bash
# Usando JAR
java -jar target/cuadre-archivos-1.0.jar

# O directamente con Maven
mvn exec:java -Dexec.mainClass="com.banco.cuadre.CuadreApp"
```

## Manual de Usuario

### Paso 1: Seleccionar Archivos
1. Haga clic en "Seleccionar Archivo" para Archivo A (Origen)
2. Seleccione la hoja deseada del archivo A
3. Haga clic en "Seleccionar Archivo" para Archivo B (Destino)
4. Seleccione la hoja deseada del archivo B

### Paso 2: Definir Relaciones
1. Haga clic en "+ Agregar relacion" para crear una nueva
2. Seleccione la columna del Archivo A
3. Seleccione la columna correspondiente del Archivo B
4. Use el buscador para filtrar columnas
5. Repita para cada relacion necesaria

**Auto-deteccion**: Puede usar "+ Auto-detectar relaciones" para que el sistema intente encontrar columnas con nombres similares automaticamente.

### Paso 3: Ejecutar Cruce
1. Verifique que todas las relaciones sean correctas
2. Haga clic en "EJECUTAR CRUCE"
3. Observe la barra de progreso
4. Seleccione la ubicacion para guardar el resultado

### Paso 4: Analizar Resultados
Abra el archivo Excel generado con las 4 hojas:
- **1-Resumen**: Muestra estadisticas generales y columnas usadas
- **2-Todos**: Lista completa con estado (VERDE=Encontrado, ROJO=No encontrado)
- **3-Verificados**: Solo registros encontrados sin duplicados
- **4-No Encontrados**: Solo registros sin coincidencia para investigacion

## Arquitectura Tecnica

### Modelo de Datos

```java
// Informacion de archivo
InfoArchivo {
    String ruta;
    String nombre;
    List<InfoHoja> hojas;
}

// Informacion de hoja
InfoHoja {
    String nombre;
    int totalFilas;
    int totalColumnas;
    List<String> encabezados;
    List<Map<String, String>> datos;
}
```

### Logica de Cruce

El cruce utiliza un indice Hash para busqueda eficiente O(n):
1. Se normalizan las claves del Archivo B
2. Se construye un indice por clave normalizada
3. Se buscan coincidencias en el Archivo A
4. Se reportan encontrados y no encontrados

### Normalizacion

```java
// Ejemplos de normalizacion
"00123"      -> "123"
"00001"      -> "1"
"  ABC  "    -> "abc"
"12.34"      -> "12.34" (decimal se mantiene)
```

## Dependencias

| Dependencia | Version | Proposito |
|-------------|---------|-----------|
| Apache POI | 5.2.5 | Lectura/escritura Excel |
| SLF4J | 2.0.11 | Logging |
| Logback | 1.4.14 | Implementacion de logging |

## Consideraciones de Rendimiento

- Archivos menores a 50,000 filas: Respuesta inmediata
- Archivos de 50,000 - 500,000 filas: Proceso en segundos
- Archivos mayores a 500,000 filas: Usar memoria adicional con -Xmx2g

```bash
java -Xmx2g -jar target/cuadre-archivos-1.0.jar
```

## Soporte y Mantenimiento

Para reportar problemas o solicitar mejoras, contacte al equipo de desarrollo.

### Version
1.0.0 - Implementacion inicial

### Historial de Cambios

- v1.0.0: Version inicial con GUI Swing, colores Davivienda, y 4 hojas de resultado
