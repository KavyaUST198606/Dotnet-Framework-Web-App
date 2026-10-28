# TeamCity setup

## 1. Agent prerequisites (Windows)
- Windows Server or Windows 10/11
- Visual Studio 2019/2022 Build Tools with the **.NET Framework 4.8 build tools**
  and **MSTest** workloads (plus **ASP.NET and web development** if building SimpleWebApp)
- TeamCity agent running as a service with access to the repo

## 2. Create the project
1. **Administration → Projects → Create project**, linked to this Git repo.
2. Add a Build Configuration.

## 3. Build step
- Type: **PowerShell**, Script: **Script file**, file `build.ps1`
- Arguments: `-Configuration Release`

## 4. Artifact paths


## 5. Trigger
Add a **VCS trigger** so builds run on each commit.

## 6. Running locally
```powershell
powershell -ExecutionPolicy Bypass -File build.ps1
```

## Alternative: build as code
TeamCity can read `.teamcity/settings.kts` from the repo. Enable it under
**Edit Project Settings → Versioned Settings**.

