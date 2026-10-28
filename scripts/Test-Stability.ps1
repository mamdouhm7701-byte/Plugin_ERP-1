# شرح الملف: يشغل نفس الفحص ثلاث مرات أو أكثر ثم يقارن النتائج والإعدادات في تقرير مستقل.
# فائدته: يختبر ثبات الفحوص المحلية أو يفتح اختيار البيزنس مرة واحدة لجولات بفروع جديدة.
[CmdletBinding()]
param(
    [ValidateSet('Local','Business')][string]$Mode='Local',
    [ValidateRange(3,10)][int]$Repeats=3
)
$ErrorActionPreference='Stop'
$projectRoot=Split-Path -Parent $PSScriptRoot
$output=Join-Path $projectRoot ('target/stability/'+[guid]::NewGuid().ToString('N'))
$null=New-Item -ItemType Directory -Path $output -Force
Push-Location -LiteralPath $projectRoot
try {
    Write-Host "Stability evidence: $output"
    if($Mode -eq 'Local') {
        $rounds=@()
        for($index=1;$index -le $Repeats;$index++) {
            $round=Join-Path $output "round-$index"
            $null=New-Item -ItemType Directory -Path $round -Force
            $sources=@(Get-ChildItem src,scripts -Recurse -File | Where-Object {$_.Extension -in '.java','.xml','.properties','.json','.py','.ps1'})
            $sources+=Get-Item -LiteralPath 'pom.xml'
            $fingerprint=($sources | Sort-Object FullName | Get-FileHash -Algorithm SHA256 | ForEach-Object Hash) -join ''
            [pscustomobject]@{mode='Local';sourceFingerprint=$fingerprint;workers=1;suite='ci-testng.xml'} |
                ConvertTo-Json | Set-Content -LiteralPath (Join-Path $round 'effective-settings.json') -Encoding UTF8
            & mvn '--batch-mode' '--no-transfer-progress' '-Pci' "-Derp.build.directory=$round/build" verify *> (Join-Path $round 'run.log')
            $rounds+=@{folder="round-$index";exitCode=$LASTEXITCODE}
            @{requested=$Repeats;rounds=$rounds} | ConvertTo-Json -Depth 5 |
                Set-Content -LiteralPath (Join-Path $output 'rounds.json') -Encoding UTF8
        }
    } else {
        $build=Join-Path $output 'build'
        & mvn '--batch-mode' '--no-transfer-progress' '-Pci' "-Derp.build.directory=$build" verify *> (Join-Path $output 'local-checks.log')
        if($LASTEXITCODE -ne 0){throw 'Local checks failed; no live rounds started.'}
        $classpathFile=Join-Path $output 'classpath.txt'
        & mvn '--batch-mode' '--no-transfer-progress' 'dependency:build-classpath' "-Dmdep.outputFile=$classpathFile" *> (Join-Path $output 'classpath.log')
        if($LASTEXITCODE -ne 0){throw 'Could not resolve runtime classpath.'}
        $dependencies=(Get-Content -LiteralPath $classpathFile -Raw).Trim()
        $javaCandidates=@()
        if($env:JAVA_HOME){$javaCandidates+=Join-Path $env:JAVA_HOME 'bin/java.exe'}
        $javaCandidates+=@(Get-ChildItem -Path (Join-Path $env:USERPROFILE '.jdks') -Filter 'jdk-21*' -Directory -ErrorAction SilentlyContinue | ForEach-Object {Join-Path $_.FullName 'bin/java.exe'})
        $java=$null
        foreach($candidate in $javaCandidates) {
            if(-not (Test-Path -LiteralPath $candidate)){continue}
            $version=& $candidate -version 2>&1 | Out-String
            if($version -match 'version "21[\."]'){$java=$candidate;break}
        }
        if(-not $java){throw 'Java 21 runtime not found. Configure JAVA_HOME.'}
        $aspectj=($dependencies -split [IO.Path]::PathSeparator | Where-Object {$_ -match 'aspectjweaver[^/\\]*\.jar$'} | Select-Object -First 1)
        if(-not $aspectj){throw 'AspectJ runtime missing; live rounds were not started.'}
        & $java "-javaagent:$aspectj" '-Dfile.encoding=UTF-8' '-cp' "$build/test-classes$([IO.Path]::PathSeparator)$build/classes$([IO.Path]::PathSeparator)$dependencies" SuiteSupport.BusinessStabilityLauncher $Repeats $output
        if(-not (Test-Path -LiteralPath (Join-Path $output 'rounds.json'))){throw 'Stability selection canceled or setup failed before any round.'}
    }
    & python -X utf8 (Join-Path $PSScriptRoot 'analyze_stability.py') $output
    $global:LASTEXITCODE=$LASTEXITCODE
    Write-Host "Comparison: $(Join-Path $output 'stability-summary.html')"
    if($global:LASTEXITCODE -ne 0){throw 'Repeated successful execution was not proven. Read stability-summary.html.'}
} finally {
    Pop-Location
}
