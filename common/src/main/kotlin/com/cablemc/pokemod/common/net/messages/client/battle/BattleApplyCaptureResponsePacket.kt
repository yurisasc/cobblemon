/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.battle

import com.cablemc.pokemod.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf

/**
 * Tells a specific player that they should choose a battle capture response for the next Pokémon request in their queue.
 *
 * Handled by [com.cablemc.pokemod.common.client.net.battle.BattleApplyCaptureResponseHandler].
 *
 * @author Hiroku
 * @since July 3rd, 2022
 */
class BattleApplyCaptureResponsePacket() : NetworkPacket {
    override fun encode(buffer: PacketByteBuf) {}
    override fun decode(buffer: PacketByteBuf) {}
}