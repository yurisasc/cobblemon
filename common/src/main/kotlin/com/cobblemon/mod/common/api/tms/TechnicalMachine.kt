/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.tms

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.util.lang
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class TechnicalMachine(
    val moveName: String,
    val recipe: TechnicalMachineRecipe,
    val obtainMethods: List<ObtainMethod>
) {

    /**
     * Gets the [Identifier] of this [TechnicalMachine]
     *
     * @return The [Identifier] of this [TechnicalMachine] (or null if not found)
     * @author whatsy
     */
    fun id(): Identifier? {
        TechnicalMachines.tmMap.forEach {
            if (TechnicalMachines.tmMap[it.key] == this) return it.key
        }
        return null
    }

    /**
     * Unlocks this [TechnicalMachine] for the player.
     *
     * @param player The [ServerPlayerEntity] to give this [TechnicalMachine] to.
     * @return Whether the player was successfully granted the [TechnicalMachine]
     * @author whatsy
     */
    fun unlock(player: ServerPlayerEntity): Boolean {
        if (id() == null) return false
        Cobblemon.playerData.get(player).tmSet.add(id()!!)
        player.sendMessage(lang("tms.unlock_tm", lang("move.$moveName")))
        return true
    }
}
