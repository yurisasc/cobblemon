package com.cablemc.pokemoncobbled.common.api.pokemon.effect

/**
 * Registry object for ShoulderEffects
 *
 * @author Qu
 * @since 2022-01-26
 */
object ShoulderEffectRegistry {
    private val effects = mutableListOf<ShoulderEffect>()

    // Effects - START
    val LIGHT_SOURCE = register(LightSourceEffect)
    // Effects - END

    fun register(effect: ShoulderEffect): ShoulderEffect {
        return effect.also {
            effects.add(it)
        }
    }

    fun get(name: String): ShoulderEffect? {
        return effects.find { it.name.equals(name, ignoreCase = true) }
    }
}