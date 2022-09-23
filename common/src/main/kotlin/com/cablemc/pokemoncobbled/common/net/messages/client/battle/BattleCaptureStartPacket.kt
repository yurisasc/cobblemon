/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * Used to indicate that a capture is being started in a battle. This is
 * to show the capture in the battle overlay.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.battle.BattleCaptureStartHandler].
 *
 * @author Hiroku
 * @since July 2nd, 2022
 */
class BattleCaptureStartPacket() : NetworkPacket {
    lateinit var pokeBallType: Identifier
    lateinit var targetPNX: String

    constructor(pokeBallType: Identifier, targetPNX: String): this() {
        this.pokeBallType = pokeBallType
        this.targetPNX = targetPNX
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(pokeBallType)
        buffer.writeString(targetPNX)
    }

    override fun decode(buffer: PacketByteBuf) {
        pokeBallType = buffer.readIdentifier()
        targetPNX = buffer.readString()
    }
}