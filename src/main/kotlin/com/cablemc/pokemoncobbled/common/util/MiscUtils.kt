package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation

fun cobbledResource(path: String) = ResourceLocation(PokemonCobbled.MODID, path)

fun String.asTranslated(vararg data: Any) = TranslatableComponent(this, *data)
fun String.isInt() = this.toIntOrNull() != null