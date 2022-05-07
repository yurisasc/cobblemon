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

repositories {
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
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
    forgeRuntimeLibrary("com.caoccao.javet:javet:1.1.3") // Linux or Windows
    forgeRuntimeLibrary("com.caoccao.javet:javet-macos:1.1.0") // Mac OS (x86_64 Only)
    forgeRuntimeLibrary("com.eliotlash.molang:molang:18")
    forgeRuntimeLibrary("com.eliotlash.mclib:mclib:18")

    bundle(kotlin("stdlib-jdk8", version = "1.6.10"))
    bundle(kotlin("reflect", version = "1.6.10"))
    bundle(kotlin("stdlib", version = rootProject.property("kotlin_version").toString()))
    bundle("org.jetbrains:annotations:${rootProject.property("annotations_version")}")
    bundle("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:${rootProject.property("serialization_version")}")
    bundle("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:${rootProject.property("serialization_version")}")
    bundle("com.caoccao.javet:javet:1.1.3") // Linux or Windows
    bundle("com.caoccao.javet:javet-macos:1.1.0") // Mac OS (x86_64 Only)
    bundle("com.eliotlash.molang:molang:18")
    bundle("com.eliotlash.mclib:mclib:18")

    // Testing - It needs this!
//    forgeRuntimeLibrary("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")

    //
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

//jar {
//    classifier("dev")
//    manifest {
//        attributes(
//                "Specification-Title" to rootProject.mod_id,
//                "Specification-Vendor" to "Cable MC",
//                "Specification-Version" to "1",
//                "Implementation-Title" to rootProject.mod_id,
//                "Implementation-Version" to project.version,
//                "Implementation-Vendor" to "Cable MC",
//        )
//    }
//}
//
//sourcesJar {
//    def commonSources = project(":common").sourcesJar
//    dependsOn commonSources
//    from commonSources.archiveFile.map { zipTree(it) }
//}
//
//components.java {
//    withVariantsFromConfiguration(project.configurations.shadowRuntimeElements) {
//        skip()
//    }
//}