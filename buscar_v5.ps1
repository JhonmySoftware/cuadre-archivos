$e = New-Object -ComObject Excel.Application
$e.Visible = $false
$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026\Cuadre 07_04_2026.xlsx')

Write-Host "=== BUSQUEDA EN COLUMNAS CLAVE ==="
$buscar = [long]"560266060000327"

$hoja = $wb.Sheets.Item("Divide estructura")
Write-Host "=== Divide estructura ==="

$headers = @()
for ($c = 1; $c -le 30; $c++) {
    $h = $hoja.Cells.Item(1, $c).Text.Trim()
    $headers += $h
    Write-Host "Col $c : $h"
}

Write-Host ""
Write-Host "Buscando $buscar..."

for ($c = 1; $c -le 30; $c++) {
    for ($r = 2; $r -le 88; $r++) {
        $val = $hoja.Cells.Item($r, $c).Text.Trim()
        if ($val) {
            $valClean = $val -replace '^0+', ''
            try {
                if ([long]$valClean -eq $buscar) {
                    Write-Host "ENCONTRADO! Fila $r, Col $c ($($headers[$c-1])): '$val'"
                }
            } catch {}
        }
    }
}

Write-Host "Fin busqueda"
$wb.Close($false)
$e.Quit()
