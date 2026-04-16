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

## 5. POM.XML COMPLETO (PARA GENERAR JAR EJECUTABLE)

Este pom.xml genera un JAR con TODAS las dependencias incluidas (uber-jar).

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
        
        <!-- Logback (necesario para logging) -->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.2.12</version>
        </dependency>
    </dependencies>
    
    <build>
        <finalName>${project.artifactId}-${project.version}</finalName>
        
        <plugins>
            <!-- Plugin compilador -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            
            <!-- SHADE PLUGIN - Genera JAR con todas las dependencias -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.2.4</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.tupackage.cuadre.CuadreApp</mainClass>
                                </transformer>
                            </transformers>
                            <filters>
                                <filter>
                                    <artifact>*:*</artifact>
                                    <excludes>
                                        <exclude>META-INF/*.SF</exclude>
                                        <exclude>META-INF/*.DSA</exclude>
                                        <exclude>META-INF/*.RSA</exclude>
                                    </excludes>
                                </filter>
                            </filters>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

**Importante**: Cambia `<mainClass>com.tupackage.cuadre.CuadreApp</mainClass>` por la clase principal de tu proyecto.

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

### Crear JAR con dependencias (uber-jar)
```bash
mvn clean package
```

Esto genera: `target/tu-proyecto-1.0.0.jar`

### Ejecutar la aplicacion (desde target/)
```bash
java -jar target/tu-proyecto-1.0.0.jar
```

---

## 7. GENERAR EL JAR - PASO A PASO

### 7.1 Desde linea de comandos

```bash
# 1. Navega a tu proyecto
cd C:\ruta\tu-proyecto

# 2. Compila y genera JAR
mvn clean package

# 3. Verifica que se creo
dir target\*.jar

# 4. Ejecuta
java -jar target\tu-proyecto-1.0.0.jar
```

### 7.2 Desde IntelliJ IDEA

1. Ve a `File > Project Structure > Artifacts`
2. Click en `+ > JAR > From modules with dependencies`
3. Selecciona la clase main
4. Build > Build Artifacts > Build

### 7.3 Desde Eclipse

1. Click derecho en proyecto > Export
2. Java > Runnable JAR file
3. Selecciona la clase main
4. Finish

### 7.4 Crear script de ejecucion (.bat)

```batch
@echo off
title Tu Proyecto
java -jar tu-proyecto-1.0.0.jar
pause
```

---

## 8. ACTUALIZAR IMPORTACIONES

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

## 9. SI QUIERES SOLO UN JAR SIMPLE (sin shade)

Si no quieres el uber-jar, usa este pom.xml simplificado:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.tupackage</groupId>
    <artifactId>tu-proyecto</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.2.5</version>
        </dependency>
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
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.tupackage.cuadre.CuadreApp</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

Luego ejecuta:
```bash
mvn clean package
java -cp target/tu-proyecto-1.0.0.jar;target/lib/* com.tupackage.cuadre.CuadreApp
```

---

## 10. CHECKLIST DE MIGRACION

```
[ ] Copiar LectorExcel.java
[ ] Copiar ValidadorCruce.java  
[ ] Copiar EscritorExcel.java
[ ] Copiar CuadreApp.java (si necesitas GUI)
[ ] Crear carpeta src/main/resources/
[ ] Crear logback.xml
[ ] Crear pom.xml con shade plugin
[ ] Agregar dependencias POI, SLF4J y Logback
[ ] Actualizar package en cada clase
[ ] Actualizar mainClass en pom.xml
[ ] mvn clean compile
[ ] mvn clean package
[ ] Probar: java -jar target/tu-proyecto-1.0.0.jar
```

---

## 11. RESUMEN RAPIDO

| Paso | Accion | Comando |
|------|--------|---------|
| 1 | Copiar archivos .java | - |
| 2 | Crear logback.xml | - |
| 3 | Crear pom.xml | - |
| 4 | Actualizar mainClass | - |
| 5 | Compilar | `mvn clean compile` |
| 6 | Generar JAR | `mvn clean package` |
| 7 | Ejecutar | `java -jar target/tu-proyecto-1.0.0.jar` |

---

**Version**: 1.0.0
