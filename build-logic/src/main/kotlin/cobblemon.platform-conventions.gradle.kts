import gradle.kotlin.dsl.accessors._56d0f1caa5d9b078747b452e7c6e7f8d.shadowJar
import net.fabricmc.loom.util.srg.CoreModClassRemapper.remapJar

plugins {
    id("cobblemon.base-conventions")
    id("com.github.johnrengelman.shadow")
}

val bundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

loom {
    val clientConfig = runConfigs.getByName("client")
    clientConfig.runDir = "runClient"
    clientConfig.programArg("--username=CobblemonDev")
    //This is AshKetchum's UUID so you get an Ash Ketchum skin
    clientConfig.programArg("--uuid=93e4e551-589a-41cb-ab2d-435266c8e035")
    val serverConfig = runConfigs.getByName("server")
    serverConfig.runDir = "runServer"
}

tasks {

    jar {
        archiveBaseName.set("Cobblemon-${project.name}")
        archiveClassifier.set("dev-slim")
    }

    shadowJar {
        archiveClassifier.set("dev-shadow")
        archiveBaseName.set("Cobblemon-${project.name}")
        configurations = listOf(bundle)
        mergeServiceFiles()
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        archiveBaseName.set("Cobblemon-${project.name}")
        archiveVersion.set("${rootProject.version}")
    }

}