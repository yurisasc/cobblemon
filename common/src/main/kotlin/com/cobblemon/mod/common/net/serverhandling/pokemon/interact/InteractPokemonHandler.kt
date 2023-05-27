/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.serverhandling.pokemon.interact

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.net.messages.server.pokemon.interact.InteractPokemonPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

object InteractPokemonHandler : ServerNetworkPacketHandler<InteractPokemonPacket> {
    override fun handle(packet: InteractPokemonPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        val pokemonEntity = player.getWorld().getEntity(packet.pokemonID)
        if (pokemonEntity is PokemonEntity) {
            if (packet.mountShoulder) {
                if (!pokemonEntity.isReadyToSitOnPlayer) {
                    return
                }
                pokemonEntity.tryMountingShoulder(player)
            } else if (packet.ride) {
                if (pokemonEntity.pokemon.riding.supported()) {
                    val seat = pokemonEntity.seats.first { it.acceptsRider(player, pokemonEntity) }
                    seat.mount(pokemonEntity, player)
                }
            } else {
                pokemonEntity.offerHeldItem(player, player.mainHandStack)
            }
        }
    }
}