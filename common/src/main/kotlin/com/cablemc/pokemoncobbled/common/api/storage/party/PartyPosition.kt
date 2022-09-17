/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.storage.party

import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

data class PartyPosition(val slot: Int) : StorePosition {
    companion object {
        fun PacketByteBuf.writePartyPosition(position: PartyPosition) {
            writeSizedInt(IntSize.U_BYTE, position.slot)
        }
        fun PacketByteBuf.readPartyPosition() = PartyPosition(readSizedInt(IntSize.U_BYTE))
    }
}