package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.activestate.InactivePokemonState
import com.cablemc.pokemoncobbled.common.pokemon.activestate.PokemonState
import net.minecraft.network.FriendlyByteBuf

class PokemonStateUpdatePacket(): SingleUpdatePacket<PokemonState>(InactivePokemonState()) {
    constructor(state: PokemonState): this() {
        value = state
    }

    override fun encodeValue(buffer: FriendlyByteBuf, value: PokemonState) {
        value.writeToBuffer(buffer)
    }

    override fun decodeValue(buffer: FriendlyByteBuf): PokemonState {
        return PokemonState.fromBuffer(buffer)
    }

    override fun set(pokemon: Pokemon, value: PokemonState) {
        pokemon.state = value
    }
}