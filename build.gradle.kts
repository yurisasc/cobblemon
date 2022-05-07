import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    kotlin("jvm") version "1.6.0" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "0.11.0-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

architectury {
    minecraft = project.property("mc_version").toString()
}

group = "com.cablemc.pokemoncobbled"
version = "${project.property("mod_version")}+${project.property("mc_version")}"
base.archivesName.set("PokemonCobbled")

tasks {
    val collectJars by registering(Copy::class) {
        val tasks = subprojects.filter { it.path != ":common" }.map { it.tasks.named("remapJar") }
        dependsOn(tasks)

        from(tasks)
        into(buildDir.resolve("libs"))
    }

    assemble {
        dependsOn(collectJars)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")

    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    group = rootProject.group
    version = rootProject.version
    base.archivesName.set(rootProject.base.archivesName)
    val loom = project.extensions.getByName<net.fabricmc.loom.api.LoomGradleExtensionAPI>("loom")
    loom.silentMojangMappingsLicense()
    loom.accessWidenerPath.set(project(":common").file("src/main/resources/pokemoncobbled-common.accesswidener"))

    dependencies {
        "minecraft"("net.minecraft:minecraft:${rootProject.property("mc_version")}")
        "mappings"("net.fabricmc:yarn:1.18.2+build.2:v2")//(loom.officialMojangMappings())
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(17)
        }

        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "17"
        }

        "jar"(Jar::class) {
            from(rootProject.file("LICENSE"))
        }
    }
}

subprojects {
    if (path != ":common") {
        apply(plugin = "com.github.johnrengelman.shadow")

        val bundle by configurations.creating {
            isCanBeConsumed = false
            isCanBeResolved = true
        }

        tasks {
            "jar"(Jar::class) {
                archiveClassifier.set("dev-slim")
            }

            "shadowJar"(ShadowJar::class) {
                archiveClassifier.set("dev-shadow")
                configurations = listOf(bundle)
            }

            "remapJar"(RemapJarTask::class) {
                dependsOn("shadowJar")
                inputFile.set(named<ShadowJar>("shadowJar").flatMap { it.archiveFile })
                archiveClassifier.set(project.name)
            }
        }
    }
}
