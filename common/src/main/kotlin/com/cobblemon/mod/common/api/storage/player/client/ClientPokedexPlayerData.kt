/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.storage.player.client

import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.cobblemon.mod.common.api.storage.pokedex.PokedexEntry
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.pokedex.DexStats
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * The client version of [PokedexPlayerData]
 * @author Apion
 * @since February 22, 2024
 */
class ClientPokedexPlayerData(
    val pokedex: MutableMap<Identifier, PokedexEntry>,
    val isIncrementalUpdate: Boolean = false,
) : ClientInstancedPlayerData(isIncrementalUpdate) {
    override fun encode(buf: PacketByteBuf) {
        val pokedexThatChanged = pokedex

        buf.writeInt(pokedexThatChanged.size)
        pokedexThatChanged.forEach {
            encodeEntry(buf, it.value)
        }
    }

    private fun encodeEntry(buf: PacketByteBuf, entry: PokedexEntry) {

        buf.writeIdentifier(entry.id)
        val entryMap = entry.progressMap
        buf.writeInt(entry.progressMap.size)
        entryMap.forEach {
            buf.writeString(it.key)
            val stats = it.value
            buf.writeByte(stats.numEncounteredWild.toInt())
            buf.writeByte(stats.numEncounteredBattle.toInt())
            buf.writeByte(stats.numCaught.toInt())
        }
    }

    companion object {
        fun decode(buf: PacketByteBuf) : SetClientPlayerDataPacket {
            val numEntries = buf.readInt()
            val entrySet = HashSet<PokedexEntry>()
            for (i in 1..numEntries) {
                entrySet.add(decodeEntry(buf))
            }
            return SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, ClientPokedexPlayerData(
                 entrySet.associateBy {it.id}.toMutableMap()
            ))
        }

        private fun decodeEntry(buf: PacketByteBuf): PokedexEntry {
            val species = Identifier.tryParse(buf.readString())!!
            val map = mutableMapOf<String, DexStats>()
            val numForms = buf.readInt()
            for (i in 0 until numForms) {
                val id = buf.readString()
                val numWild = buf.readByte()
                val numBattle = buf.readByte()
                val numCaught = buf.readByte()
                map[id] = DexStats(numWild, numBattle, numCaught)
            }
            return PokedexEntry(species, map)
        }

        fun afterDecodeAction(data: ClientInstancedPlayerData) {
            if (data !is ClientPokedexPlayerData) return
            CobblemonClient.clientPokedexData = data
        }

        fun incrementalAfterDecodeAction(data: ClientInstancedPlayerData) {
            if (data !is ClientPokedexPlayerData) return
            data.pokedex.forEach {
                CobblemonClient.clientPokedexData.pokedex[it.key] = it.value
            }
        }
    }
}