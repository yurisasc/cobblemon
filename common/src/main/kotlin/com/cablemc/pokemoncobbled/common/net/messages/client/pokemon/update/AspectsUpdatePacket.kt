package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

class AspectsUpdatePacket() : SingleUpdatePacket<Set<String>>(emptySet()) {
    constructor(pokemon: Pokemon, aspects: Set<String>): this() {
        setTarget(pokemon)
        value = aspects
    }

    override fun encodeValue(buffer: PacketByteBuf, value: Set<String>) {
        buffer.writeSizedInt(IntSize.U_BYTE, value.size)
        value.forEach { buffer.writeString(it) }
    }

    override fun decodeValue(buffer: PacketByteBuf): Set<String> {
        val aspects = mutableSetOf<String>()
        repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
            aspects.add(buffer.readString())
        }
        return aspects
    }

    override fun set(pokemon: Pokemon, value: Set<String>) {
        pokemon.aspects = value
    }
}