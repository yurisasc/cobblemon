package com.cobblemon.mod.common.config.pokedex

import com.cobblemon.mod.common.Cobblemon
import net.minecraft.util.Identifier

data class PokedexCategory(
    val id : Identifier,
    val childrenDex : List<Identifier>
)

data class PokedexEntryCategory(
    val id : Identifier,
    val pokedexId : Identifier = Identifier(Cobblemon.MODID, "Pokedex.Unknown"),
    val forms : List<String> = mutableListOf("normal")
)