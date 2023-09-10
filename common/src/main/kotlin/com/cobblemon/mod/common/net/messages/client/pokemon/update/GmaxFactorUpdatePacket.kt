package com.cobblemon.mod.common.net.messages.client.pokemon.update

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

/**
 * Updates whether the Pokémon has the Gigantamax factor.
 *
 * @author Segfault Guy
 * @since July 27, 2023
 */
class GmaxFactorUpdatePacket(pokemon: () -> Pokemon, value: Boolean) : BooleanUpdatePacket<GmaxFactorUpdatePacket>(pokemon, value) {
    override val id = ID

    override fun set(pokemon: Pokemon, value: Boolean) {
        pokemon.gmaxFactor = value
    }

    companion object {
        val ID = cobblemonResource("gmax_factor_update")
        fun decode(buffer: PacketByteBuf) = GmaxFactorUpdatePacket(decodePokemon(buffer), buffer.readBoolean())
    }
}