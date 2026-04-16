@echo off
cd /d "%~dp0"
title Cuadre Archivos - Banco Davivienda
color 0B

echo ================================================
echo    CUADRE DE ARCHIVOS - Banco Davivienda
echo ================================================
echo.

java -version
echo.

echo Iniciando aplicacion...
echo.

java -jar bin\CuadreArchivos.jar

if errorlevel 1 (
    echo.
    echo ERROR: No se pudo ejecutar la aplicacion.
    echo Asegurese de tener Java instalado.
    pause
)
