/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.spawn

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.net.messages.client.spawn.SpawnExtraDataEntityPacket
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity

class SpawnExtraDataEntityHandler<E : Entity> : ClientNetworkPacketHandler<SpawnExtraDataEntityPacket<E>> {
    override fun handle(packet: SpawnExtraDataEntityPacket<E>, client: MinecraftClient) {
        packet.spawnAndApply(client)
    }

}