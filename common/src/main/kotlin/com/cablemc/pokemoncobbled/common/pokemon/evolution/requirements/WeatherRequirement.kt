package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import net.minecraft.entity.LivingEntity

class WeatherRequirement : EntityQueryRequirement {
    companion object {
        const val ADAPTER_VARIANT = "weather"
    }
    val isRaining: Boolean? = null
    val isThundering: Boolean? = null

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity): Boolean {
        val world = queriedEntity.world
        return when {
            isRaining == true && !world.isRaining -> false
            isRaining == false && world.isRaining -> false
            isThundering == true && !world.isThundering -> false
            isThundering == false && world.isThundering -> false
            else -> true
        }
    }
}