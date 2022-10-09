
plugins {
    base
    id("pokemod.root-conventions")
}

group = "com.cablemc.pokemod"
version = "${project.property("mod_version")}+${project.property("mc_version")}"