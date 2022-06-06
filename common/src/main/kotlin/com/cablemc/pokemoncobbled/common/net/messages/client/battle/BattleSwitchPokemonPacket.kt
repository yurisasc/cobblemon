package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import net.minecraft.network.PacketByteBuf

class BattleSwitchPokemonPacket() : NetworkPacket {
    lateinit var pnx: String
    lateinit var newPokemon: BattleInitializePacket.ActiveBattlePokemonDTO

    constructor(pnx: String, newPokemon: BattlePokemon): this() {
        this.pnx = pnx
        this.newPokemon = BattleInitializePacket.ActiveBattlePokemonDTO.fromPokemon(newPokemon)
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeString(pnx)
        newPokemon.saveToBuffer(buffer)
    }

    override fun decode(buffer: PacketByteBuf) {
        pnx = buffer.readString()
        newPokemon = BattleInitializePacket.ActiveBattlePokemonDTO.loadFromBuffer(buffer)
    }

}