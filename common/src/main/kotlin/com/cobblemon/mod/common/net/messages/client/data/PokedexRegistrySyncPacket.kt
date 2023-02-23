/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.data

import com.cobblemon.mod.common.pokedex.PokedexDataRegistry
import net.minecraft.network.PacketByteBuf

class PokedexRegistrySyncPacket : DataRegistrySyncPacket<PokedexDataRegistry> {

    constructor(): super(emptyList())
    constructor(pokedexes: PokedexDataRegistry): super(listOf(pokedexes))

    override fun encodeEntry(buffer: PacketByteBuf, entry: PokedexDataRegistry) {
        TODO("Not yet implemented")
    }

    override fun decodeEntry(buffer: PacketByteBuf): PokedexDataRegistry? {
        TODO("Not yet implemented")
    }

    override fun synchronizeDecoded(entries: Collection<PokedexDataRegistry>) {
        TODO("Not yet implemented")
    }
}