package com.cobblemon.mod.common.api.pokemon.evolution.adapters

import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.mojang.serialization.Codec
import net.minecraft.util.Identifier

data class Variant<T>(
    val identifier: Identifier,
    val codec: Codec<out T>
)
