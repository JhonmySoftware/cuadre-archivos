$e = New-Object -ComObject Excel.Application
$e.Visible = $false
$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026\Cuadre 07_04_2026.xlsx')

Write-Host "=== BUSQUEDA EN HOJAS - Columnas 1-30 ==="
$buscar = [long]"560266060000327"

foreach ($hoja in $wb.Sheets) {
    Write-Host ""
    Write-Host "=== $($hoja.Name) ==="
    $maxRow = $hoja.UsedRange.Rows.Count
    Write-Host "Total filas: $maxRow"
    
    $encontrado = $false
    $maxCheck = [Math]::Min($maxRow, 1000)
    
    for ($col = 1; $col -le 30; $col++) {
        $header = $hoja.Cells.Item(1, $col).Text.Trim()
        
        for ($row = 2; $row -le $maxCheck; $row++) {
            $val = $hoja.Cells.Item($row, $col).Text.Trim()
            if ($val) {
                $valClean = $val -replace '^0+', ''
                try {
                    if ([long]$valClean -eq $buscar) {
                        Write-Host "  ENCONTRADO! Fila $row, Col $col ($header): '$val'"
                        $encontrado = $true
                        break
                    }
                } catch {}
            }
        }
        if ($encontrado) { break }
    }
    
    if (-not $encontrado) {
        Write-Host "  NO encontrado en columnas 1-30 (primeras $maxCheck filas)"
    }
}

$wb.Close($false)
$e.Quit()
