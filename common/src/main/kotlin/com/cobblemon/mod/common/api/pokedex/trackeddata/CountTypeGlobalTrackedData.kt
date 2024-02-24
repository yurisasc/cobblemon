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

class CountTypeGlobalTrackedData(
    val type: String
) : GlobalTrackedData() {
    @Transient
    override val triggerEvents = setOf(EventTriggerType.CAUGHT)
    var numOfType = 0

    override fun onCatch(event: PokemonCapturedEvent): Boolean {
        if (event.pokemon.species.types.any { it.name == type }) {
            numOfType++
            return true
        }
        return false
    }

    override fun clone(): GlobalTrackedData {
        val result = CountTypeGlobalTrackedData(type)
        result.numOfType = numOfType
        return result
    }

    override fun encode(buf: PacketByteBuf) {
        buf.writeString(type)
        buf.writeInt(numOfType)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is CountTypeGlobalTrackedData) return false
        return other.type == type
    }

    companion object {
        val ID = cobblemonResource("count_type")

        fun decode(buf: PacketByteBuf): CountTypeGlobalTrackedData {
            val type = buf.readString()
            val numOfType = buf.readInt()
            val result = CountTypeGlobalTrackedData(type)
            result.numOfType = numOfType
            return result
        }
    }
}