/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.pokemon.status

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokemon.status.Status
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.network.ServerPlayerEntity
import kotlin.random.Random
import net.minecraft.util.Identifier

/**
 * Represents a status that persists outside of battle.
 *
 * @author Deltric
 */
open class PersistentStatus(
    name: Identifier,
    showdownName: String = "",
    private val defaultDuration: IntRange = 0..0
) : Status(name) {
    /**
     * Called when a status duration is expired.
     */
    open fun onStatusExpire(player: ServerPlayerEntity, pokemon: Pokemon, random: Random) {

    }

    /**
     * Called every second on the Pokémon for the status
     */
    open fun onSecondPassed(player: ServerPlayerEntity, pokemon: Pokemon, random: Random) {

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