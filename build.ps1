<#
.SYNOPSIS
    Restore, build, test, and publish script for TeamCity or local use.
.EXAMPLE
    .\build.ps1 -Configuration Release
#>
[CmdletBinding()]
param(
    [string]$Configuration = "Release"
)

$ErrorActionPreference = "Stop"
$root      = $PSScriptRoot
$solution  = Join-Path $root "MyDotNetFrameworkApp.sln"
$artifacts = Join-Path $root "artifacts"
$toolsDir  = Join-Path $root "tools"
$nuget     = Join-Path $toolsDir "nuget.exe"

function Find-MSBuild {
    $vswhere = "${env:ProgramFiles(x86)}\Microsoft Visual Studio\Installer\vswhere.exe"
    if (Test-Path $vswhere) {
        $path = & $vswhere -latest -requires Microsoft.Component.MSBuild -find "MSBuild\**\Bin\MSBuild.exe" | Select-Object -First 1
        if ($path) { return $path }
    }
    $fallback = "${env:ProgramFiles(x86)}\MSBuild\14.0\Bin\MSBuild.exe"
    if (Test-Path $fallback) { return $fallback }
    throw "MSBuild not found. Install Visual Studio Build Tools or set up MSBuild on the agent."
}

function Find-VSTest {
    $vswhere = "${env:ProgramFiles(x86)}\Microsoft Visual Studio\Installer\vswhere.exe"
    $path = & $vswhere -latest -requires Microsoft.VisualStudio.PackageGroup.TestTools.Core -find "Common7\IDE\CommonExtensions\Microsoft\TestWindow\vstest.console.exe" | Select-Object -First 1
    if (-not $path) { throw "vstest.console.exe not found." }
    return $path
}

function Get-NuGet {
    if (-not (Test-Path $nuget)) {
        New-Item -ItemType Directory -Force -Path $toolsDir | Out-Null
        Write-Host "==> Downloading nuget.exe"
        Invoke-WebRequest -Uri "https://dist.nuget.org/win-x86-commandline/latest/nuget.exe" -OutFile $nuget
    }
    return $nuget
}

$msbuild  = Find-MSBuild
$vstest   = Find-VSTest
$nugetExe = Get-NuGet

Write-Host "==> Restoring NuGet packages"
& $nugetExe restore $solution -ConfigFile (Join-Path $root "nuget.config") -NonInteractive
if ($LASTEXITCODE -ne 0) { throw "NuGet restore failed" }

Write-Host "==> Building solution ($Configuration)"
& $msbuild $solution /t:Rebuild /p:Configuration=$Configuration /p:Platform="Any CPU" /m /v:minimal
if ($LASTEXITCODE -ne 0) { throw "Build failed" }

Write-Host "==> Running tests"
$testDll = Join-Path $root "tests\MyApp.Core.Tests\bin\$Configuration\MyApp.Core.Tests.dll"
& $vstest $testDll /Logger:trx /ResultsDirectory:(Join-Path $artifacts "TestResults")
if ($LASTEXITCODE -ne 0) { throw "Tests failed" }

Write-Host "==> Publishing artifacts"
$publish = Join-Path $artifacts "publish"
New-Item -ItemType Directory -Force -Path $publish | Out-Null
Copy-Item "src\MyApp.Console\bin\$Configuration\*" $publish -Recurse -Force

Write-Host "==> Done. Output in $artifacts"
exit 0