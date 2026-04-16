$e = New-Object -ComObject Excel.Application
$e.Visible = $false
$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026\Cuadre 07_04_2026.xlsx')

Write-Host "=== Busca rapida en columnas de cuenta ==="
$buscar = [long]"560266060000327"

$hoja = $wb.Sheets.Item("Divide estructura")

$columnasCuenta = @(4, 24, 100, 101, 102)

foreach ($c in $columnasCuenta) {
    $h = $hoja.Cells.Item(1, $c).Text.Trim()
    Write-Host "Columna $c ($h)..."
    for ($r = 2; $r -le 88; $r++) {
        $val = $hoja.Cells.Item($r, $c).Text.Trim()
        if ($val) {
            try {
                $v = [long]($val -replace '^0+', '')
                if ($v -eq $buscar) {
                    Write-Host "  ENCONTRADO! Fila $r: '$val'"
                }
            } catch {}
        }
    }
}

Write-Host ""
Write-Host "=== Busca en Divide (columnas de cuenta) ==="
$hoja2 = $wb.Sheets.Item("Divide")
$columnasDivide = @(2, 4, 10, 12)

foreach ($c in $columnasDivide) {
    $h = $hoja2.Cells.Item(1, $c).Text.Trim()
    Write-Host "Columna $c ($h)..."
    for ($r = 2; $r -le 100; $r++) {
        $val = $hoja2.Cells.Item($r, $c).Text.Trim()
        if ($val) {
            try {
                $v = [long]($val -replace '^0+', '')
                if ($v -eq $buscar) {
                    Write-Host "  ENCONTRADO! Fila $r: '$val'"
                }
            } catch {}
        }
    }
}

$wb.Close($false)
$e.Quit()
