architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    accessWidenerPath.set(project(":common").file("src/main/resources/pokemoncobbled-common.accesswidener"))

    forge {
        convertAccessWideners.set(true)
        mixinConfig("mixins.pokemoncobbled-common.json")
    }
}

dependencies {
    forge("net.minecraftforge:forge:${rootProject.property("mc_version")}-${rootProject.property("forge_version")}")
    modApi("dev.architectury:architectury-forge:${rootProject.property("architectury_version")}")

    // Kotlin
    forgeRuntimeLibrary(kotlin("stdlib-jdk8", version = "1.6.10"))
    forgeRuntimeLibrary(kotlin("reflect", version = "1.6.10"))
    forgeRuntimeLibrary(kotlin("stdlib", version = rootProject.property("kotlin_version").toString()))
    forgeRuntimeLibrary("org.jetbrains:annotations:${rootProject.property("annotations_version")}")
    forgeRuntimeLibrary("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:${rootProject.property("serialization_version")}")
    forgeRuntimeLibrary("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:${rootProject.property("serialization_version")}")

    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'

    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    "developmentForge"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    bundle(project(path = ":common", configuration = "transformProductionForge")) {
        isTransitive = false
    }
    testImplementation(project(":common", configuration = "namedElements"))
    // For Showdown
    forgeRuntimeLibrary("org.graalvm.js:js:22.0.0")
    forgeRuntimeLibrary("org.graalvm.js:js-scriptengine:22.0.0")

    bundle(kotlin("stdlib-jdk8", version = "1.6.10"))
    bundle(kotlin("reflect", version = "1.6.10"))
    bundle(kotlin("stdlib", version = rootProject.property("kotlin_version").toString()))
    bundle("org.jetbrains:annotations:${rootProject.property("annotations_version")}")
    bundle("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:${rootProject.property("serialization_version")}")
    bundle("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:${rootProject.property("serialization_version")}")



    bundle("org.graalvm.js:js:22.0.0")
    bundle("org.graalvm.js:js-scriptengine:22.0.0")
}

tasks {
    shadowJar {
        exclude("architectury-common.accessWidener")
    }
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand("version" to project.version)
        }
    }
}