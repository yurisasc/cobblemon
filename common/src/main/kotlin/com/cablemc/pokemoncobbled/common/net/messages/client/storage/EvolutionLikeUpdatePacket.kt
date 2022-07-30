package com.cablemc.pokemoncobbled.common.net.messages.client.storage

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionLike
import com.cablemc.pokemoncobbled.common.net.messages.client.PokemonUpdatePacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.PacketByteBuf

/**
 * The base of all evolution related updates.
 *
 * @param C The type of [EvolutionLike] on the current side.
 * @param S The type of [EvolutionLike] being sent to the other side.
 *
 * @author Licious
 * @since April 28th, 2022.
 */
abstract class EvolutionLikeUpdatePacket<C : EvolutionLike, S : EvolutionLike> : PokemonUpdatePacket() {

    abstract var current: C
    abstract var sending: S

    /**
     * Creates the [S] expected from the [C].
     *
     * @param pokemon The [Pokemon] being affected.
     * @return The resulting [EvolutionDisplay].
     */
    protected abstract fun createSending(pokemon: Pokemon): S

    final override fun encode(buffer: PacketByteBuf) {
        super.encode(buffer)
        this.encodeSending(buffer)
    }

    final override fun decode(buffer: PacketByteBuf) {
        super.decode(buffer)
        this.decodeSending(buffer)
    }

    abstract fun encodeSending(buffer: PacketByteBuf)

    abstract fun decodeSending(buffer: PacketByteBuf)

}