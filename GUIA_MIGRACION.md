# GUIA DE MIGRACION - FILE COMPARATOR

Esta guia explica paso a paso como migrar el proyecto a otro proyecto Java.

---

## 1. QUE CLASES COPIAR

Copia estas 4 clases a tu proyecto:

```
TU_PROYECTO/src/main/java/com/tupackage/cuadre/
├── LectorExcel.java       ← OBLIGATORIO
├── ValidadorCruce.java    ← OBLIGATORIO
├── EscritorExcel.java     ← OBLIGATORIO
└── CuadreApp.java        ← OPCIONAL (solo si quieres la GUI)
```

**Nota**: Puedes renombrar el paquete `com.banco.cuadre` a `com.tupackage.cuadre`.

---

## 2. DEPENDENCIAS MAVEN

Agrega estas dependencias a tu `pom.xml`:

```xml
<dependencies>
    <!-- Apache POI - Lectura/Escritura Excel (.xlsx) -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.5</version>
    </dependency>
    
    <!-- Logging (necesario para que funcione) -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.36</version>
    </dependency>
</dependencies>
```

---

## 3. ESTRUCTURA DE TU PROYECTO

```
TU_PROYECTO/
├── pom.xml
├── src/main/java/
│   └── com/tupackage/
│       └── cuadre/
│           ├── LectorExcel.java
│           ├── ValidadorCruce.java
│           └── EscritorExcel.java
└── src/main/resources/    ← CREAR ESTE
    └── logback.xml         ← CREAR ESTE
```

---

## 4. ARCHIVO LOGBACK.XML

Crea `src/main/resources/logback.xml` con este contenido:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="com.tupackage.cuadre" level="INFO" />
    <logger name="org.apache.poi" level="WARN" />
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

---

## 5. POM.XML COMPLETO

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.tupackage</groupId>
    <artifactId>tu-proyecto</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Tu Proyecto</name>
    <description>Tu descripcion</description>
    
    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <!-- Apache POI -->
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.2.5</version>
        </dependency>
        
        <!-- SLF4J -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.36</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 6. COMANDOS MAVEN

### Compilar el proyecto
```bash
mvn clean compile
```

### Ejecutar tests (si agregas)
```bash
mvn test
```

### Crear JAR ejecutable
```bash
mvn clean package
```

### Ejecutar la aplicacion
```bash
java -jar target/tu-proyecto-1.0.0.jar
```

---

## 7. ACTUALIZAR IMPORTACIONES

Si cambias el nombre del paquete, actualiza los imports:

```java
// ANTES (original)
package com.banco.cuadre;
import com.banco.cuadre.LectorExcel;
import com.banco.cuadre.ValidadorCruce;

// DESPUES (tu proyecto)
package com.tupackage.cuadre;
import com.tupackage.cuadre.LectorExcel;
import com.tupackage.cuadre.ValidadorCruce;
```

---

## 8. CHECKLIST DE MIGRACION

```
[ ] Copiar LectorExcel.java
[ ] Copiar ValidadorCruce.java  
[ ] Copiar EscritorExcel.java
[ ] Copiar CuadreApp.java (si necesitas GUI)
[ ] Crear carpeta src/main/resources/
[ ] Crear logback.xml
[ ] Agregar dependencias POI y SLF4J al pom.xml
[ ] Actualizar package en cada clase
[ ] Actualizar imports en tu codigo
[ ] Ejecutar mvn clean compile
[ ] Probar con mvn exec:java o ejecutando main class
```

---

## 9. RESUMEN RAPIDO

| Paso | Accion |
|------|--------|
| 1 | Copia 4 archivos .java |
| 2 | Crea `src/main/resources/logback.xml` |
| 3 | Agrega dependencias al pom.xml |
| 4 | Actualiza package e imports |
| 5 | `mvn clean compile` |
| 6 | `mvn clean package` |
| 7 | `java -jar target/tu-proyecto-1.0.0.jar` |

---

**Version**: 1.0.0
