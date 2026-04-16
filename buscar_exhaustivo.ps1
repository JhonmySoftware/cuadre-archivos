$e = New-Object -ComObject Excel.Application
$e.Visible = $false
$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026\Cuadre 07_04_2026.xlsx')

Write-Host "=============================================="
Write-Host "BUSQUEDA EXHAUSTIVA - TODAS LAS FILAS"
Write-Host "=============================================="
Write-Host ""

$buscarOriginal = "560266060000327"
$buscarSinCeros = [long]::Parse($buscarOriginal)
Write-Host "Buscando: '$buscarOriginal' (sin ceros: $buscarSinCeros)"
Write-Host ""

foreach ($hoja in $wb.Sheets) {
    Write-Host "=== Hoja: $($hoja.Name) ==="
    $usedRange = $hoja.UsedRange
    $lastRow = $usedRange.Row + $usedRange.Rows.Count - 1
    $lastCol = $usedRange.Column + $usedRange.Columns.Count - 1
    Write-Host "Rango: Filas 1-$lastRow, Columnas 1-$lastCol"
    
    $encontrados = @()
    
    for ($fila = 1; $fila -le $lastRow; $fila++) {
        for ($col = 1; $col -le $lastCol; $col++) {
            $valor = $hoja.Cells.Item($fila, $col).Text.Trim()
            if ($valor -ne "") {
                try {
                    $valorNum = [long]$valor
                    if ($valorNum -eq $buscarSinCeros) {
                        $encontrados += "  Fila $fila, Col $col : '$valor'"
                    }
                } catch {
                    $valorSinCeros = $valor -replace '^0+', ''
                    try {
                        $v = [long]$valorSinCeros
                        if ($v -eq $buscarSinCeros) {
                            $encontrados += "  Fila $fila, Col $col : '$valor' (normalizado: $valorSinCeros)"
                        }
                    } catch {}
                }
            }
        }
    }
    
    if ($encontrados.Count -gt 0) {
        Write-Host "  ENCONTRADOS ($($encontrados.Count)):"
        $encontrados | ForEach-Object { Write-Host $_ }
    } else {
        Write-Host "  NO encontrado"
    }
    Write-Host ""
}

$wb.Close($false)
$e.Quit()
