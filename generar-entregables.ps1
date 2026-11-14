$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$env:PYTHONIOENCODING = "utf-8"
$raiz = Split-Path -Parent $MyInvocation.MyCommand.Path
$entregables = Join-Path $raiz "docs/entregables"
$compilado = Join-Path $raiz "out/entregables"
New-Item -ItemType Directory -Force -Path $entregables | Out-Null
New-Item -ItemType Directory -Force -Path $compilado | Out-Null

$fuentes = Get-ChildItem -Path (Join-Path $raiz "src") -Recurse -Filter *.java |
    ForEach-Object { $_.FullName }

javac -encoding UTF-8 -d $compilado $fuentes
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

python (Join-Path $raiz "docs/generar_evidencia.py")
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

python (Join-Path $raiz "docs/generar_informe.py")
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$empaquetado = Join-Path $raiz "out/empaquetado"
if (Test-Path $empaquetado) { Remove-Item -Recurse -Force $empaquetado }
New-Item -ItemType Directory -Force -Path (Join-Path $empaquetado "docs") | Out-Null
Copy-Item -Recurse (Join-Path $raiz "src") (Join-Path $empaquetado "src")
Copy-Item (Join-Path $raiz "docs/informe.md") (Join-Path $empaquetado "docs/informe.md")
Copy-Item (Join-Path $raiz "docs/generar_informe.py") (Join-Path $empaquetado "docs/generar_informe.py")
Copy-Item (Join-Path $raiz "docs/generar_evidencia.py") (Join-Path $empaquetado "docs/generar_evidencia.py")
Copy-Item (Join-Path $raiz "README.md") (Join-Path $empaquetado "README.md")
Copy-Item (Join-Path $raiz "ejecutar.ps1") (Join-Path $empaquetado "ejecutar.ps1")
Copy-Item (Join-Path $raiz "probar.ps1") (Join-Path $empaquetado "probar.ps1")
Copy-Item (Join-Path $raiz "generar-entregables.ps1") (Join-Path $empaquetado "generar-entregables.ps1")
Copy-Item (Join-Path $raiz ".gitignore") (Join-Path $empaquetado ".gitignore")

$zip = Join-Path $entregables "codigo-fuente.zip"
if (Test-Path $zip) { Remove-Item -Force $zip }
Compress-Archive -Path (Join-Path $empaquetado "*") -DestinationPath $zip -Force

"Entregables generados en: $entregables"
