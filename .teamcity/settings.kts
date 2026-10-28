import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2024.03"  // match your TeamCity server version

project {
    buildType(Build)
}

object Build : BuildType({
    name = "Build and Test"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        powerShell {
            name = "Build, test, package"
            scriptMode = file { path = "build.ps1" }
            scriptArgs = "-Configuration Release"
        }
    }

    triggers {
        vcs { }
    }

    artifactRules = "artifacts/** => artifacts"
})