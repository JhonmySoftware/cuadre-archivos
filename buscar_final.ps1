$e = New-Object -ComObject Excel.Application
$e.Visible = $false
$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026\Cuadre 07_04_2026.xlsx')

Write-Host "=== Busca rapida en columnas de cuenta ==="
$buscar = [long]"560266060000327"

$hoja = $wb.Sheets.Item("Divide estructura")

$columnasCuenta = @(4, 24)

foreach ($c in $columnasCuenta) {
    $h = $hoja.Cells.Item(1, $c).Text.Trim()
    Write-Host "Columna $c - $h"
    for ($r = 2; $r -le 88; $r++) {
        $val = $hoja.Cells.Item($r, $c).Text.Trim()
        if ($val) {
            try {
                $v = [long]($val -replace '^0+', '')
                if ($v -eq $buscar) {
                    Write-Host "  ENCONTRADO Fila $r Valor $val"
                }
            } catch {}
        }
    }
}

Write-Host ""
Write-Host "=== Divide - primeras filas ==="
$hoja2 = $wb.Sheets.Item("Divide")
for ($r = 2; $r -le 10; $r++) {
    $v4 = $hoja2.Cells.Item($r, 4).Text.Trim()
    $v10 = $hoja2.Cells.Item($r, 10).Text.Trim()
    if ($v4) {
        try {
            $vn4 = [long]($v4 -replace '^0+', '')
            if ($vn4 -eq $buscar) {
                Write-Host "Col 4 ENCONTRADO Fila $r Valor $v4"
            }
        } catch {}
    }
    if ($v10) {
        try {
            $vn10 = [long]($v10 -replace '^0+', '')
            if ($vn10 -eq $buscar) {
                Write-Host "Col 10 ENCONTRADO Fila $r Valor $v10"
            }
        } catch {}
    }
}

$wb.Close($false)
$e.Quit()
