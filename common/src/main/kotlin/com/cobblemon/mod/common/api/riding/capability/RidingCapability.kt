/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.capability

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerProperties
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import java.util.function.Predicate

interface RidingCapability : Encodable {

    /** A reference key used to denote the individual capability */
    val key: Identifier

    /** Specifies what allows this capability to be applied to the entity */
    val condition: Predicate<PokemonEntity>

    /** Specifies properties relative to a controller configured for this capability */
    val properties: RideControllerProperties

    companion object {

        val LAND = cobblemonResource("land")
        val LIQUID = cobblemonResource("liquids")
        val FLIGHT = cobblemonResource("flight")

        fun create(key: Identifier, properties: RideControllerProperties): RidingCapability {
            return when(key) {
                LAND -> LandCapability(properties)
                else -> throw IllegalArgumentException("Invalid key: $key")
            }
        }

        fun decode(buffer: PacketByteBuf): RidingCapability {
            val key = buffer.readIdentifier()
            return this.create(key, RideControllerProperties.decode(buffer))
        }

    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(this.key)
        this.properties.encode(buffer)
    }

}