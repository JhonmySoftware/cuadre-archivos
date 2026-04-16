$e = New-Object -ComObject Excel.Application
$e.Visible = $false
$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026.xlsx')

Write-Host "=== HOJAS ==="
$wb.Sheets | ForEach-Object { Write-Host $_.Name }

Write-Host ""
Write-Host "=== Divide estructura - Encabezados ==="
$ws = $wb.Sheets.Item('Divide estructura')
$headers = $ws.Range('A1:CV1').Value2
if ($headers) {
    $col = 1
    foreach ($h in $headers) {
        if ($h) { Write-Host "Col $col : $h" }
        $col++
    }
}

Write-Host ""
Write-Host "=== Divide estructura - Primeras 3 filas de datos ==="
$data = $ws.Range('A1:CV3').Value2
if ($data) {
    foreach ($row in $data) {
        Write-Host "---"
        $i = 0
        foreach ($cell in $row) {
            $i++
            if ($cell -and $i -le 30) { Write-Host "  $cell" }
        }
    }
}

Write-Host ""
Write-Host "=== Divide - Encabezados ==="
$ws2 = $wb.Sheets.Item('Divide')
$headers2 = $ws2.Range('A1:Z1').Value2
if ($headers2) {
    $col = 1
    foreach ($h in $headers2) {
        if ($h) { Write-Host "Col $col : $h" }
        $col++
    }
}

Write-Host ""
Write-Host "=== Divide - Primeros datos (fila 2) ==="
$data2 = $ws2.Range('A2:Z2').Value2
if ($data2) {
    foreach ($cell in $data2) {
        if ($cell) { Write-Host "  $cell" }
    }
}

$wb.Close($false)
$e.Quit()
