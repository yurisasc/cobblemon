package com.cablemc.pokemoncobbled.common.api.pokemon.status

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.util.RandomPeriod
import net.minecraft.resources.ResourceLocation

/**
 * Represents the base of a status
 *
 * @author Deltric
 */
class Status(
    val name: ResourceLocation,
    val nonVolatile: Boolean,
    private val duration: RandomPeriod
) {
    /**
     * The random period that this status could last.
     * @return the random period of the status.
     */
    fun statusPeriod(): RandomPeriod {
        return PokemonCobbled.config.passiveStatuses[name.toString()] ?: duration
    }

    /**
     * The status's period as a config entry.
     * @return Status id with random period as a pair.
     */
    fun configEntry(): Pair<String, RandomPeriod> {
        return Pair(name.toString(), duration)
    }
}