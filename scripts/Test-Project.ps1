[CmdletBinding()]
param(
    [ValidateSet('Local', 'Acceptance', 'Regression')]
    [string]$Mode = 'Local'
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$runId = [Guid]::NewGuid().ToString('N')
$runRoot = Join-Path $projectRoot "target/validation/$runId"
$null = New-Item -ItemType Directory -Path $runRoot -Force
Push-Location -LiteralPath $projectRoot
try {
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
    if ($Mode -ne 'Local') {
        $suite = if ($Mode -eq 'Acceptance') {
            'src/test/resources/additional/standalone/acceptance-testng.xml'
        } else { 'src/test/resources/regression-testng.xml' }
        $liveArgs = @('--batch-mode', '--no-transfer-progress',
            '-DExecutionType=LocalHeadless', '-Dallure.report.generate=false',
            "-Dallure.results.directory=$runRoot/live/allure-results",
            "-Dallure.report.directory=$runRoot/live/allure-report",
            "-Dsurefire.suiteXmlFiles=$suite", 'test')
        & mvn @liveArgs *> (Join-Path $runRoot 'live.log')
        $liveExitCode = $LASTEXITCODE
        if (Test-Path -LiteralPath 'target/surefire-reports') {
            Copy-Item -LiteralPath 'target/surefire-reports' -Destination (Join-Path $runRoot 'live/surefire-reports') -Recurse
        }
        if ($liveExitCode -ne 0) {
            Get-Content -LiteralPath (Join-Path $runRoot 'live.log') -Tail 35
            throw "Live $Mode validation failed. Preserve target recovery journals."
        }
        Write-Host "Live $Mode validation passed."
    }
    Write-Host "Validation complete. Evidence: $runRoot"
} finally {
    Pop-Location
}
