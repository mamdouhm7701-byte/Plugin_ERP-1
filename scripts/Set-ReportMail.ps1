# شرح الملف: يحفظ إعداد إرسال التقرير بالبريد محليًا.
param(
    [string]$Username = 'eyd.mmdouh@gmail.com',
    [string]$Recipients = 'mamdouhm7701@gmail.com'
)
$ErrorActionPreference = 'Stop'
if (-not $Username) { $Username = Read-Host 'Gmail sending account' }
if ($Username -notmatch '^[^\s@]+@[^\s@]+\.[^\s@]+$') { throw 'Invalid sending email' }
foreach ($address in $Recipients.Split(',')) {
    if ($address.Trim() -notmatch '^[^\s@]+@[^\s@]+\.[^\s@]+$') { throw 'Invalid recipient email' }
}
$securePassword = Read-Host 'Gmail App Password (hidden)' -AsSecureString
$pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
try {
    $appPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer).Replace(' ', '')
    if ($appPassword -notmatch '^[a-zA-Z]{16}$') { throw 'Expected a 16-letter Gmail App Password' }
    $file = Join-Path $PSScriptRoot '../config/ReportMail.local.properties'
    $content = "enabled=true`nusername=$Username`nto=$Recipients`npassword=$appPassword`n"
    [IO.File]::WriteAllText($file, $content, [Text.Encoding]::ASCII)
    Write-Host 'Mail enabled. Local settings are ignored by Git. Run tests to send reports.'
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer)
    $appPassword = $null
    $content = $null
}
