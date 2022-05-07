package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.activestate.InactivePokemonState
import com.cablemc.pokemoncobbled.common.pokemon.activestate.PokemonState
import net.minecraft.network.PacketByteBuf

class PokemonStateUpdatePacket(): SingleUpdatePacket<PokemonState>(InactivePokemonState()) {
    constructor(pokemon: Pokemon, state: PokemonState): this() {
        setTarget(pokemon)
        value = state
    }
    override fun encodeValue(buffer: PacketByteBuf, value: PokemonState) { value.writeToBuffer(buffer) }
    override fun decodeValue(buffer: PacketByteBuf) = PokemonState.fromBuffer(buffer)
    override fun set(pokemon: Pokemon, value: PokemonState) { pokemon.state = value }
}