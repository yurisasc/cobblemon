package com.cablemc.pokemoncobbled.common.api.types

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.text.Text
import net.minecraft.util.Identifier

/**
 * Class representing a type of a Pokemon or Move
 *
 * @param name: The English name used to load / find it (spaces -> _)
 * @param displayName: A Component used to display the name, normally a TranslatableText
 * @param textureXMultiplier: The multiplier by which the TypeWidget shall move the display
 * @param resourceLocation: The location of the resource used in the TypeWidget
 */
class ElementalType(
    val name: String,
    val displayName: Text,
    val hue: Int,
    val textureXMultiplier: Int,
    val resourceLocation: Identifier = Identifier(PokemonCobbled.MODID, "ui/types.png")
) {
}