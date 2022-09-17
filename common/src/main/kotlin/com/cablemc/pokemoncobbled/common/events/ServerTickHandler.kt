/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.events

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.storage.party.PlayerPartyStore
import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.util.party
import net.minecraft.server.MinecraftServer

object ServerTickHandler {
    private var secondsTick = 0

    fun onTick(server: MinecraftServer) {
        PokemonCobbled.bestSpawner.spawnerManagers.forEach { it.onServerTick() }
        BattleRegistry.tick()

        secondsTick++

        if (secondsTick == 20) {
            secondsTick = 0

            // Party tick
            for (player in server.playerManager.playerList) {
                player.party().onSecondPassed(player)
            }
        }
    }
}