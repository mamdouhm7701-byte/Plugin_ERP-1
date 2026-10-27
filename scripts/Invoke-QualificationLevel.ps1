[CmdletBinding()]
param(
    [Parameter(Mandatory)][string]$Suite,
    [Parameter(Mandatory)][string]$OutputRoot,
    [Parameter(Mandatory)][string]$Classpath,
    [ValidateRange(1,6)][int]$Workers=3,
    [ValidateSet('LocalHeadless','Local')][string]$ExecutionType='LocalHeadless',
    [ValidatePattern('^$|^[a-z0-9][a-z0-9_-]{0,63}$')][string]$BranchProfile='',
    [string]$BranchProfileFingerprint='',
    [switch]$SelectBranchProfile
)
$ErrorActionPreference='Stop'
$projectRoot=Split-Path -Parent $PSScriptRoot
$java=if($env:JAVA_HOME){Join-Path $env:JAVA_HOME 'bin/java.exe'}else{'java'}
$planRoot=Join-Path $OutputRoot 'plan'
$runtimeClasspath="$projectRoot/target/test-classes;$projectRoot/target/classes;$Classpath"
$profileArguments=@('-Dbranch.profile=', '-Dbranch.profile.fingerprint=')
if($BranchProfile) {
    $manifest=Join-Path $OutputRoot 'branch-profile.json'
    & $java -cp $runtimeClasspath Plugin_ERP.BranchProfiles.BranchProfileCommand $BranchProfile $manifest $BranchProfileFingerprint
    if($LASTEXITCODE -ne 0){throw 'Selected branch profile changed or could not be pinned.'}
    $BranchProfileFingerprint=(Get-Content -LiteralPath $manifest -Raw | ConvertFrom-Json).fingerprint
    $profileArguments=@("-Dbranch.profile=$BranchProfile", "-Dbranch.profile.fingerprint=$BranchProfileFingerprint")
} elseif($BranchProfileFingerprint -or (Test-Path -LiteralPath (Join-Path $OutputRoot 'branch-profile.json'))) {throw 'This run requires its original branch profile name and fingerprint.'}
$profileArguments += "-Dbranch.profile.select=$($SelectBranchProfile.IsPresent.ToString().ToLowerInvariant())"
& $java @profileArguments -cp $runtimeClasspath SuiteSupport.ParallelSuitePlanner $Suite $planRoot $Workers
if($LASTEXITCODE -ne 0){throw 'Could not prepare a coverage-preserving parallel plan.'}
$plan=Get-Content -LiteralPath (Join-Path $planRoot 'plan.json') -Raw | ConvertFrom-Json
$outcomes=@()
foreach($stage in @(
    @{Name='parallel';Count=$plan.parallelClasses.Count},
    @{Name='exclusive';Count=$plan.exclusiveClasses.Count}
)) {
    if($stage.Count -eq 0){continue}
    $folder=Join-Path $OutputRoot $stage.Name
    $null=New-Item -ItemType Directory -Path $folder -Force
    Write-Host "Stage $($stage.Name): $($stage.Count) classes; parallel worker limit $Workers."
    $started=[DateTimeOffset]::UtcNow
    $arguments=@('--batch-mode','--no-transfer-progress',"-DExecutionType=$ExecutionType",
        '-Dallure.report.generate=false','-Dallure.report.open=false',
        '-DforkCount=1','-DreuseForks=true',"-Dallure.results.directory=$folder/allure-results",
        "-Dallure.report.directory=$folder/allure-report","-Dsurefire.reportsDirectory=$folder/surefire-reports",
        "-Dsurefire.suiteXmlFiles=$(Join-Path $planRoot ($stage.Name+'.xml'))",'test')
    $arguments=$profileArguments+$arguments
    & mvn @arguments *> (Join-Path $folder 'live.log')
    $code=$LASTEXITCODE
    $outcomes += [pscustomobject]@{Stage=$stage.Name;ExitCode=$code;StartedUtc=$started.ToString('o');FinishedUtc=[DateTimeOffset]::UtcNow.ToString('o')}
    $outcomes | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath (Join-Path $OutputRoot 'stage-outcomes.json') -Encoding UTF8
}
$global:LASTEXITCODE=if(@($outcomes | Where-Object ExitCode -ne 0).Count){1}else{0}
