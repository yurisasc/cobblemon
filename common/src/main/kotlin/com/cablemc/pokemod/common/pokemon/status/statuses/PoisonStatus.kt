/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.status.statuses

import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.pokemon.status.PersistentStatus
import com.cablemc.pokemod.common.util.pokemodResource
import kotlin.math.max
import kotlin.math.round
import kotlin.random.Random
import net.minecraft.server.network.ServerPlayerEntity
class PoisonStatus : PersistentStatus(
    name = pokemodResource("poison"),
    showdownName = "psn",
    applyMessage = "pokemod.status.poison.apply",
    removeMessage = null,
    defaultDuration = IntRange(180, 300)
) {
    override fun onSecondPassed(player: ServerPlayerEntity, pokemon: Pokemon, random: Random) {
        // 1 in 15 chance to damage 5% of their HP with a minimum of 1
        if (!pokemon.isFainted() && random.nextInt(15) == 0) {
            pokemon.currentHealth -= max(1, round(pokemon.hp * 0.05).toInt())
        }
    }
}