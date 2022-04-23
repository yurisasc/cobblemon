package com.cablemc.pokemoncobbled.common.pokemon.status

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokemon.status.Status
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import kotlin.random.Random

/**
 * Represents a status that persists outside of battle.
 *
 * @author Deltric
 */
class PersistentStatus(
    name: ResourceLocation,
    private val defaultDuration: IntRange = 0..0
) : Status(name) {
    /**
     * Called when a status duration is expired.
     */
    fun onStatusExpire(player: ServerPlayer, pokemon: Pokemon, random: Random) {

    }

    /**
     * Called every second on the Pokémon for the status
     */
    fun onStatusTick(player: ServerPlayer, pokemon: Pokemon, random: Random) {

    }

    /**
     * The random period that this status could last.
     * @return the random period of the status.
     */
    fun statusPeriod(): IntRange {
        return PokemonCobbled.config.passiveStatuses[name.toString()] ?: defaultDuration
    }

    /**
     * The status's period as a config entry.
     * @return Status id with random period as a pair.
     */
    fun configEntry(): Pair<String, IntRange> {
        return name.toString() to defaultDuration
    }
}