import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.*
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2024.03"  // must match your TeamCity server version

project {
    buildType(Build)
}

object Build : BuildType({
    name = "Build, Scan, Test and Publish"
    description = "Restore, Snyk scan, build, test and publish the .NET Framework solution"

    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
    }

    steps {
        nuGetInstaller {
            name = "1. Restore NuGet packages"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            projects = "MyDotNetFrameworkApp.sln"
        }

        powerShell {
            name = "2. Snyk dependency scan"
            scriptMode = script {
                content = """
                    ${'$'}env:SNYK_TOKEN = "%snyk.token%"
                    npm install -g snyk
                    if (${'$'}LASTEXITCODE -ne 0) { throw "Failed to install Snyk CLI" }
                    snyk test --file=MyDotNetFrameworkApp.sln --all-projects --severity-threshold=high
                    if (${'$'}LASTEXITCODE -ne 0) { throw "Snyk found high or critical dependency vulnerabilities" }
                """.trimIndent()
            }
        }

        powerShell {
            name = "3. Snyk code scan"
            scriptMode = script {
                content = """
                    ${'$'}env:SNYK_TOKEN = "%snyk.token%"
                    snyk code test --severity-threshold=high
                    if (${'$'}LASTEXITCODE -ne 0) { throw "Snyk code scan found high or critical issues" }
                """.trimIndent()
            }
        }

        msBuild {
            name = "4. Build solution"
            path = "MyDotNetFrameworkApp.sln"
            targets = "Rebuild"
            runnerArgs = "/p:Configuration=Release /p:Platform=\"Any CPU\" /m /v:minimal"
        }

        vsTest {
            name = "5. Run MSTest unit tests"
            testAssemblies = "tests/MyApp.Core.Tests/bin/Release/MyApp.Core.Tests.dll"
        }

        powerShell {
            name = "6. Publish console app"
            scriptMode = script {
                content = """
                    New-Item -ItemType Directory -Force -Path artifacts\publish | Out-Null
                    Copy-Item src\MyApp.Console\bin\Release\* artifacts\publish -Recurse -Force
                """.trimIndent()
            }
        }
    }

    triggers {
        vcs { }
    }

    artifactRules = "artifacts/** => artifacts"
})