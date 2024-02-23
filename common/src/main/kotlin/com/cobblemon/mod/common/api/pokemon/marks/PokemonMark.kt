package com.cobblemon.mod.common.api.pokemon.marks

import net.minecraft.util.Identifier

class PokemonMark(
    id: Identifier,
    val title: String,
    val icon: Identifier
) {
    @Transient
    var identifier = id
        internal set
}