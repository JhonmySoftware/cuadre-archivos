$e = New-Object -ComObject Excel.Application
$e.Visible = $false
$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026\Cuadre 07_04_2026.xlsx')

Write-Host "=== BUSQUEDA SOLO EN HOJAS PEQUENAS ==="
$buscar = [long]"560266060000327"

$hojasPequenas = @("Extracto POS", "Divide estructura")

foreach ($nombre in $hojasPequenas) {
    try {
        $hoja = $wb.Sheets.Item($nombre)
        Write-Host ""
        Write-Host "=== $nombre ==="
        
        $maxRow = $hoja.UsedRange.Rows.Count
        $maxCol = $hoja.UsedRange.Columns.Count
        Write-Host "Dimensiones: $maxRow filas x $maxCol columnas"
        
        for ($col = 1; $col -le $maxCol; $col++) {
            $header = $hoja.Cells.Item(1, $col).Text.Trim()
            for ($row = 2; $row -le $maxRow; $row++) {
                $val = $hoja.Cells.Item($row, $col).Text.Trim()
                if ($val) {
                    $valClean = $val -replace '^0+', ''
                    try {
                        if ([long]$valClean -eq $buscar) {
                            Write-Host "  ENCONTRADO! Fila $row, Col $col ($header): '$val'"
                        }
                    } catch {}
                }
            }
        }
        Write-Host "  Fin busqueda"
    } catch {
        Write-Host "  Hoja no encontrada"
    }
}

$wb.Close($false)
$e.Quit()
