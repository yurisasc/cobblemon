/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.serverhandling.pasture

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.api.storage.pc.link.PCLinkManager
import com.cobblemon.mod.common.block.entity.PokemonTetherBlockEntity
import com.cobblemon.mod.common.net.messages.server.pasture.PasturePokemonPacket
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

object PasturePokemonHandler : ServerNetworkPacketHandler<PasturePokemonPacket> {
    override fun handle(packet: PasturePokemonPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        val pc = Cobblemon.storage.getPC(player.uuid)
        val pokemon = pc[packet.pokemonId] ?: return

        val pastureBlockEntity = player.world.getBlockEntity(packet.pasturePos) as? PokemonTetherBlockEntity ?: return
        val state = player.world.getBlockState(packet.pasturePos)
        val direction = state.get(HorizontalFacingBlock.FACING)

        if (pokemon.tetheringId != null) {
            return
        }

        pastureBlockEntity.tether(player, pokemon, direction)

        // todo pc links but for pasture blocks
    }
}