/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.server

import com.cablemc.pokemod.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf

class SelectStarterPacket internal constructor() : NetworkPacket {

    var categoryName: String = ""
    var selected: Int = -1

    constructor(categoryName: String, selected: Int) : this() {
        this.categoryName = categoryName
        this.selected = selected
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeString(categoryName)
        buffer.writeInt(selected)
    }

    override fun decode(buffer: PacketByteBuf) {
        categoryName = buffer.readString()
        selected = buffer.readInt()
    }
}