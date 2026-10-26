[CmdletBinding()]
param(
    [ValidateSet('Local', 'Acceptance', 'Regression', 'ParallelPilot', 'ParallelScreens', 'Smoke', 'Sanity', 'E2E', 'Qualification')]
    [string]$Mode = 'Local',
    [ValidateRange(1,6)][int]$Workers = 6,
    [ValidateSet('LocalHeadless','Local')][string]$ExecutionType = 'LocalHeadless'
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$runId = [Guid]::NewGuid().ToString('N')
$runRoot = Join-Path $projectRoot "target/validation/$runId"
$null = New-Item -ItemType Directory -Path $runRoot -Force
Push-Location -LiteralPath $projectRoot
$liveMutex=$null;$ownsLiveMutex=$false
$liveFile=$null
$previousMail=$env:REPORT_MAIL_ENABLED
try {
    $env:REPORT_MAIL_ENABLED='false'
    if($Mode -ne 'Local') {
        $hash=[Convert]::ToHexString([Security.Cryptography.SHA256]::HashData([Text.Encoding]::UTF8.GetBytes($projectRoot.ToUpperInvariant())))
        $liveMutex=[Threading.Mutex]::new($false,"Local\PluginERP-Live-$hash")
        try {$ownsLiveMutex=$liveMutex.WaitOne(0)} catch [Threading.AbandonedMutexException] {$ownsLiveMutex=$true}
        if(-not $ownsLiveMutex){throw 'Another live run already owns this workspace. Do not overlap separate JVMs on the same branch.'}
        # Shared with the native IntelliJ TestNG launcher; Windows denies overlapping file leases.
        try {$liveFile=[IO.File]::Open((Join-Path $projectRoot 'target/erp-live-run.lock'),[IO.FileMode]::OpenOrCreate,[IO.FileAccess]::ReadWrite,[IO.FileShare]::None)}
        catch {throw 'Another ERP run is active. Wait for it to finish before starting a separate run.'}
        if(Get-Command Get-CimInstance -ErrorAction SilentlyContinue) {
            $existing=@(Get-CimInstance Win32_Process | Where-Object {
                $_.Name -eq 'java.exe' -and $_.CommandLine -match '(?:-DExecutionType=LocalHeadless|com\.intellij\.rt\.testng\.RemoteTestNGStarter)' -and $_.CommandLine -match [regex]::Escape($projectRoot)
            })
            if($existing.Count){throw 'A live Maven run is already active for this project; inspect it before starting another.'}
        }
    }
    Write-Host "Validation mode: $Mode"
    Write-Host "Run output: $runRoot"
    # Never clean target: it can contain journals for unfinished ERP cleanup.
    $localArgs = @('--batch-mode', '--no-transfer-progress', '-Pci',
        "-Dallure.results.directory=$runRoot/local/allure-results",
        "-Dallure.report.directory=$runRoot/local/allure-report", 'verify')
    & mvn @localArgs *> (Join-Path $runRoot 'local.log')
    $localExitCode = $LASTEXITCODE
    if (Test-Path -LiteralPath 'target/ci/surefire-reports') {
        Copy-Item -LiteralPath 'target/ci/surefire-reports' -Destination (Join-Path $runRoot 'local/surefire-reports') -Recurse
    }
    if ($localExitCode -ne 0) {
        Get-Content -LiteralPath (Join-Path $runRoot 'local.log') -Tail 35
        throw 'Local validation failed; live tests were not started.'
    }
    Write-Host 'Local validation passed.'
    if($Mode -in @('Qualification','Smoke','Sanity','E2E','Regression')) {
        $freeGB=if(Get-Command Get-CimInstance -ErrorAction SilentlyContinue) {
            (Get-CimInstance Win32_OperatingSystem).FreePhysicalMemory/1MB
        } else {3.0}
        $memoryLimit=if($freeGB -ge 6){6}elseif($freeGB -ge 4.5){4}elseif($freeGB -ge 2.75){3}elseif($freeGB -ge 2.0){2}else{1}
        $cpuLimit=[Math]::Max(1,[Math]::Floor([Environment]::ProcessorCount/2))
        $effectiveWorkers=[int][Math]::Min($Workers,[Math]::Min($memoryLimit,$cpuLimit))
        [pscustomobject]@{Requested=$Workers;Effective=$effectiveWorkers;FreeMemoryGB=[Math]::Round($freeGB,2);Policy='Conservative memory guard; isolated classes only; branch-default and operator classes exclusive'} |
            ConvertTo-Json | Set-Content -LiteralPath (Join-Path $runRoot 'worker-decision.json') -Encoding UTF8
        Write-Host "Parallel workers: requested $Workers, selected $effectiveWorkers; free memory $([Math]::Round($freeGB,2)) GB."
        & mvn '--batch-mode' '--no-transfer-progress' 'dependency:build-classpath' "-Dmdep.outputFile=$runRoot/test-classpath.txt" *> (Join-Path $runRoot 'classpath.log')
        if($LASTEXITCODE -ne 0){throw 'Could not resolve the Maven test runtime classpath.'}
        $runtimeClasspath=(Get-Content -LiteralPath (Join-Path $runRoot 'test-classpath.txt') -Raw).Trim()
    }
    if ($Mode -ne 'Local') {
        $levels = if ($Mode -eq 'Qualification') { @('Smoke', 'Sanity', 'E2E') } else { @($Mode) }
        $outcomes = @()
        foreach ($level in $levels) {
            $suite = switch ($level) {
                'Acceptance' { 'src/test/resources/additional/standalone/acceptance-testng.xml' }
                'ParallelPilot' { 'src/test/resources/additional/standalone/parallel-three-pilot-testng.xml' }
                'ParallelScreens' { 'src/test/resources/additional/standalone/parallel-three-screens-testng.xml' }
                'Smoke' { 'src/test/resources/smoke-testng.xml' }
                'Sanity' { 'src/test/resources/sanity-testng.xml' }
                'E2E' { 'src/test/resources/e2e-testng.xml' }
                default { 'src/test/resources/regression-testng.xml' }
            }
            $levelRoot = Join-Path $runRoot $level.ToLowerInvariant()
            $null = New-Item -ItemType Directory -Path $levelRoot -Force
            $started = [DateTimeOffset]::UtcNow
            Write-Host "Starting $level ($ExecutionType). Evidence: $levelRoot"
            $liveArgs = @('--batch-mode', '--no-transfer-progress',
                "-DExecutionType=$ExecutionType", '-Dallure.report.generate=false', '-Dallure.report.open=false',
                "-Dallure.results.directory=$levelRoot/allure-results",
                "-Dallure.report.directory=$levelRoot/allure-report",
                "-Dsurefire.reportsDirectory=$levelRoot/surefire-reports",
                "-Dsurefire.suiteXmlFiles=$suite", 'test')
            if($level -in @('Smoke','Sanity','E2E','Regression')) {
                & (Join-Path $PSScriptRoot 'Invoke-QualificationLevel.ps1') -Suite $suite -OutputRoot $levelRoot -Classpath $runtimeClasspath -Workers $effectiveWorkers -ExecutionType $ExecutionType
            } else {
                & mvn @liveArgs *> (Join-Path $levelRoot 'live.log')
            }
            $liveExitCode = $LASTEXITCODE
            $outcomes += [pscustomobject]@{
                Level = $level; ExitCode = $liveExitCode; Suite = $suite; Evidence = $levelRoot
                StartedUtc = $started.ToString('o'); FinishedUtc = [DateTimeOffset]::UtcNow.ToString('o')
            }
            $outcomes | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath (Join-Path $runRoot 'live-outcomes.json') -Encoding UTF8
            if ($liveExitCode -ne 0) {
                Write-Warning "$level reported failures. Evidence and recovery journals are preserved."
            } else { Write-Host "$level passed." }
        }
        if (@($outcomes | Where-Object ExitCode -ne 0).Count -gt 0) {
            throw "Live validation has failures; see live-outcomes.json. All requested levels were attempted."
        }
    }
    Write-Host "Validation complete. Evidence: $runRoot"
} finally {
    if($liveFile){$liveFile.Dispose()}
    if($ownsLiveMutex){$liveMutex.ReleaseMutex()}
    if($liveMutex){$liveMutex.Dispose()}
    $env:REPORT_MAIL_ENABLED=$previousMail
    Pop-Location
}
