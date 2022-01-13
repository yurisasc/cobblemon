package com.cablemc.pokemoncobbled.common.api.types

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

/**
 * Class representing a type of a Pokemon or Move
 *
 * @param name: The English name used to load / find it (spaces -> _)
 * @param displayName: A Component used to display the name, normally a TranslatableComponent
 * @param textureXMultiplier: The multiplier by which the TypeWidget shall move the display
 * @param resourceLocation: The location of the resource used in the TypeWidget
 */
class ElementalType(
    val name: String,
    val displayName: Component,
    val textureXMultiplier: Int,
    val resourceLocation: ResourceLocation = ResourceLocation(PokemonCobbled.MODID, "ui/types.png")
) {
}