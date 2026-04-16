$e = New-Object -ComObject Excel.Application
$e.Visible = $false

$txtPath = 'C:\Users\fabio\Downloads\Cuadre 07_04_2026\Credibanco_P2M_Breb_2_20260413.txt'
$lineas = Get-Content $txtPath
$header = $lineas[0] -split ","
$data = $lineas[1] -split ","

$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026\Cuadre 07_04_2026.xlsx')

Write-Host "=== TODAS las columnas de TODAS las filas en hojas pequenas ==="

foreach ($nombre in @("Extracto POS", "Divide estructura")) {
    Write-Host ""
    Write-Host "=== Hoja: $nombre ==="
    $hoja = $wb.Sheets.Item($nombre)
    $maxRow = $hoja.UsedRange.Rows.Count
    $maxCol = $hoja.UsedRange.Columns.Count
    
    Write-Host "Dimension: $maxRow filas x $maxCol columnas"
    
    $headers = @()
    for ($c = 1; $c -le $maxCol; $c++) {
        $headers += $hoja.Cells.Item(1, $c).Text.Trim()
    }
    
    for ($r = 2; $r -le $maxRow; $r++) {
        for ($c = 1; $c -le $maxCol; $c++) {
            $val = $hoja.Cells.Item($r, $c).Text.Trim()
            if ($val) {
                $h = $headers[$c-1]
                if ($h -match "cuenta| numero") {
                    Write-Host "Row $r Col $c [$h]: $val"
                }
            }
        }
    }
}

Write-Host ""
Write-Host "=== Muestra de datos en Divide estructura (filas 2-5) ==="
$hoja = $wb.Sheets.Item("Divide estructura")
for ($r = 2; $r -le 5; $r++) {
    Write-Host "---Fila $r---"
    for ($c = 1; $c -le 30; $c++) {
        $val = $hoja.Cells.Item($r, $c).Text.Trim()
        if ($val) {
            $h = $hoja.Cells.Item(1, $c).Text.Trim()
            Write-Host "  Col $c [$h]: $val"
        }
    }
}

$wb.Close($false)
$e.Quit()
