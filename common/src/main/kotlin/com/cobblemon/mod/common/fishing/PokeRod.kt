/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.fishing

import com.cobblemon.mod.common.item.interactive.PokerodItem
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.StringIdentifiable

/**
 * Base poke rod object
 * It is intended that there is one poke rod object initialized for a given poke rod type.
 *
 * @property name the poke rod registry name
 * @property bobberType The [ItemStack] of this Pokérod that is the bobber.
 * @property lineColor list of [RGB] values that apply to the fishing line of the Pokérod
 */
open class PokeRod(
    val name: Identifier,
    val bobberType: ItemStack,
    val lineColor: Triple<Int, Int, Int>
): StringIdentifiable {

    @Transient
    var identifier: Identifier = name
        internal set

    override fun asString(): String {
        return identifier.toString()
    }

    // This gets attached during item registry
    internal lateinit var item: PokerodItem

    fun item(): PokerodItem = this.item

    fun stack(count: Int = 1): ItemStack = ItemStack(this.item(), count)

    internal fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(name)
        buffer.writeItemStack(bobberType)
        buffer.writeInt(lineColor.first)
        buffer.writeInt(lineColor.second)
        buffer.writeInt(lineColor.third)
    }

    companion object {
        internal fun decode(buffer: PacketByteBuf): PokeRod {
            val id = buffer.readIdentifier()
            val stack = buffer.readItemStack()
            val r = buffer.readInt()
            val g = buffer.readInt()
            val b = buffer.readInt()
            return PokeRod(id, stack, Triple(r, g, b))
        }
    }

}