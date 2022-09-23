plugins {
    id("pokemoncobbled.base-conventions")
}

architectury {
    common()
}

repositories {
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
}

dependencies {
    implementation(libs.stdlib)
    implementation(libs.reflect)

    modImplementation(libs.fabricLoader)
    modApi(libs.architectury)
    modApi(libs.molang)
    modApi(libs.mclib)

    // For Showdown
    modCompileOnly(libs.javet) // Linux or Windows
    modCompileOnly(libs.javetMac) // Mac OS (x86_64 Only)

    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'

    testRuntimeOnly(libs.junitEngine)
    testImplementation(libs.junitApi)
    testImplementation(libs.junitParams)
    testImplementation(libs.mockito)
    testImplementation(libs.mockk)

    // For Showdown
//    modCompileOnly 'com.caoccao.javet:javet:1.0.6' // Linux or Windows
//    modCompileOnly 'com.caoccao.javet:javet-macos:1.0.6' // Mac OS (x86_64 Only)
//    modCompileOnly group: 'commons-io', name: 'commons-io', version: '2.6'
}