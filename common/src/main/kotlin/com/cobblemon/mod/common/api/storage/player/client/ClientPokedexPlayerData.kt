/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.storage.player.client

import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.net.messages.client.starter.SetClientPlayerDataPacket
import com.cobblemon.mod.common.pokedex.DexStats
import com.cobblemon.mod.common.pokedex.PokedexEntry
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * The client version of [PokedexPlayerData]
 * @author Apion
 * @since February 22, 2024
 */
class ClientPokedexPlayerData(
    val pokedex: Collection<PokedexEntry>
) : ClientInstancedPlayerData() {
    override fun encode(buf: PacketByteBuf) {
        buf.writeInt(pokedex.size)
        pokedex.forEach {
            encodeEntry(buf, it)
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
            buf.writeEnumConstant(stats.knowledge)
        }
    }

    companion object {
        fun decode(buf: PacketByteBuf) : SetClientPlayerDataPacket {
            val numEntries = buf.readInt()
            val entrySet = HashSet<PokedexEntry>()
            for (i in 1..numEntries) {
                entrySet.add(decodeEntry(buf))
            }
            return SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, ClientPokedexPlayerData(entrySet))
        }

        private fun decodeEntry(buf: PacketByteBuf): PokedexEntry {
            val species = Identifier.tryParse(buf.readString())!!
            val map = mutableMapOf<String, DexStats>()
            val numForms = buf.readInt()
            for (i in 1..numForms) {
                val id = buf.readString()
                val numWild = buf.readByte()
                val numBattle = buf.readByte()
                val numCaught = buf.readByte()
                val knowledge = buf.readEnumConstant(DexStats.Knowledge::class.java)
                map[id] = DexStats(numWild, numBattle, numCaught, knowledge)
            }
            return PokedexEntry(species, map)
        }

        fun runAction(data: ClientInstancedPlayerData) {
            if (data !is ClientPokedexPlayerData) return
            CobblemonClient.clientPokedexData = data
        }
    }
}