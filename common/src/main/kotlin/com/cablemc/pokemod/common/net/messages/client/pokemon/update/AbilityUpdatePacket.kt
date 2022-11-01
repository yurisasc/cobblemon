package com.cablemc.pokemod.common.net.messages.client.pokemon.update

import com.cablemc.pokemod.common.api.abilities.Abilities
import com.cablemc.pokemod.common.api.abilities.AbilityTemplate
import com.cablemc.pokemod.common.pokemon.Pokemon
import net.minecraft.network.PacketByteBuf

/**
 * Packet sent when the ability of a Pokémon has changed. Only sends the template.
 *
 * @author Hiroku
 * @since November 1st, 2022
 */
class AbilityUpdatePacket() : SingleUpdatePacket<AbilityTemplate>(Abilities.first()) {
    constructor(pokemon: Pokemon, ability: AbilityTemplate): this() {
        setTarget(pokemon)
        value = ability
    }

    override fun encodeValue(buffer: PacketByteBuf, value: AbilityTemplate) {
        buffer.writeString(value.name)
    }

    override fun decodeValue(buffer: PacketByteBuf): AbilityTemplate {
        return Abilities.get(buffer.readString())!!
    }

    override fun set(pokemon: Pokemon, value: AbilityTemplate) {
        pokemon.ability = value.create()
    }
}