package com.cobblemon.mod.common.api.pokemon.marks

import com.google.gson.annotations.SerializedName
import net.minecraft.util.Identifier

class PokemonMark(
    id: Identifier,
    val name: String,
    val title: String,
    val icon: Identifier
) {
    @Transient
    var identifier = id
        internal set
}