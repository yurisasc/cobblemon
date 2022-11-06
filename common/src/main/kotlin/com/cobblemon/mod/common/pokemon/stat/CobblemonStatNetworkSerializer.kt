/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.stat

import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.StatNetworkSerializer
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.net.IntSize
import com.cobblemon.mod.common.util.readSizedInt
import com.cobblemon.mod.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object CobblemonStatNetworkSerializer : StatNetworkSerializer {

    private val ordinalToStat = Stats.values().associateBy { it.ordinal }
    private val identifierToOrdinal = Stats.values().associate { it.identifier to it.ordinal }

    override fun decode(buffer: PacketByteBuf): Stat {
        val ordinal = buffer.readSizedInt(IntSize.U_BYTE)
        return this.ordinalLookup(ordinal)
    }

    override fun encode(buffer: PacketByteBuf, data: Stat) {
        val ordinal = this.identifierLookup(data.identifier)
        buffer.writeSizedInt(IntSize.U_BYTE, ordinal)
    }

    private fun ordinalLookup(ordinal: Int): Stat {
        return ordinalToStat[ordinal]
            ?: throw IllegalArgumentException("Cannot find the stat with the ordinal $ordinal, this should only happen if there is a custom Stat implementation but no BaseStatNetworkResolver to go alongside it")
    }

    private fun identifierLookup(identifier: Identifier): Int {
        return this.identifierToOrdinal[identifier]
            ?: throw IllegalArgumentException("Cannot find the stat to encode, this should only happen if there is a custom Stat implementation but no BaseStatNetworkResolver to go alongside it on the server side")
    }

}