plugins {
    id("pokemoncobbled.base-conventions")
    id("com.github.johnrengelman.shadow")
}

val bundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

tasks {

    jar {
        archiveBaseName.set("PokemonCobbled-${project.name}")
        archiveClassifier.set("dev-slim")
    }

    shadowJar {
        archiveClassifier.set("dev-shadow")
        archiveBaseName.set("PokemonCobbled-${project.name}")
        configurations = listOf(bundle)
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        archiveBaseName.set("PokemonCobbled-${project.name}")
        archiveClassifier.set("${rootProject.version}-remap")
    }

}