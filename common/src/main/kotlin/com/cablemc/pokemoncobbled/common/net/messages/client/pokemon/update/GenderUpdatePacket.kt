package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.pokemon.Gender
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

class GenderUpdatePacket() : SingleUpdatePacket<Gender>(Gender.GENDERLESS) {
    constructor(pokemon: Pokemon, gender: Gender): this() {
        setTarget(pokemon)
        value = gender
    }

    override fun encodeValue(buffer: PacketByteBuf, value: Gender) {
        buffer.writeSizedInt(IntSize.U_BYTE, value.ordinal)
    }

    override fun decodeValue(buffer: PacketByteBuf): Gender {
        return Gender.values()[buffer.readSizedInt(IntSize.U_BYTE)]
    }

    override fun set(pokemon: Pokemon, value: Gender) {
        pokemon.gender = value
    }
}