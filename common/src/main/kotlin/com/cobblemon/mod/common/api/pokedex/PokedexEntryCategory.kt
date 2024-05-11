package com.cobblemon.mod.common.api.pokedex

enum class PokedexEntryCategory(name: String) {
    STARTER("starter"),
    STANDARD("standard"),
    PSEUDO_LEGEND("pseudo_legend"),
    SUB_LEGEND("sub_legend"),
    LEGEND("legend"),
    MYTHICAL("mythical");

    companion object {
        infix fun from(name: String): PokedexEntryCategory? = PokedexEntryCategory.values().firstOrNull { it.name == name }
    }
}