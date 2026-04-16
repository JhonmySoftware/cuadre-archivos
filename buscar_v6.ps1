$e = New-Object -ComObject Excel.Application
$e.Visible = $false
$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026\Cuadre 07_04_2026.xlsx')

Write-Host "=== Divide estructura - TODAS las columnas ==="
$buscar = [long]"560266060000327"

$hoja = $wb.Sheets.Item("Divide estructura")

for ($c = 1; $c -le 115; $c++) {
    $h = $hoja.Cells.Item(1, $c).Text.Trim()
    for ($r = 2; $r -le 88; $r++) {
        $val = $hoja.Cells.Item($r, $c).Text.Trim()
        if ($val -and $val.Length -gt 5) {
            $valClean = $val -replace '^0+', ''
            try {
                if ([long]$valClean -eq $buscar) {
                    Write-Host "ENCONTRADO! Fila $r, Col $c: '$h' = '$val'"
                }
            } catch {}
        }
    }
}

Write-Host "Fin"

$wb.Close($false)
$e.Quit()
