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

/**
 * Tells the client to process the request that was previously sent via a BattleQueueRequestPacket.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.battle.BattleMakeChoiceHandler].
 *
 * @author Hiroku
 * @since May 22nd, 2022
 */
class BattleMakeChoicePacket : NetworkPacket {
    override fun encode(buffer: PacketByteBuf) {}
    override fun decode(buffer: PacketByteBuf) {}
}