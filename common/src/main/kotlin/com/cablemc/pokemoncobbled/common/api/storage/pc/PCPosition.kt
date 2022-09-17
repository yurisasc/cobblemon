/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.storage.pc

import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

data class PCPosition(val box: Int, val slot: Int) : StorePosition {
    companion object {
        fun PacketByteBuf.writePCPosition(position: PCPosition) {
            writeSizedInt(IntSize.U_BYTE, position.box)
            writeSizedInt(IntSize.U_BYTE, position.slot)
        }
        fun PacketByteBuf.readPCPosition() = PCPosition(readSizedInt(IntSize.U_BYTE), readSizedInt(IntSize.U_BYTE))
    }
}