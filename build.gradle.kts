plugins {
    base
    id("cobblemon.root-conventions")
    id ("net.nemerosa.versioning") version "3.1.0"
}

group = "com.cobblemon.mod"
version = "${project.property("mod_version")}+${project.property("mc_version")}"

val isSnapshot = project.property("snapshot")?.equals("true") ?: false
if (isSnapshot) {
    version = "$version-${versioning.info.branch}-${versioning.info.build}"
}
