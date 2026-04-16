$e = New-Object -ComObject Excel.Application
$e.Visible = $false
$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026\Cuadre 07_04_2026.xlsx')

Write-Host "=============================================="
Write-Host "BUSQUEDA OPTIMIZADA POR COLUMNA"
Write-Host "=============================================="
Write-Host ""

$buscarOriginal = "560266060000327"
$buscarSinCeros = [long]$buscarOriginal
Write-Host "Buscando: '$buscarOriginal' -> $buscarSinCeros"
Write-Host ""

foreach ($hoja in $wb.Sheets) {
    Write-Host "=== Hoja: $($hoja.Name) ==="
    $usedRange = $hoja.UsedRange
    $lastRow = $usedRange.Row + $usedRange.Rows.Count - 1
    $lastCol = $usedRange.Column + $usedRange.Columns.Count - 1
    
    $encontrados = @()
    
    for ($col = 1; $col -le $lastCol; $col++) {
        $header = $hoja.Cells.Item(1, $col).Text.Trim()
        
        for ($fila = 2; $fila -le [Math]::Min($lastRow, 500); $fila++) {
            $valor = $hoja.Cells.Item($fila, $col).Text.Trim()
            if ($valor -ne "") {
                try {
                    $v = [long]($valor -replace '^0+', '')
                    if ($v -eq $buscarSinCeros) {
                        $encontrados += "  Fila $fila, Col $col ($header): '$valor'"
                    }
                } catch {}
            }
        }
    }
    
    if ($encontrados.Count -gt 0) {
        Write-Host "  ENCONTRADOS ($($encontrados.Count)):"
        $encontrados | Select-Object -First 20 | ForEach-Object { Write-Host $_ }
        if ($encontrados.Count -gt 20) { Write-Host "  ... y $($encontrados.Count - 20) mas" }
    } else {
        Write-Host "  NO encontrado (primeras 500 filas)"
    }
    Write-Host ""
}

$wb.Close($false)
$e.Quit()
