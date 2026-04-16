@echo off
REM ==========================================
REM Cuadre de Archivos - Script de Build
REM ==========================================

echo.
echo ==========================================
echo   BANCO DAVIVIENDA
echo   Cuadre de Archivos - Build Script
echo ==========================================
echo.

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java no esta instalado o no esta en PATH
    pause
    exit /b 1
)

REM Verificar Maven
mvn --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven no esta instalado o no esta en PATH
    echo Descargue Maven desde: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo [INFO] Compilando proyecto...
echo.

REM Compilar
call mvn clean compile
if errorlevel 1 (
    echo.
    echo [ERROR] Error en compilacion
    pause
    exit /b 1
)

echo.
echo [OK] Compilacion exitosa
echo.

REM Preguntar si desea empaquetar
set /p empacar="Desea generar el JAR ejecutable? (S/N): "
if /i "%empacar%"=="S" (
    echo.
    echo [INFO] Generando JAR...
    call mvn package -DskipTests
    if errorlevel 1 (
        echo.
        echo [ERROR] Error al generar JAR
        pause
        exit /b 1
    )
    echo.
    echo [OK] JAR generado en: target\cuadre-archivos-1.0.jar
)

echo.
echo ==========================================
echo   Build completado
echo ==========================================
pause
