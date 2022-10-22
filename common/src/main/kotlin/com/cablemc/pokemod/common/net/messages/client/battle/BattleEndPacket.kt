/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.battle

import com.cablemc.pokemod.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf

/**
 * Tells the client to terminate its battle reference
 *
 * Handled by [com.cablemc.pokemod.common.client.net.battle.BattleEndHandler].
 *
 * @author Hiroku
 * @since May 6th, 2022
 */
class BattleEndPacket : NetworkPacket {
    override fun encode(buffer: PacketByteBuf) {}
    override fun decode(buffer: PacketByteBuf) {}
}