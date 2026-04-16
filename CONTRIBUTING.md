# Como Contribuir

¡Gracias por tu interes en contribuir!

## Proceso de Desarrollo

### 1. Configuracion del Entorno

```bash
# Requisitos
- Java JDK 8+
- Maven 3.6+
- Git

# Clonar repositorio
git clone https://github.com/TU_USUARIO/cuadre-archivos.git
cd cuadre-archivos

# Compilar proyecto
mvn clean compile
```

### 2. Reglas de Codificacion

- **Convenciones**: Sigue el estilo de codificacion de Java estandar
- **Nomenclatura**:
  - Clases: `PascalCase` (ej: `CuadreApp`)
  - Metodos: `camelCase` (ej: `ejecutarCruce`)
  - Constantes: `MAYUSCULAS_UNDERSCORE` (ej: `DAVI_ROJO`)
- **Documentacion**: Usa Javadoc para todas las clases y metodos publicos
- **Lineas**: Maximo 120 caracteres por linea

### 3. Ramas (Branches)

| Rama | Proposito |
|------|-----------|
| `main` | Codigo estable, listo para produccion |
| `develop` | Desarrollo de nuevas funcionalidades |
| `feature/*` | Nuevas funcionalidades |
| `bugfix/*` | Correccion de errores |
| `hotfix/*` | Correcciones urgentes en produccion |

### 4. Commits

Usa mensajes de commit descriptivos:

```
tipo: descripcion corta

descripcion larga (opcional)
```

**Tipos:**
- `feat`: Nueva funcionalidad
- `fix`: Correccion de error
- `docs`: Documentacion
- `style`: Formato, estilos (sin cambio de logica)
- `refactor`: Refactorizacion de codigo
- `test`: Agregar o modificar pruebas
- `chore`: Tareas de mantenimiento

**Ejemplos:**
```
feat: agregar modo de cruce OR

Permite al usuario seleccionar si quiere que al menos
una columna coincida en lugar de todas.

fix: corregir normalizacion de ceros izquierda

El numero "000123" ahora se normaliza correctamente a "123"
en lugar de mantener los ceros.

docs: actualizar README con nuevas instrucciones
```

### 5. Pull Requests

1. Asegurate de que tu codigo compila: `mvn clean compile`
2. Ejecuta las pruebas: `mvn test`
3. Verifica el analisis estatico si esta configurado
4. Llena la plantilla de PR con:
   - Descripcion del cambio
   - Issue relacionado (si aplica)
   - Pasos para probar
   - Screenshots (si es cambio de UI)

### 6. Pruebas

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar con cobertura
mvn test jacoco:report
```

### 7. Build y Release

```bash
# Build sin pruebas
mvn clean package -DskipTests

# Release (requiere configuracion de gpg para firmar)
mvn release:prepare
mvn release:perform
```

## Recursos

- [Guia de estilo Java](https://google.github.io/styleguide/javaguide.html)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Maven en 5 minutos](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

## Preguntas

Si tienes dudas, abre un issue con la etiqueta `question`.
