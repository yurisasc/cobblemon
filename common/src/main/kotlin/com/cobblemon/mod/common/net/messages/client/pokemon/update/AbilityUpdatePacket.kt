/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.pokemon.update

import com.cobblemon.mod.common.api.abilities.AbilityTemplate
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.RegistryFriendlyByteBuf

/**
 * Packet sent when the ability of a Pokémon has changed. Only sends the template.
 *
 * @author Hiroku
 * @since November 1st, 2022
 */
class AbilityUpdatePacket(pokemon: () -> Pokemon, ability: AbilityTemplate) : SingleUpdatePacket<AbilityTemplate, AbilityUpdatePacket>(pokemon, ability) {

    override val id = ID

    override fun encodeValue(buffer: RegistryFriendlyByteBuf) {
        buffer.writeResourceKey(this.value.resourceKey())
    }

    override fun set(pokemon: Pokemon, value: AbilityTemplate) {
        pokemon.ability = value.asAbility()
    }

    companion object {
        val ID = cobblemonResource("ability_update")
        fun decode(buffer: RegistryFriendlyByteBuf): AbilityUpdatePacket {
            val pokemon = decodePokemon(buffer)
            val ability = buffer.registryAccess()
                .registryOrThrow(CobblemonRegistries.ABILITY_KEY)
                .getOrThrow(buffer.readResourceKey(CobblemonRegistries.ABILITY_KEY))
            return AbilityUpdatePacket(pokemon, ability)
        }
    }

}