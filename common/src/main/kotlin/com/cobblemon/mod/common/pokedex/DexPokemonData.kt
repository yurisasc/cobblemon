package com.cobblemon.mod.common.pokedex

import com.cobblemon.mod.common.api.net.Decodable
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

class DexPokemonData: Decodable, Encodable {
    var identifier = cobblemonResource("dex.pokemon")
    var formsOrderedList : MutableList<String> = mutableListOf()

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(identifier)
        buffer.writeInt(formsOrderedList.size)
        formsOrderedList.forEach {
            buffer.writeString(it)
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        identifier = buffer.readIdentifier()
        val formsOrderListSize = buffer.readInt()
        for(i in 0 until formsOrderListSize){
            formsOrderedList.add(buffer.readString())
        }
    }
}