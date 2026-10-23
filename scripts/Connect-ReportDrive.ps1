param([Parameter(Mandatory=$true)][string]$ClientJson)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
Push-Location $projectRoot
try {
    $clientFile = (Resolve-Path -LiteralPath $ClientJson).Path
    & mvn --batch-mode --no-transfer-progress '-DskipTests' compile dependency:build-classpath '-Dmdep.outputFile=target/report-mail-classpath.txt'
    if ($LASTEXITCODE -ne 0) { throw 'Maven preparation failed' }
    $classpath = (Join-Path $projectRoot 'target/classes') + [IO.Path]::PathSeparator + [IO.File]::ReadAllText((Join-Path $projectRoot 'target/report-mail-classpath.txt')).Trim()
    $java = Get-ChildItem "$env:USERPROFILE/.jdks" -Directory -Filter '*21*' -ErrorAction SilentlyContinue | Where-Object { Test-Path (Join-Path $_.FullName 'bin/java.exe') } | Select-Object -First 1
    $javaExecutable = if ($java) { Join-Path $java.FullName 'bin/java.exe' } else { 'java' }
    & $javaExecutable -cp $classpath Plugin_ERP.Utiel.Allur.ReportMailCommand authorize $clientFile
    if ($LASTEXITCODE -ne 0) { throw 'Drive authorization did not complete' }
} finally { Pop-Location }
