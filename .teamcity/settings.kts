import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2024.03"  // match your TeamCity server version

project {
    params {
        // Set the real token value as a secure parameter in TeamCity (Project > Parameters),
        // or replace this reference with the credentialsJSON id that stores it.
        password("sonar.token", "credentialsJSON:CHANGE-ME", display = ParameterDisplay.HIDDEN)
        text("sonar.host.url", "https://sonarqube.example.com", display = ParameterDisplay.NORMAL)
        text("sonar.projectKey", "your-project-key", display = ParameterDisplay.NORMAL)
    }

    buildType(Build)
}

object Build : BuildType({
    name = "Build, Test, Coverage, Sonar"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        powerShell {
            name = "Install SonarScanner and ReportGenerator"
            scriptMode = script {
                content = """
                    dotnet tool update --global dotnet-sonarscanner
                    dotnet tool update --global dotnet-reportgenerator-globaltool
                """.trimIndent()
            }
        }

        powerShell {
            name = "Sonar begin"
            scriptMode = script {
                content = """
                    dotnet sonarscanner begin /k:"%sonar.projectKey%" /d:sonar.host.url="%sonar.host.url%" /d:sonar.token="%sonar.token%" /d:sonar.cs.opencover.reportsPaths="TestResults/**/coverage.opencover.xml"
                """.trimIndent()
            }
        }

        powerShell {
            name = "Build"
            scriptMode = script {
                content = """
                    dotnet build --configuration Release --no-incremental
                """.trimIndent()
            }
        }

        powerShell {
            name = "Test with coverage"
            scriptMode = script {
                content = """
                    dotnet test --configuration Release --no-build --results-directory TestResults --collect:"XPlat Code Coverage" --logger "trx;LogFileName=results.trx" -- DataCollectionRunSettings.DataCollectors.DataCollector.Configuration.Format=opencover
                """.trimIndent()
            }
        }

        powerShell {
            name = "Coverage report"
            scriptMode = script {
                content = """
                    reportgenerator -reports:"TestResults/**/coverage.opencover.xml" -targetdir:coverage -reporttypes:Html;Cobertura;TextSummary
                """.trimIndent()
            }
        }

        powerShell {
            name = "Sonar end"
            scriptMode = script {
                content = """
                    dotnet sonarscanner end /d:sonar.token="%sonar.token%"
                """.trimIndent()
            }
        }

        powerShell {
            name = "Package"
            scriptMode = file { path = "build.ps1" }
            scriptArgs = "-Configuration Release -Target Package"
        }
    }

    triggers {
        vcs { }
    }

    artifactRules = """
        artifacts/** => artifacts
        TestResults/** => TestResults
        coverage/** => coverage
    """.trimIndent()
})
 
