import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("pokemoncobbled.base-conventions")
    id("com.github.johnrengelman.shadow")
}

val bundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

tasks {

    withType<Jar> {
        archiveBaseName.set("PokemonCobbled-${project.name}")
        archiveClassifier.set("dev-slim")
    }

    withType<ShadowJar> {
        archiveClassifier.set("dev-shadow")
        configurations = listOf(bundle)
    }

    withType<RemapJarTask> {
        dependsOn("shadowJar")
        inputFile.set(named<ShadowJar>("shadowJar").flatMap { it.archiveFile })
        archiveClassifier.set(project.name)
    }
}