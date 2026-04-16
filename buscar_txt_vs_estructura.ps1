$e = New-Object -ComObject Excel.Application
$e.Visible = $false
$wb = $e.Workbooks.Open('C:\Users\fabio\Downloads\Cuadre 07_04_2026\Cuadre 07_04_2026.xlsx')

Write-Host "=== Extrayendo cuentas del TXT ==="
$txtPath = 'C:\Users\fabio\Downloads\Cuadre 07_04_2026\Credibanco_P2M_Breb_2_20260413.txt'
$lineas = Get-Content $txtPath
$header = $lineas[0] -split ","
$data = $lineas[1] -split ","

Write-Host "Header: numero_cuenta_tarjeta esta en posicion $([array]::IndexOf($header, 'numero_cuenta_tarjeta'))"
Write-Host "Header: numero_cuenta_origen esta en posicion $([array]::IndexOf($header, 'numero_cuenta_origen'))"

$idxTarjeta = [array]::IndexOf($header, 'numero_cuenta_tarjeta')
$idxOrigen = [array]::IndexOf($header, 'numero_cuenta_origen')

Write-Host ""
Write-Host "Cuentas del TXT:"
Write-Host "  numero_cuenta_tarjeta: $($data[$idxTarjeta])"
Write-Host "  numero_cuenta_origen: $($data[$idxOrigen])"

$cuentaTarjeta = [long]($data[$idxTarjeta])
$cuentaOrigen = [long]($data[$idxOrigen])

Write-Host ""
Write-Host "=== Buscando en Divide estructura ==="

$hoja = $wb.Sheets.Item("Divide estructura")

for ($r = 2; $r -le 88; $r++) {
    for ($c = 1; $c -le 115; $c++) {
        $val = $hoja.Cells.Item($r, $c).Text.Trim()
        if ($val -and $val.Length -gt 10) {
            try {
                $v = [long]($val -replace '^0+', '')
                if ($v -eq $cuentaTarjeta -or $v -eq $cuentaOrigen) {
                    $h = $hoja.Cells.Item(1, $c).Text.Trim()
                    Write-Host "ENCONTRADO"
                    Write-Host "  Fila $r, Col $c, Header: $h"
                    Write-Host "  Valor: $val"
                }
            } catch {}
        }
    }
}

Write-Host "Fin"
$wb.Close($false)
$e.Quit()
