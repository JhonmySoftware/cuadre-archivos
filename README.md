# Cuadre de Archivos

![Java](https://img.shields.io/badge/Java-1.8+-ED8B00?style=flat-square&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36?style=flat-square&logo=apache-maven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

Aplicacion de escritorio para validacion y cruce de archivos Excel en el area funcional de Banco Davivienda.

## Proposito

Verificar que la informacion contenida en un archivo Excel (Origen) haya sido correctamente transferida a otro archivo Excel (Destino), generando un reporte detallado con estadisticas y diferencias.

## Caracteristicas

- **GUI Swing** intuitiva y responsive
- **Relaciones configurables** (Primary Key + Keys adicionales)
- **Modos de cruce**: TODAS (AND) / CUALQUIERA (OR)
- **Normalizacion automatica** de datos (ceros a la izquierda, mayusculas/minusculas)
- **Reportes en Excel** con 4 hojas:
  - Resumen de estadisticas
  - Todos los registros
  - Registros verificados
  - Registros no encontrados
- **Estilos Davivienda** en el Excel de salida
- **Compatible** con Java 8+

## Requisitos

- Java JDK 8 o superior
- Maven 3.6+ (para compilacion)
- Windows 7+/Linux/macOS

## Instalacion y Uso

### Compilar desde codigo fuente

```bash
# Clonar el repositorio
git clone https://github.com/TU_USUARIO/cuadre-archivos.git
cd cuadre-archivos

# Compilar y empaquetar
mvn clean package

# Ejecutar
java -jar target/cuadre-archivos-1.0.jar
```

### Usar el JAR directamente

```bash
java -jar cuadre-archivos-1.0.jar
```

### Con mas memoria (archivos grandes)

```bash
java -Xmx2g -jar cuadre-archivos-1.0.jar
```

## Estructura del Proyecto

```
cuadre-archivos/
├── pom.xml                          # Configuracion Maven
├── src/main/java/com/banco/cuadre/
│   ├── CuadreApp.java              # Interfaz grafica (Swing)
│   ├── LectorExcel.java           # Lectura de archivos Excel
│   ├── ValidadorCruce.java        # Logica de cruce de datos
│   └── EscritorExcel.java         # Generacion de reportes Excel
├── src/main/resources/
│   └── logback.xml                # Configuracion de logs
├── src/test/java/                  # Pruebas unitarias
├── DOCUMENTACION_TECNICA.md        # Documentacion tecnica
├── DOCUMENTACION_FUNCIONAL.md     # Manual de usuario
└── LICENSE
```

## Configuracion

El archivo `pom.xml` contiene todas las dependencias:

| Dependencia | Version | Proposito |
|-------------|---------|-----------|
| Apache POI | 5.2.5 | Lectura/escritura Excel |
| SLF4J | 2.0.11 | Logging |
| Logback | 1.4.14 | Implementacion de logging |

## Documentacion

- [Manual de Usuario](DOCUMENTACION_FUNCIONAL.md)
- [Documentacion Tecnica](DOCUMENTACION_TECNICA.md)

## Contribuir

1. Haz fork del proyecto
2. Crea una rama (`git checkout -b feature/nueva-funcion`)
3. Commitea tus cambios (`git commit -m 'Agrega nueva funcion'`)
4. Haz push a la rama (`git push origin feature/nueva-funcion`)
5. Abre un Pull Request

## Licencia

Este proyecto esta bajo la licencia MIT. Ver [LICENSE](LICENSE) para mas detalles.

## Autor

Desarrollado para **Banco Davivienda**

---

*Si te es util, dale una estrella ⭐*
