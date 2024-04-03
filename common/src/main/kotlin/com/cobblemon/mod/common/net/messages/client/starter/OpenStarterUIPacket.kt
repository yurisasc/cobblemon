/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.starter

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.starter.RenderableStarterCategory
import com.cobblemon.mod.common.api.starter.StarterCategory
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class OpenStarterUIPacket(val categories: Map<Identifier, RenderableStarterCategory>) : NetworkPacket<OpenStarterUIPacket> {

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeMap(this.categories, PacketByteBuf::writeIdentifier) { categoryBuffer, category ->
            categoryBuffer.writeText(category.displayName)
            categoryBuffer.writeCollection(category.pokemon) { pokemonBuffer, renderable ->
                renderable.saveToBuffer(pokemonBuffer)
            }
        }
    }

    companion object {
        val ID = cobblemonResource("open_starter")
        fun decode(buffer: PacketByteBuf): OpenStarterUIPacket {
            val categories = buffer.readMap(PacketByteBuf::readIdentifier) { categoryBuffer ->
                val displayName = buffer.readText()
                val renderablePokemon = categoryBuffer.readList(RenderablePokemon::loadFromBuffer)
                return@readMap RenderableStarterCategory(displayName, renderablePokemon)
            }
            return OpenStarterUIPacket(categories)
        }
    }
}