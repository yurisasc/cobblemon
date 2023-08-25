/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.npc

import com.cobblemon.mod.common.api.entity.NPCSideDelegate
import com.cobblemon.mod.common.net.messages.client.npc.PlayNPCAnimationPacket
import net.minecraft.server.world.ServerWorld

class NPCServerDelegate : NPCSideDelegate {
    lateinit var entity: NPCEntity

    override fun initialize(entity: NPCEntity) {
        super.initialize(entity)
        this.entity = entity
    }

    override fun playAnimation(animationType: String) {
        val pkt = PlayNPCAnimationPacket(entity.id, animationType)
        (entity.world as ServerWorld).getPlayers { it.distanceTo(entity) < 64 }.forEach(pkt::sendToPlayer)
    }
}