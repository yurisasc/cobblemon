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
}

architectury {
    common("forge", "fabric")
}

repositories {
    maven(url = "${rootProject.projectDir}/deps")
    mavenLocal()
}

dependencies {
    implementation(libs.stdlib)
    implementation(libs.reflect)

    modImplementation(libs.fabricLoader)
    modApi(libs.molang)
    compileOnlyApi(libs.jeiApi)

    // For Showdown
    modCompileOnly(libs.graal)

    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'


    testRuntimeOnly(libs.junitEngine)
    testImplementation(libs.junitApi)
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
