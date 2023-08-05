/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.sound

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.client.entity.PokemonClientDelegate
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.net.messages.client.sound.PokemonCryPacket
import net.minecraft.client.MinecraftClient

object PokemonCryHandler : ClientNetworkPacketHandler<PokemonCryPacket> {
    override fun handle(packet: PokemonCryPacket, client: MinecraftClient) {
        val entity = client.world?.getEntityById(packet.entityId) ?: return
        if (entity is PokemonEntity) {
            val delegate = entity.delegate as PokemonClientDelegate
            delegate.cry()
        }
    }
}