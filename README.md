# MyDotNetFrameworkApp

Sample .NET Framework 4.8 repository with:

- `MyApp.Core` - class library
- `MyApp.Console` - console app that uses the library
- `SimpleWebApp` - minimal ASP.NET Web Forms app
- `MyApp.Core.Tests` - MSTest unit tests
- `build.ps1` - restore, build, test, and publish script
- `nuget.config` - NuGet package source configuration
- `.teamcity/settings.kts` - TeamCity build configuration as code
- `teamcity/README.md` - manual TeamCity setup notes

## Requirements

- Windows with Visual Studio 2019/2022 (or Build Tools) including
  the .NET Framework 4.8 development tools
- PowerShell 5.1 or later

## Build locally

```powershell
powershell -ExecutionPolicy Bypass -File build.ps1 -Configuration Release
```

Output is written to `artifacts/`.