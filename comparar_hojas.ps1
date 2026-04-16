$e = New-Object -ComObject Excel.Application
$e.Visible = $false

$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026\Cuadre 07_04_2026.xlsx')

Write-Host "=== Divide vs Divide estructura - Comparando cuentas ==="
Write-Host ""

$divide = $wb.Sheets.Item("Divide")
$estructura = $wb.Sheets.Item("Divide estructura")

Write-Host "Cuentas en Divide (numero-cuenta-origen = Col J = 10):"
$cuentasDivide = @{}
for ($r = 2; $r -le 100; $r++) {
    $v = $divide.Cells.Item($r, 10).Text.Trim()
    if ($v -and $v.Length -gt 5) {
        $vClean = [string]([long]($v -replace '^0+', ''))
        if (-not $cuentasDivide.ContainsKey($vClean)) {
            $cuentasDivide[$vClean] = $v
        }
    }
}

Write-Host "Total cuentas unicas en Divide: $($cuentasDivide.Count)"
$c = 0
foreach ($key in $cuentasDivide.Keys) {
    Write-Host "  $key (original: $($cuentasDivide[$key]))"
    $c++
    if ($c -ge 10) { Write-Host "  ... (mas)"; break }
}

Write-Host ""
Write-Host "Cuentas en Divide estructura (NUMERO CUENTA - TARJETA = Col D = 4):"
$cuentasEst = @{}
for ($r = 2; $r -le 88; $r++) {
    $v = $estructura.Cells.Item($r, 4).Text.Trim()
    if ($v -and $v.Length -gt 5) {
        $vClean = [string]([long]($v -replace '^0+', ''))
        if (-not $cuentasEst.ContainsKey($vClean)) {
            $cuentasEst[$vClean] = $v
        }
    }
}

Write-Host "Total cuentas unicas en Estructura: $($cuentasEst.Count)"
$c = 0
foreach ($key in $cuentasEst.Keys) {
    Write-Host "  $key (original: $($cuentasEst[$key]))"
    $c++
    if ($c -ge 10) { Write-Host "  ... (mas)"; break }
}

Write-Host ""
Write-Host "=== COINCIDENCIAS ENCONTRADAS ==="
$coincidencias = 0
foreach ($cta in $cuentasDivide.Keys) {
    if ($cuentasEst.ContainsKey($cta)) {
        Write-Host "CUENTA: $cta"
        Write-Host "  Divide: $($cuentasDivide[$cta])"
        Write-Host "  Estructura: $($cuentasEst[$cta])"
        $coincidencias++
    }
}

Write-Host ""
Write-Host "Total coincidencias: $coincidencias"

$wb.Close($false)
$e.Quit()
