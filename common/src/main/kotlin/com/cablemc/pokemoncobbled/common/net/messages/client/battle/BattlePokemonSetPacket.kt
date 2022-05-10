package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.PacketByteBuf

class BattlePokemonSetPacket() : NetworkPacket {
    lateinit var pokemon: Pokemon

    constructor(pokemon: Pokemon): this() {
        this.pokemon = pokemon
    }

    override fun encode(buffer: PacketByteBuf) {
        pokemon.saveToBuffer(buffer)
    }

    override fun decode(buffer: PacketByteBuf) {
        pokemon = Pokemon().loadFromBuffer(buffer)
    }
}