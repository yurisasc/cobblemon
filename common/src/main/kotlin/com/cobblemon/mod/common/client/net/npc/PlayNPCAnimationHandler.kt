/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.npc

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.client.entity.NPCClientDelegate
import com.cobblemon.mod.common.entity.npc.NPCEntity
import com.cobblemon.mod.common.net.messages.client.npc.PlayNPCAnimationPacket
import net.minecraft.client.MinecraftClient

object PlayNPCAnimationHandler : ClientNetworkPacketHandler<PlayNPCAnimationPacket> {
    override fun handle(packet: PlayNPCAnimationPacket, client: MinecraftClient) {
        val entity = client.world?.getEntityById(packet.entityId) ?: return
        if (entity is NPCEntity) {
            val delegate = entity.delegate as NPCClientDelegate
            delegate.playAnimation(packet.animationType)
        }
    }
}