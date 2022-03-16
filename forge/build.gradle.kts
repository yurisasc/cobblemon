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

val kotlin_version: String by project
val annotations_version: String by project
val coroutines_version: String by project
val serialization_version: String by project

dependencies {
    forge("net.minecraftforge:forge:${rootProject.property("mc_version")}-${rootProject.property("forge_version")}")
    modApi("dev.architectury:architectury-forge:${rootProject.property("architectury_version")}")

    // Kotlin
    forgeRuntimeLibrary(kotlin("stdlib-jdk8", version = "1.6.10"))
    forgeRuntimeLibrary(kotlin("reflect", version = "1.6.10"))
    forgeRuntimeLibrary(kotlin("stdlib", version = kotlin_version))
    forgeRuntimeLibrary("org.jetbrains:annotations:${annotations_version}")
    forgeRuntimeLibrary("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:${serialization_version}")
    forgeRuntimeLibrary("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:${serialization_version}")

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

    // For Showdown
    bundle("com.caoccao.javet:javet:1.0.6") // Linux or Windows
    bundle("com.caoccao.javet:javet-macos:1.0.6") // Mac OS (x86_64 Only)


    // Testing - It needs this!
//    forgeRuntimeLibrary("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")

    // For Tests
//    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.2")
//    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.2")
//    testImplementation("org.mockito:mockito-core:3.3.3")
//    testImplementation("io.mockk:mockk:1.12.1")
    //    testImplementation(project(":common", configuration = "namedElements"))
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