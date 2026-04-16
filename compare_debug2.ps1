$e = New-Object -ComObject Excel.Application
$e.Visible = $false
$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026.xlsx')

Write-Host "=== COMPARACION DIRECTA ==="

$wsDivide = $wb.Sheets.Item('Divide')
$wsEstructura = $wb.Sheets.Item('Divide estructura')

Write-Host ""
Write-Host "=== Divide - Fila 2 (datos clave) ==="
Write-Host "  numero-cuenta-origen (Col J=10): " $wsDivide.Cells.Item(2, 10).Text
Write-Host "  tipo-id-origen (Col A=1): " $wsDivide.Cells.Item(2, 1).Text
Write-Host "  num-id-origen (Col B=2): " $wsDivide.Cells.Item(2, 2).Text
Write-Host "  fecha-transaccion (Col E=5): " $wsDivide.Cells.Item(2, 5).Text

Write-Host ""
Write-Host "=== Divide estructura - Fila 2 (datos clave) ==="
Write-Host "  NUMERO CUENTA - TARJETA (Col D=4): " $wsEstructura.Cells.Item(2, 4).Text
Write-Host "  TIPO-REGISTRO (Col A=1): " $wsEstructura.Cells.Item(2, 1).Text
Write-Host "  RED-LOGICA AUTORIZADOR (Col B=2): " $wsEstructura.Cells.Item(2, 2).Text
Write-Host "  FECHA-TRANSACCION (Col L=12): " $wsEstructura.Cells.Item(2, 12).Text

Write-Host ""
Write-Host "=== Comparando cuentas (sin ceros iniciales) ==="
$cuenta1 = $wsDivide.Cells.Item(2, 10).Text.Trim()
$cuenta2 = $wsEstructura.Cells.Item(2, 4).Text.Trim()
$cuenta1Clean = $cuenta1 -replace '^0+', ''
$cuenta2Clean = $cuenta2 -replace '^0+', ''
Write-Host "  Divide cuenta: '$cuenta1' -> '$cuenta1Clean'"
Write-Host "  Estructura cuenta: '$cuenta2' -> '$cuenta2Clean'"
Write-Host "  Son iguales (sin ceros): $($cuenta1Clean -eq $cuenta2Clean)"

Write-Host ""
Write-Host "=== Buscando cuenta del Divide en Estructura (sin ceros) ==="
$encontrado = $false
$maxFilas = 20
for ($fila = 2; $fila -le $maxFilas; $fila++) {
    $cuenta = $wsEstructura.Cells.Item($fila, 4).Text.Trim()
    if ($cuenta) {
        $cuentaLimpia = $cuenta -replace '^0+', ''
        if ($cuentaLimpia -eq $cuenta1Clean) {
            Write-Host "  ENCONTRADO en fila $fila : '$cuenta'"
            $encontrado = $true
        }
    }
}
if (-not $encontrado) {
    Write-Host "  NO encontrado en las primeras $maxFilas filas"
}

Write-Host ""
Write-Host "=== Muestras de cuentas en Divide (filas 2-5) ==="
for ($fila = 2; $fila -le 5; $fila++) {
    $c = $wsDivide.Cells.Item($fila, 10).Text.Trim()
    $cClean = $c -replace '^0+', ''
    Write-Host "  Fila $fila : '$c' -> '$cClean'"
}

Write-Host ""
Write-Host "=== Muestras de cuentas en Estructura (filas 2-5) ==="
for ($fila = 2; $fila -le 5; $fila++) {
    $c = $wsEstructura.Cells.Item($fila, 4).Text.Trim()
    $cClean = $c -replace '^0+', ''
    Write-Host "  Fila $fila : '$c' -> '$cClean'"
}

$wb.Close($false)
$e.Quit()
