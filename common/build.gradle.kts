import extensions.isSnapshot
import extensions.version
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/*
 *
 *  * Copyright (C) 2023 Cobblemon Contributors
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 */

plugins {
    id("cobblemon.base-conventions")
    id("cobblemon.publish-conventions")

    id("net.kyori.blossom")
    id("org.jetbrains.gradle.plugin.idea-ext")
    id ("net.nemerosa.versioning") version "2.8.2"
}

architectury {
    common("neoforge", "fabric")
}

repositories {
    maven(url = "${rootProject.projectDir}/deps")
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://maven.neoforged.net/releases")
    mavenLocal()
}

dependencies {
    implementation(libs.stdlib)
    implementation(libs.reflect)

    modImplementation(libs.fabricLoader)
    //Flywheel has no common dep so just pick one and don't use any platform specific code in common
    //modCompileOnly(libs.flywheelFabric)
    modApi(libs.molang)
    //modCompileOnly(libs.lambDynamicLights) { isTransitive = false }

    // For Showdown
    modCompileOnly(libs.graal)

    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'

    // For datastore
    modCompileOnly(libs.mongoDriverCore)
    modCompileOnly(libs.mongoDriverSync)

    testImplementation(libs.fabricJunit)
    testImplementation(libs.junitParams)
    testImplementation(libs.mockito)
    testImplementation(libs.mockk)
    testImplementation(libs.classgraph)
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        setEvents(listOf("failed"))
        setExceptionFormat("full")
    }
}

sourceSets {
    main {
        blossom {
            kotlinSources {
                fun generateLicenseHeader() : String {
                    val builder = StringBuilder()
                    builder.append("/*\n")
                    rootProject.file("HEADER").forEachLine {
                        if(it.isEmpty()) {
                            builder.append(" *").append("\n")
                        } else {
                            builder.append(" * ").append(it).append("\n")
                        }
                    }

                    return builder.append(" */").append("\n").toString()
                }

                property("license", generateLicenseHeader())
                property("modid", "cobblemon")
                property("version", project.version())
                property("isSnapshot", if(rootProject.isSnapshot()) "true" else "false")
                property("gitCommit", versioning.info.commit)
                property("branch", versioning.info.branch)
                System.getProperty("buildNumber")?.let { property("buildNumber", it) }
                property("timestamp", OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss")) + " UTC")
            }
        }
    }
}