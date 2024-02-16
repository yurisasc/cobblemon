plugins {
    base
    id("cobblemon.root-conventions")
    id ("net.nemerosa.versioning") version "2.8.2"
}

group = "com.cobblemon.mod"
version = "${project.property("mod_version")}+${project.property("mc_version")}"

val isSnapshot = project.property("snapshot")?.equals("true") ?: false
if (isSnapshot) {
    version = "$version-SNAPSHOT-${versioning.info.branch}-${versioning.info.build}"
}