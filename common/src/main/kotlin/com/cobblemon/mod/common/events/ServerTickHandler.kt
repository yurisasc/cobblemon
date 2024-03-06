/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.events

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.tms.TechnicalMachines
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.item.TechnicalMachineItem
import com.cobblemon.mod.common.util.party
import net.minecraft.server.MinecraftServer

object ServerTickHandler {
    private var secondsTick = 0

    fun onTick(server: MinecraftServer) {
        Cobblemon.bestSpawner.spawnerManagers.forEach { it.onServerTick() }
        BattleRegistry.tick()

        secondsTick++

        if (secondsTick == 20) {
            secondsTick = 0

            // Code to run every in-game second
            for (player in server.playerManager.playerList) {
                player.party().onSecondPassed(player)
                TechnicalMachines.checkPassives(player)

                // Give player tm unlocks for tms they don't already have
                repeat(player.inventory.size()) {
                    val stack = player.inventory.getStack(it)
                    if (stack.item is TechnicalMachineItem) {
                        val tm = TechnicalMachineItem.getMoveNbt(stack) ?: return@repeat
                        if (!Cobblemon.playerData.get(player).tmSet.contains(tm.id())) {
                            tm.unlock(player)
                        }
                    }
                }
            }
        }
    }
}