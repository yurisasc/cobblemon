/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokedex.trackeddata

import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

class CountTypeCaughtGlobalTrackedData(
    val type: String
) : GlobalTrackedData() {
    @Transient
    override val triggerEvents = setOf(EventTriggerType.CAUGHT, EventTriggerType.EVOLVE, EventTriggerType.TRADE)
    var numOfType = 0

    override fun onCatch(event: PokemonCapturedEvent): Boolean {
        if (event.pokemon.species.types.any { it.name == type }) {
            numOfType++
            return true
        }
        return false
    }

    override fun clone(): GlobalTrackedData {
        val result = CountTypeCaughtGlobalTrackedData(type)
        result.numOfType = numOfType
        return result
    }

    override fun encode(buf: PacketByteBuf) {
        buf.writeString(type)
        buf.writeInt(numOfType)
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is CountTypeCaughtGlobalTrackedData) return false
        return other == type
    }

    companion object {
        val ID = cobblemonResource("count_type")

        fun decode(buf: PacketByteBuf): CountTypeCaughtGlobalTrackedData {
            val type = buf.readString()
            val numOfType = buf.readInt()
            val result = CountTypeCaughtGlobalTrackedData(type)
            result.numOfType = numOfType
            return result
        }
    }
}