/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.storage.pc

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.net.messages.client.storage.pc.ClosePCPacket
import net.minecraft.client.MinecraftClient

object ClosePCHandler : ClientNetworkPacketHandler<ClosePCPacket> {
    override fun handle(packet: ClosePCPacket, client: MinecraftClient) {
        // TODO close the PC GUI if the UUID of the opened PC matches packet.storeID
    }
}