$e = New-Object -ComObject Excel.Application
$e.Visible = $false
$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026\Cuadre 07_04_2026.xlsx')

Write-Host "=== BUSCANDO cuenta del TXT en el Excel ==="
Write-Host "Cuenta a buscar: 560266060000327"
Write-Host ""

$buscar = "560266060000327"

foreach ($hoja in $wb.Sheets) {
    Write-Host "=== Hoja: $($hoja.Name) ==="
    $encontrado = $false
    
    $usedRange = $hoja.UsedRange
    $lastRow = $usedRange.Row + $usedRange.Rows.Count - 1
    $lastCol = $usedRange.Column + $usedRange.Columns.Count - 1
    
    for ($fila = 1; $fila -le [Math]::Min($lastRow, 100); $fila++) {
        for ($col = 1; $col -le [Math]::Min($lastCol, 20); $col++) {
            $valor = $hoja.Cells.Item($fila, $col).Text.Trim()
            if ($valor -replace '^0+', '' -eq $buscar) {
                Write-Host "  ENCONTRADO en Fila $fila, Col $col : '$valor'"
                $encontrado = $true
            }
        }
    }
    
    if (-not $encontrado) {
        Write-Host "  NO encontrado en las primeras 100 filas"
    }
    Write-Host ""
}

$wb.Close($false)
$e.Quit()
