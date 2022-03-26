architectury {
    common()
}

loom {
    accessWidenerPath.set(file("src/main/resources/pokemoncobbled-common.accesswidener"))
}

sourceSets {
    main {
        ext.set("refmap", "PokemonCobbled-common-refmap.json")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    modApi("dev.architectury:architectury:${rootProject.property("architectury_version")}")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.2")
    testImplementation("org.mockito:mockito-core:3.3.3")
    testImplementation("io.mockk:mockk:1.12.1")

    // For Showdown
    modCompileOnly("org.graalvm.js:js:22.0.0")
    modCompileOnly("org.graalvm.js:js-scriptengine:22.0.0")
}