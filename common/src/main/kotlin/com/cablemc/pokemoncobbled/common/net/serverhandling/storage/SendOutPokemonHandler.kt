/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.serverhandling.storage

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.CobbledSounds
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.scheduling.afterOnMain
import com.cablemc.pokemoncobbled.common.net.messages.server.SendOutPokemonPacket
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketHandler
import com.cablemc.pokemoncobbled.common.pokemon.activestate.ActivePokemonState
import com.cablemc.pokemoncobbled.common.util.playSoundServer
import com.cablemc.pokemoncobbled.common.util.toVec3d
import com.cablemc.pokemoncobbled.common.util.traceBlockCollision
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

const val SEND_OUT_DURATION = 1.5F
object SendOutPokemonHandler : ServerPacketHandler<SendOutPokemonPacket> {
    override fun invokeOnServer(packet: SendOutPokemonPacket, ctx: CobbledNetwork.NetworkContext, player: ServerPlayerEntity) {
        val slot = packet.slot.takeIf { it >= 0 } ?: return
        val party = PokemonCobbled.storage.getParty(player)
        val pokemon = party.get(slot) ?: return
        if (pokemon.currentHealth <= 0) {
            return
        }
        val state = pokemon.state

        if (state !is ActivePokemonState) {
            val trace = player.traceBlockCollision(maxDistance = 15F)
            if (trace != null && trace.direction == Direction.UP && !player.world.getBlockState(trace.blockPos.up()).material.isSolid) {
                val position = Vec3d(trace.location.x, trace.blockPos.up().toVec3d().y, trace.location.z)
                pokemon.sendOutWithAnimation(player, player.getWorld(), position)
            }
        } else {
            val entity = state.entity
            if (entity != null) {
                entity.recallWithAnimation()
            } else {
                pokemon.recall()
            }
        }
    }
}