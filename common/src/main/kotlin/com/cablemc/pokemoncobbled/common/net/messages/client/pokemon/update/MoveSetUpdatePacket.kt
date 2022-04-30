package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.api.moves.MoveSet
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.PacketByteBuf

class MoveSetUpdatePacket internal constructor(): SingleUpdatePacket<MoveSet>(MoveSet()) {

    constructor(
        pokemon: Pokemon,
        moveSet: MoveSet
    ): this() {
        setTarget(pokemon)
        value = moveSet
    }

    override fun encodeValue(buffer: PacketByteBuf, value: MoveSet) {
        value.saveToBuffer(buffer)
    }

    override fun decodeValue(buffer: PacketByteBuf): MoveSet {
        return MoveSet().loadFromBuffer(buffer)
    }

    override fun set(pokemon: Pokemon, value: MoveSet) {
        pokemon.moveSet.copyFrom(value)
    }
}