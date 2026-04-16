$e = New-Object -ComObject Excel.Application
$e.Visible = $false
$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026.xlsx')

Write-Host "=== COMPARACION DIRECTA ==="
Write-Host ""

$wsDivide = $wb.Sheets.Item('Divide')
$wsEstructura = $wb.Sheets.Item('Divide estructura')

Write-Host "=== Divide - Fila 2 (primeros 15 datos) ==="
$data2 = $wsDivide.Range('A2:Z2').Value2
$i = 0
foreach ($cell in $data2) {
    $i++
    if ($cell) { Write-Host "  Col $i : $cell" }
}

Write-Host ""
Write-Host "=== Divide estructura - Fila 2 (primeros 15 datos) ==="
$data3 = $wsEstructura.Range('A2:Z2').Value2
$i = 0
foreach ($cell in $data3) {
    $i++
    if ($cell) { Write-Host "  Col $i : $cell" }
}

Write-Host ""
Write-Host "=== Buscando cuenta en Divide (numero-cuenta-origen = columna J = indice 10) ==="
$cuentaDivide = $wsDivide.Cells.Item(2, 10).Value
Write-Host "  Cuenta Divide: '$cuentaDivide'"

Write-Host ""
Write-Host "=== Buscando cuenta en Divide estructura (NUMERO CUENTA - TARJETA = columna D = indice 4) ==="
$cuentaEstructura = $wsEstructura.Cells.Item(2, 4).Value
Write-Host "  Cuenta Estructura: '$cuentaEstructura'"

Write-Host ""
Write-Host "=== Comparando (sin ceros) ==="
$cuenta1Clean = $cuentaDivide -replace '^0+', ''
$cuenta2Clean = $cuentaEstructura -replace '^0+', ''
Write-Host "  Divide limpio: '$cuenta1Clean'"
Write-Host "  Estructura limpio: '$cuenta2Clean'"
Write-Host "  Son iguales: $($cuenta1Clean -eq $cuenta2Clean)"

Write-Host ""
Write-Host "=== Buscando cuenta del Divide en TODAS las filas de Estructura ==="
$encontrado = $false
for ($fila = 2; $fila -le 10; $fila++) {
    $cuenta = $wsEstructura.Cells.Item($fila, 4).Value
    if ($cuenta) {
        $cuentaLimpia = [string]$cuenta | ForEach-Object { $_ -replace '^0+', '' }
        $cuentaLimpia = $cuentaLimpia.Trim()
        if ($cuentaLimpia -eq $cuenta1Clean) {
            Write-Host "  ENCONTRADO en fila $fila : '$cuenta'"
            $encontrado = $true
        }
    }
}
if (-not $encontrado) {
    Write-Host "  NO encontrado en las primeras 10 filas"
}

$wb.Close($false)
$e.Quit()
