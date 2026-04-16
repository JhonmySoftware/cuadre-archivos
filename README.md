# File Comparator - Cuadre de Archivos

![Java](https://img.shields.io/badge/Java-1.8+-ED8B00?style=flat-square&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36?style=flat-square&logo=apache-maven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

Aplicacion de escritorio para validacion y cruce de archivos Excel. Compara datos entre dos archivos y genera reportes detallados con estadisticas de coincidencias.

## Que puedes hacer con esta aplicacion?

### Casos de uso principales:

| Caso | Descripcion |
|------|-------------|
| **Validacion de migraciones** | Verificar que los datos迁移 correctamente entre sistemas |
| **Auditoria de datos** | Comparar exportaciones de bases de datos |
| **Control de calidad** | Detectar inconsistencias en archivos de datos |
| **Reconciliacion** | Cruzar archivos de diferentes fuentes |
| **Generacion de reportes** | Producir informes ejecutivos para analisis |

### Caracteristicas avanzadas:

- **Cruce por multiples columnas** - Usa una o varias columnas como clave
- **Modo AND/OR** - Todas las claves coinciden O al menos una
- **Normalizacion automatica** - Elimina ceros a la izquierda, normaliza texto
- **Archivos grandes** - Soporta hasta 500 MB por archivo
- **Multi-hoja** - Procesa cualquier hoja del Excel
- **Reportes estilizados** - Excel con formatos profesionales

## Caracteristicas tecnicas

- **GUI Swing** intuitiva y responsive
- **Relaciones configurables** (Primary Key + Keys adicionales)
- **Modos de cruce**: TODAS (AND) / CUALQUIERA (OR)
- **Normalizacion automatica** de datos
- **Reportes en Excel** con 4 hojas:
  - Resumen de estadisticas
  - Todos los registros
  - Registros verificados
  - Registros no encontrados
- **Compatible** con Java 8+

## Requisitos

- Java JDK 8 o superior
- Maven 3.6+ (para compilacion)
- Windows 7+/Linux/macOS

## Instalacion y Uso

### Compilar desde codigo fuente

```bash
# Clonar el repositorio
git clone https://github.com/JhonmySoftware/cuadre-archivos.git
cd cuadre-archivos

# Compilar y empaquetar
mvn clean package

# Ejecutar
java -jar target/cuadre-archivos-1.0.0.jar
```

### Usar el JAR directamente

Descarga el release y ejecuta:
```bash
java -jar cuadre-archivos-1.0.0.jar
```

### Con mas memoria (archivos grandes)

```bash
java -Xmx2g -jar cuadre-archivos-1.0.0.jar
```

## Estructura del Proyecto

```
cuadre-archivos/
├── pom.xml                          # Configuracion Maven
├── src/main/java/com/banco/cuadre/
│   ├── CuadreApp.java              # Interfaz grafica (Swing)
│   ├── LectorExcel.java            # Lectura de archivos Excel
│   ├── ValidadorCruce.java         # Logica de cruce de datos
│   ├── EscritorExcel.java          # Generacion de reportes Excel
│   └── EjemplosUso.java            # Ejemplos de integracion
├── src/main/resources/
│   └── logback.xml                 # Configuracion de logs
├── src/test/java/                  # Pruebas unitarias
├── GUIA_INTEGRACION.md             # Guia para desarrolladores
├── DOCUMENTACION_TECNICA.md        # Documentacion tecnica
├── DOCUMENTACION_FUNCIONAL.md      # Manual de usuario
└── LICENSE
```

## Integracion en otros proyectos

Esta aplicacion puede integrarse como libreria en otros proyectos Java. Ver [GUIA_INTEGRACION.md](GUIA_INTEGRACION.md) para ejemplos detallados.

### Uso basico:

```java
// Leer archivos
LectorExcel lector = new LectorExcel();
LectorExcel.InfoArchivo archivoA = lector.leerArchivo("origen.xlsx");
LectorExcel.InfoArchivo archivoB = lector.leerArchivo("destino.xlsx");

// Configurar cruce
List<String> colsA = Arrays.asList("id_cliente", "fecha");
List<String> colsB = Arrays.asList("cliente_id", "fec_mov");

// Ejecutar
ValidadorCruce validador = new ValidadorCruce();
Map<String, Object> resultado = validador.generarResultadoConModo(
    archivoA.hojas.get(0).datos, colsA,
    archivoB.hojas.get(0).datos, colsB,
    null, false
);

// Generar reporte
EscritorExcel escritor = new EscritorExcel();
escritor.guardarResultadoNuevo(resultado, "resultado.xlsx", null);
```

## Documentacion

- [Guia de Integracion](GUIA_INTEGRACION.md) - Como usar en codigo
- [Manual de Usuario](DOCUMENTACION_FUNCIONAL.md) - Guia de la interfaz
- [Documentacion Tecnica](DOCUMENTACION_TECNICA.md) - Referencia completa

## Contribuir

1. Haz fork del proyecto
2. Crea una rama (`git checkout -b feature/nueva-funcion`)
3. Commitea tus cambios (`git commit -m 'Agrega nueva funcion'`)
4. Haz push a la rama (`git push origin feature/nueva-funcion`)
5. Abre un Pull Request

## Licencia

Este proyecto esta bajo la licencia MIT. Ver [LICENSE](LICENSE) para mas detalles.

---

*Si te es util, dale una estrella*
