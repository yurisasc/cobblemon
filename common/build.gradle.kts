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
}
