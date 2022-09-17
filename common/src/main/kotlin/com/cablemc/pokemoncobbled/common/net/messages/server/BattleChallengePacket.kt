/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.server

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import java.util.UUID
import net.minecraft.network.PacketByteBuf

class BattleChallengePacket() : NetworkPacket {
    var targetedEntityId: Int = -1
    lateinit var selectedPokemonId: UUID

    constructor(targetedEntityId: Int, selectedPokemonId: UUID): this() {
        this.targetedEntityId = targetedEntityId
        this.selectedPokemonId = selectedPokemonId
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeInt(this.targetedEntityId)
        buffer.writeUuid(this.selectedPokemonId)
    }

    override fun decode(buffer: PacketByteBuf) {
        this.targetedEntityId = buffer.readInt()
        this.selectedPokemonId = buffer.readUuid()
    }
}