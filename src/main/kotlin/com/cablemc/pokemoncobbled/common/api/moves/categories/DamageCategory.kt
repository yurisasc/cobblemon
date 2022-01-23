package com.cablemc.pokemoncobbled.common.api.moves.categories

import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

/**
 * Representing a DamageCategory from the Pokemon Game
 *
 * @param name: The English name used to load / find it (spaces -> _)
 * @param displayName: A Component used to display the name, normally a TranslatableComponent
 * @param resourceLocation: The location of the resource used in the GUI
 */
class DamageCategory(
    val name: String,
    val displayName: Component,
    val textureXMultiplier: Int,
    val resourceLocation: ResourceLocation = cobbledResource("ui/categories.png")
) {
}