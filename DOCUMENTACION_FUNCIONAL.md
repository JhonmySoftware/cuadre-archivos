# CUADRE DE ARCHIVOS - MANUAL DE USUARIO
## Banco Davivienda

---

## 1. INTRODUCCION

La aplicacion **Cuadre de Archivos** permite validar que la informacion contenida en un archivo Excel (Archivo A - Origen) haya sido correctamente transferida a otro archivo Excel (Archivo B - Destino).

### 1.1 Proposito

- Verificar la integridad de datos entre sistemas
- Identificar registros faltantes o inconsistentes
- Generar reportes para analisis del equipo funcional

---

## 2. PRIMEROS PASOS

### 2.1 Requisitos

- Computadora con Windows 7, 8, 10 o 11
- Java Runtime Environment (JRE) 8 o superior
- Archivos Excel en formato .xlsx

### 2.2 Instalacion

1. Copie los archivos `cuadre-archivos-1.0.jar` y `cuadre.bat` a una carpeta
2. Haga doble clic en `cuadre.bat` para ejecutar
3. La aplicacion se abrira maximizada

---

## 3. INTERFAZ DE USUARIO

### 3.1 Pantalla Principal

```
+----------------------------------------------------------+
|  [BANDA COLORES DAVIVIENDA]                              |
|  BANCO DAVIVIENDA                                         |
|  Cuadre de Archivos                                       |
+----------------------------------------------------------+
|                                                           |
|  +---------------------+  +---------------------+          |
|  | ARCHIVO A (ORIGEN) |  | ARCHIVO B (DESTINO)|          |
|  | [Seleccionar]      |  | [Seleccionar]      |          |
|  | Archivo: ---       |  | Archivo: ---       |          |
|  | Hoja: ---          |  | Hoja: ---          |          |
|  +---------------------+  +---------------------+          |
|                                                           |
|  +------------------------------------------------------+ |
|  | RELACIONES DE COLUMNAS           Modo: [TODAS] [O]  | |
|  | PK  PRIMARY KEY  A: [columna] > B: [columna]      | |
|  | K1  KEY          A: [columna] > B: [columna]      | |
|  | + PK  + Key  + Auto-detectar                        | |
|  +------------------------------------------------------+ |
|                                                           |
|  [INSTRUCCIONES]                          [EJECUTAR]     |
|                                                           |
+----------------------------------------------------------+
|  [===================== 100% ======================]       |
|  | Log de procesamiento...                              | |
+----------------------------------------------------------+
```

---

## 4. COMO USAR LA APLICACION

### Paso 1: Seleccionar Archivos

1. Haga clic en **"Seleccionar archivo Excel"** en el panel **Archivo A (Origen)**
2. Busque y seleccione el archivo Excel de origen
3. Seleccione la hoja que contiene los datos
4. Repita los pasos 1-3 para el **Archivo B (Destino)**

### Paso 2: Definir Relaciones

Las relaciones indican como se comparan las columnas entre archivos.

**Conceptos importantes:**

| Concepto | Descripcion |
|----------|-------------|
| **PK (Primary Key)** | Identificador unico - columna principal para el cruce |
| **KEY** | Campo adicional de comparacion |
| **TODAS (Y)** | Todas las relaciones deben coincidir |
| **CUALQUIERA (O)** | Al menos una relacion debe coincidir |

**Agregar Primary Key:**
- Haga clic en **"+ PK"**
- Seleccione la columna del Archivo A
- Seleccione la columna correspondiente del Archivo B

**Agregar Key adicional:**
- Haga clic en **"+ Key"**
- Configure las columnas como en el paso anterior

**Auto-detectar:**
- Haga clic en **"Auto-detectar"**
- El sistema buscara columnas con nombres similares

### Paso 3: Ejecutar el Cruce

1. Verifique que todas las relaciones sean correctas
2. Seleccione el modo de coincidencia:
   - **TODAS (Y)**: Requiere que todas las columnas coincidan
   - **CUALQUIERA (O)**: Basta con que una columna coincida
3. Haga clic en **"Ejecutar Cruce"**
4. Elija donde guardar el archivo de resultado
5. Espere a que finalice el procesamiento

### Paso 4: Revisar Resultados

El archivo Excel generado contiene 4 hojas:

| Hoja | Contenido |
|------|-----------|
| **1-Resumen** | Estadisticas generales y relaciones usadas |
| **2-Todos** | Todos los registros con estado |
| **3-Verificados** | Solo registros encontrados |
| **4-No Encontrados** | Solo registros sin coincidencia |

---

## 5. INTERPRETACION DE RESULTADOS

### 5.1 Hoja 1: Resumen

Muestra:
- Estadisticas generales del cruce
- Total de registros en cada archivo
- Numero de encontrados y no encontrados
- Porcentaje de efectividad
- Lista de relaciones de columnas utilizadas

### 5.2 Columnas de Estado

| Estado | Significado |
|--------|-------------|
| **ENCONTRADO** | El registro tiene coincidencia en el Archivo B |
| **NO ENCONTRADO** | El registro NO tiene coincidencia |

### 5.3 Columna CRUCE_CLAVE

Muestra el valor de la clave de cruce (normalizado sin ceros a la izquierda).

### 5.4 Columna COINCIDENCIAS_EN_B

Indica cuantas veces aparece el valor en el Archivo B.

---

## 6. RESALTADO VISUAL

### 6.1 Headers de Columnas

| Elemento | Color |
|----------|-------|
| Headers normales | Fondo rojo, texto blanco |
| Primary Key | Fondo rosa, texto rojo negrita |

### 6.2 Estados

| Estado | Color |
|--------|-------|
| ENCONTRADO | Texto verde negrita |
| NO ENCONTRADO | Texto rojo negrita |

---

## 7. EJEMPLO PRACTICO

### Situacion

Tiene un archivo de transacciones del dia anterior (Origen) y necesita verificar que todas llegaron correctamente a la base de datos (Destino).

### Solucion

1. Seleccione archivo de transacciones como **Archivo A**
2. Seleccione export de la base de datos como **Archivo B**
3. Agregue una **PK** usando la columna `numero_referencia`
4. Ejecute el cruce en modo **TODAS**
5. Revise la hoja **"4-No Encontrados"** para ver que transacciones faltan

---

## 8. PREGUNTAS FRECUENTES

### Q: Que pasa si no selecciono ninguna PK?
R: Debe agregar al menos una relacion. La PK es obligatoria para identificar los registros.

### Q: Cual es la diferencia entre TODAS y CUALQUIERA?
R: 
- **TODAS**: Solo marca como encontrado si TODAS las columnas coinciden
- **CUALQUIERA**: Marca como encontrado si AL MENOS UNA columna coincide

### Q: Puedo agregar varias Keys?
R: Si, puede agregar tantas como necesite para su validacion.

### Q: Que son los ceros a la izquierda?
R: La aplicacion normaliza automaticamente. "00123" y "123" se consideran iguales.

### Q: Cual es el tamano maximo de archivo?
R: 500 MB por archivo. Para archivos mayores, divida en partes.

---

## 9. LIMITES TECNICOS

| Recurso | Limite |
|---------|--------|
| Tamano archivo | 500 MB |
| Filas por hoja | 1,000,000 |
| Columnas por hoja | 500 |
| Caracteres por celda | 32,767 |

---

## 10. CONTACTO

Para soporte tecnico o reporte de problemas, contacte al equipo de desarrollo.

**Version**: 1.0.0  
**Desarrollado para**: Banco Davivienda  
**Fecha**: 2026
