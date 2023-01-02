plugins {
    id("cobblemon.base-conventions")
    id("maven-publish")
}

architectury {
    common()
}

repositories {
    maven(url = "${rootProject.projectDir}/deps")
    mavenLocal()
}

dependencies {
    implementation(libs.stdlib)
    implementation(libs.reflect)

    modImplementation(libs.fabricLoader)
    modApi(libs.architectury)
    modApi(libs.molang)

    // For Showdown
    modCompileOnly(libs.graal)

    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'


    testRuntimeOnly(libs.junitEngine)
    testImplementation(libs.junitApi)
    testImplementation(libs.junitParams)
    testImplementation(libs.mockito)
    testImplementation(libs.mockk)

    compileOnly("net.luckperms:api:${rootProject.property("luckperms_version")}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven("https://maven.impactdev.net/repository/development/") {
            name = "ImpactDev-Public"
            credentials {
                username = System.getenv("NEXUS_USER")
                password = System.getenv("NEXUS_PW")
            }
        }
    }

    publications {
        create<MavenPublication>("mod") {
            from(components["java"])
            groupId = "com.cobblemon"
            artifactId = "mod"
            version = rootProject.version.toString()
        }
    }
}