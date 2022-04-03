package com.cablemc.pokemoncobbled.common.pokemon.status

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokemon.status.Status
import net.minecraft.resources.ResourceLocation

/**
 * Represents a status that persists outside of battle.
 *
 * @author Deltric
 */
class PersistentStatus(
    name: ResourceLocation,
    private val duration: IntRange = IntRange(0, 0)
) : Status(name) {
    /**
     * The random period that this status could last.
     * @return the random period of the status.
     */
    fun statusPeriod(): IntRange {
        return PokemonCobbled.config.passiveStatuses[name.toString()] ?: duration
    }

    /**
     * The status's period as a config entry.
     * @return Status id with random period as a pair.
     */
    fun configEntry(): Pair<String, IntRange> {
        return name.toString() to duration
    }
}