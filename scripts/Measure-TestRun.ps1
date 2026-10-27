[CmdletBinding()]
param(
    [Parameter(Mandatory)][string]$ResultsDirectory,
    [ValidateRange(1,24)][double]$BudgetHours = 7
)
$ErrorActionPreference = 'Stop'
$root = (Resolve-Path -LiteralPath $ResultsDirectory).Path
$results = @(Get-ChildItem -LiteralPath $root -Filter '*-result.json' -Recurse -File |
    ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw | ConvertFrom-Json })
if ($results.Count -eq 0) { throw 'No Allure test results found; no duration can be claimed.' }
$first = ($results | Measure-Object -Property start -Minimum).Minimum
$last = ($results | Measure-Object -Property stop -Maximum).Maximum
$spanHours = ($last - $first) / 3600000.0
$summary = [pscustomobject]@{
    ResultsDirectory = $root
    TestInvocations = $results.Count
    Failed = @($results | Where-Object status -in @('failed','broken')).Count
    Skipped = @($results | Where-Object status -eq 'skipped').Count
    MeasuredHours = [Math]::Round($spanHours, 4)
    BudgetHours = $BudgetHours
    WithinBudget = $spanHours -lt $BudgetHours
    Measurement = 'First test start to last test stop; excludes build and any setup before first test. Not proof of full Regression unless input includes all its stages.'
}
$summary | ConvertTo-Json -Depth 3
