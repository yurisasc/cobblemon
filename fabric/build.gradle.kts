plugins {
    id("pokemoncobbled.platform-conventions")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

val generatedResources = file("src/generated/resources")

sourceSets {
    main {
        resources {
            srcDir(generatedResources)
        }
    }
}

repositories {
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
}

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

    modApi(libs.fabricApi)
    modApi(libs.fabricKotlin)
    modApi(libs.architecturyFabric)

    listOf(
        libs.javet,
        libs.javetMac,
        libs.molang,
        libs.mclib
    ).forEach {
        bundle(it)
        runtimeOnly(it)
    }

}

tasks {
    // The AW file is needed in :fabric project resources when the game is run.
    val copyAccessWidener by registering(Copy::class) {
        from(ACCESS_WIDENER)
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