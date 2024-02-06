/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.controller.properties

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.riding.context.RidingContextBuilder
import com.cobblemon.mod.common.pokemon.riding.controllers.GenericLandController
import com.cobblemon.mod.common.pokemon.riding.controllers.GenericLandControllerAdapter
import com.cobblemon.mod.common.pokemon.riding.controllers.GenericLiquidController
import com.cobblemon.mod.common.pokemon.riding.controllers.GenericLiquidControllerAdapter
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

interface RideControllerProperties : Encodable {

    val identifier: Identifier

    fun apply(context: RidingContextBuilder)

    companion object {

        val deserializers: Map<Identifier, Deserializer<*>>

        init {
            // TODO - Post registration event

            this.deserializers = mapOf(GenericLandController.key to GenericLandControllerAdapter, GenericLiquidController.key to GenericLiquidControllerAdapter)
        }

        fun decode(buffer: PacketByteBuf): RideControllerProperties {
            val key = buffer.readIdentifier()
            return this.deserializers[key]!!.decode(buffer)
        }

    }

}