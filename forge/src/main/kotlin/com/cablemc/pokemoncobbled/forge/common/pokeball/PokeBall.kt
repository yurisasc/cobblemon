package com.cablemc.pokemoncobbled.forge.common.pokeball

import com.cablemc.pokemoncobbled.forge.common.api.pokeball.catching.CatchRateModifier
import net.minecraft.resources.ResourceLocation

/**
 * Base poke ball object
 * It is intended that there is one poke ball object initialized for a given poke ball type.
 *
 * @property name the poke ball registry name
 * @property catchRateModifiers list of all [CatchRateModifier] that is applicable to the poke ball
 */
open class PokeBall(
    val name : ResourceLocation,
    val catchRateModifiers: List<CatchRateModifier> = listOf()
)