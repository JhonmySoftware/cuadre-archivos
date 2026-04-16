@echo off
title Cuadre de Archivos - Banco Davivienda
color 0C

echo ==========================================
echo   BANCO DAVIVIENDA
echo   Cuadre de Archivos
echo ==========================================
echo.

java -jar "%~dp0cuadre-archivos-1.0.jar"

if errorlevel 1 (
    echo.
    echo [ERROR] No se pudo ejecutar la aplicacion.
    echo Verifique que Java este instalado correctamente.
    pause
)
