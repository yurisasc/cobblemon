package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.api.moves.BenchedMoves
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.FriendlyByteBuf

class BenchedMovesUpdatePacket() : SingleUpdatePacket<BenchedMoves>(BenchedMoves()) {
    constructor(pokemon: Pokemon, value: BenchedMoves) : this() {
        setTarget(pokemon)
        this.value = value
    }

    override fun encodeValue(buffer: FriendlyByteBuf, value: BenchedMoves) {
        value.saveToBuffer(buffer)
    }

    override fun decodeValue(buffer: FriendlyByteBuf) = value.loadFromBuffer(buffer)

    override fun set(pokemon: Pokemon, value: BenchedMoves) {
        pokemon.benchedMoves.doThenEmit {
            pokemon.benchedMoves.clear()
            pokemon.benchedMoves.addAll(value)
        }
    }
}