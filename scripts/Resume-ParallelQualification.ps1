[CmdletBinding()]
param([Parameter(Mandatory)][string]$RunRoot)
$ErrorActionPreference='Stop'
$projectRoot=Split-Path -Parent $PSScriptRoot
$RunRoot=[IO.Path]::GetFullPath($RunRoot)
if(-not $RunRoot.StartsWith((Join-Path $projectRoot 'target')+[IO.Path]::DirectorySeparatorChar)) {
    throw 'Run evidence must be inside this project target directory.'
}
$plan=Get-Content -LiteralPath (Join-Path $RunRoot 'e2e-plan/plan.json') -Raw | ConvertFrom-Json
$stages=@(
    @{Name='e2e-parallel';Suite='e2e-plan/parallel.xml';Count=$plan.parallelClasses.Count},
    @{Name='e2e-exclusive';Suite='e2e-plan/exclusive.xml';Count=$plan.exclusiveClasses.Count}
)
$outcomes=@();$previousMail=$env:REPORT_MAIL_ENABLED
$liveMutex=$null;$ownsLiveMutex=$false
Push-Location -LiteralPath $projectRoot
try {
    $hash=[Convert]::ToHexString([Security.Cryptography.SHA256]::HashData([Text.Encoding]::UTF8.GetBytes($projectRoot.ToUpperInvariant())))
    $liveMutex=[Threading.Mutex]::new($false,"Local\PluginERP-Live-$hash")
    try {$ownsLiveMutex=$liveMutex.WaitOne(0)} catch [Threading.AbandonedMutexException] {$ownsLiveMutex=$true}
    if(-not $ownsLiveMutex){throw 'Another live run already owns this workspace.'}
    if(Get-Command Get-CimInstance -ErrorAction SilentlyContinue) {
        $existing=@(Get-CimInstance Win32_Process | Where-Object {
            $_.Name -eq 'java.exe' -and $_.CommandLine -match '(?:-DExecutionType=LocalHeadless|com\.intellij\.rt\.testng\.RemoteTestNGStarter)' -and $_.CommandLine -match [regex]::Escape($projectRoot)
        })
        if($existing.Count){throw 'A live Maven run is already active for this project.'}
    }
    $env:REPORT_MAIL_ENABLED='false'
    foreach($stage in $stages) {
        if($stage.Count -eq 0){continue}
        $folder=Join-Path $RunRoot $stage.Name
        $null=New-Item -ItemType Directory -Path $folder -Force
        $started=[DateTimeOffset]::UtcNow
        Write-Host "Starting $($stage.Name); configured parallel workers: $($plan.workers)."
        $arguments=@('-o','--batch-mode','--no-transfer-progress','-DExecutionType=LocalHeadless',
            '-Dvideo.enabled=false','-Dallure.report.generate=false','-Dallure.report.open=false',
            '-DforkCount=1','-DreuseForks=true',"-Dallure.results.directory=$folder/allure-results",
            "-Dallure.report.directory=$folder/allure-report","-Dsurefire.reportsDirectory=$folder/surefire-reports",
            "-Dsurefire.suiteXmlFiles=$(Join-Path $RunRoot $stage.Suite)",'test')
        & mvn @arguments *> (Join-Path $folder 'live.log')
        $code=$LASTEXITCODE
        $outcomes += [pscustomobject]@{Stage=$stage.Name;ExitCode=$code;StartedUtc=$started.ToString('o');FinishedUtc=[DateTimeOffset]::UtcNow.ToString('o')}
        $outcomes | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath (Join-Path $RunRoot 'parallel-outcomes.json') -Encoding UTF8
        Write-Host "$($stage.Name) finished; exit code: $code."
    }
    if(@($outcomes | Where-Object ExitCode -ne 0).Count){throw 'Parallel qualification has recorded failures; see parallel-outcomes.json.'}
} finally {
    if($ownsLiveMutex){$liveMutex.ReleaseMutex()}
    if($liveMutex){$liveMutex.Dispose()}
    $env:REPORT_MAIL_ENABLED=$previousMail;Pop-Location
}
