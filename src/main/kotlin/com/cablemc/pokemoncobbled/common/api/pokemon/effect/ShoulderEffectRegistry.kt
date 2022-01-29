package com.cablemc.pokemoncobbled.common.api.pokemon.effect

import com.cablemc.pokemoncobbled.common.pokemon.effects.LightSourceEffect
import com.cablemc.pokemoncobbled.common.pokemon.effects.SlowFallEffect

/**
 * Registry object for ShoulderEffects
 *
 * @author Qu
 * @since 2022-01-26
 */
object ShoulderEffectRegistry {
    private val effects = mutableMapOf<String, Class<out ShoulderEffect>>()

    // Effects - START
    val LIGHT_SOURCE = register("light_source", LightSourceEffect::class.java)
    val SLOW_FALL = register("slow_fall", SlowFallEffect::class.java)
    // Effects - END

    fun register(name: String, effect: Class<out ShoulderEffect>): Class<out ShoulderEffect> {
        return effect.also {
            effects[name] = it
        }
    }

    fun unregister(name: String) = effects.remove(name)

    fun getName(clazz: Class<out ShoulderEffect>): String {
        return effects.firstNotNullOf { if (it.value == clazz) it.key else null }
    }

    fun get(name: String): Class<out ShoulderEffect>? {
        return effects[name]
    }
}