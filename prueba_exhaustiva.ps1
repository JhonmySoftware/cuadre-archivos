$e = New-Object -ComObject Excel.Application
$e.Visible = $false

Write-Host "=== CARGANDO DATOS ==="

$txtPath = 'C:\Users\fabio\Downloads\Cuadre 07_04_2026\Credibanco_P2M_Breb_2_20260413.txt'
$lineas = Get-Content $txtPath
$header = $lineas[0] -split ","
$data = $lineas[1] -split ","

Write-Host "Cuentas en TXT:"
$cTarjeta = $data[3]
$cOrigen = $data[22]
Write-Host "  numero_cuenta_tarjeta: $cTarjeta"
Write-Host "  numero_cuenta_origen: $cOrigen"

$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026\Cuadre 07_04_2026.xlsx')

Write-Host ""
Write-Host "=== BUSCANDO TODAS LAS CUENTAS TXT EN TODAS LAS HOJAS ==="

$cuentasTxt = @()
$cuentasTxt += [long]($cTarjeta)
$cuentasTxt += [long]($cOrigen)

foreach ($hoja in $wb.Sheets) {
    Write-Host ""
    Write-Host "=== $($hoja.Name) ==="
    $maxRow = $hoja.UsedRange.Rows.Count
    $maxCol = $hoja.UsedRange.Columns.Count
    
    $resultados = @()
    
    for ($col = 1; $col -le $maxCol; $col++) {
        for ($row = 2; $row -le $maxRow; $row++) {
            $val = $hoja.Cells.Item($row, $col).Text.Trim()
            if ($val -and $val.Length -gt 5) {
                foreach ($cta in $cuentasTxt) {
                    $valClean = $val -replace '^0+', ''
                    try {
                        if ([long]$valClean -eq $cta) {
                            $h = $hoja.Cells.Item(1, $col).Text.Trim()
                            $resultados += "Fila $row Col $col [$h]: $val"
                        }
                    } catch {}
                }
            }
        }
    }
    
    if ($resultados.Count -gt 0) {
        Write-Host "ENCONTRADOS ($($resultados.Count)):"
        $resultados | ForEach-Object { Write-Host "  $_" }
    } else {
        Write-Host "NO hay coincidencias"
    }
}

$wb.Close($false)
$e.Quit()
