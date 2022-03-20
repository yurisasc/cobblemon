architectury {
    platformSetupLoomIde()
    fabric()
}

val generatedResources = file("src/generated/resources")
val accessWidenerFile = project(":common").file("src/main/resources/pokemoncobbled-common.accesswidener")

loom {
    accessWidenerPath.set(accessWidenerFile)
}

sourceSets {
    main {
        ext.set("refmap", "PokemonCobbled-fabric-refmap.json")
        resources {
            srcDir(generatedResources)
        }
    }
}

val kotlin_version: String by project
val annotations_version: String by project
val coroutines_version: String by project
val serialization_version: String by project

dependencies {
    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    "developmentFabric"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    bundle(project(path = ":common", configuration = "transformProductionFabric")) {
        isTransitive = false
    }

    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")
    modApi("dev.architectury:architectury-fabric:${rootProject.property("architectury_version")}")
    modApi("net.fabricmc:fabric-language-kotlin:${rootProject.property("fabric_kotlin")}")

    // For Kotlin
    bundle(kotlin("stdlib-jdk8", version = "1.6.10"))
    bundle(kotlin("reflect", version = "1.6.10"))
    bundle(kotlin("stdlib", version = rootProject.property("kotlin_version").toString()))
    bundle("org.jetbrains:annotations:${rootProject.property("annotations_version")}")
    bundle("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:${rootProject.property("serialization_version")}")
    bundle("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:${rootProject.property("serialization_version")}")

    // For Showdown
    bundle("com.caoccao.javet:javet:1.0.6") // Linux or Windows
    bundle("com.caoccao.javet:javet-macos:1.0.6") // Mac OS (x86_64 Only)
//    common group: 'commons-io', name: 'commons-io', version: '2.6'
}

tasks {
    // The AW file is needed in :fabric project resources when the game is run.
    val copyAccessWidener by registering(Copy::class) {
        from(accessWidenerFile)
        into(generatedResources)
    }

    shadowJar {}

    processResources {
        dependsOn(copyAccessWidener)
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
}