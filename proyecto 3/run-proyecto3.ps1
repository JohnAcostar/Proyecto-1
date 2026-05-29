param(
    [switch]$CompileOnly
)

$ErrorActionPreference = "Stop"
Set-Location -Path $PSScriptRoot

if (-not (Test-Path -Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sourcesFile = New-TemporaryFile
Get-ChildItem -Path "src" -Recurse -Filter "*.java" | ForEach-Object {
    $_.FullName
} | Set-Content -Path $sourcesFile -Encoding UTF8

javac --release 21 -encoding UTF-8 -cp "lib/*" -d bin "@$sourcesFile"
Remove-Item -Path $sourcesFile -Force

if (-not $CompileOnly) {
    java -cp "bin;lib/*" gui.MainSwing
}
