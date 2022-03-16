architectury {
    common()
}

loom {
    accessWidenerPath.set(file("src/main/resources/pokemoncobbled-common.accesswidener"))
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    modApi("dev.architectury:architectury:${rootProject.property("architectury_version")}")

    // For Showdown
    modCompileOnly("com.caoccao.javet:javet:1.0.6") // Linux or Windows
    modCompileOnly("com.caoccao.javet:javet-macos:1.0.6") // Mac OS (x86_64 Only)
    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.2")
    testImplementation("org.mockito:mockito-core:3.3.3")
    testImplementation("io.mockk:mockk:1.12.1")

    // For Showdown
//    modCompileOnly 'com.caoccao.javet:javet:1.0.6' // Linux or Windows
//    modCompileOnly 'com.caoccao.javet:javet-macos:1.0.6' // Mac OS (x86_64 Only)
//    modCompileOnly group: 'commons-io', name: 'commons-io', version: '2.6'
}