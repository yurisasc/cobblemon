/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.pokemon.update

import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.RegistryFriendlyByteBuf

/**
 * Updates the Tera Type of the Pokémon.
 *
 * @author Segfault Guy
 * @since July 19, 2023
 */
class TeraTypeUpdatePacket(pokemon: () -> Pokemon, value: ElementalType) : SingleUpdatePacket<ElementalType, TeraTypeUpdatePacket>(pokemon, value) {
    override val id = ID

    override fun encodeValue(buffer: RegistryFriendlyByteBuf) {
        buffer.writeResourceKey(this.value.resourceKey())
    }

    override fun set(pokemon: Pokemon, value: ElementalType) {
        pokemon.teraType = value
    }

    companion object {
        val ID = cobblemonResource("tera_type_update")
        fun decode(buffer: RegistryFriendlyByteBuf) = TeraTypeUpdatePacket(
            decodePokemon(buffer),
            buffer.registryAccess().registryOrThrow(CobblemonRegistries.ELEMENTAL_TYPE_KEY)
                .get(buffer.readResourceKey(CobblemonRegistries.ELEMENTAL_TYPE_KEY))!!
        )
    }
}