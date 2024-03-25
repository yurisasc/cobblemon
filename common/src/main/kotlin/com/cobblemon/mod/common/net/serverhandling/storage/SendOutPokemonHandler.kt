/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.serverhandling.storage

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.net.messages.server.SendOutPokemonPacket
import com.cobblemon.mod.common.pokemon.activestate.ActivePokemonState
import com.cobblemon.mod.common.pokemon.activestate.ShoulderedState
import com.cobblemon.mod.common.util.raycastToNearbyGround
import com.cobblemon.mod.common.util.toBlockPos
import net.minecraft.block.CactusBlock
import net.minecraft.block.CampfireBlock
import net.minecraft.block.FireBlock
import net.minecraft.block.MagmaBlock
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.RaycastContext

object SendOutPokemonHandler : ServerNetworkPacketHandler<SendOutPokemonPacket> {

    const val SEND_OUT_DURATION = 1.5F

    override fun handle(packet: SendOutPokemonPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        val slot = packet.slot.takeIf { it >= 0 } ?: return
        val party = Cobblemon.storage.getParty(player)
        val pokemon = party.get(slot) ?: return
        if (pokemon.isFainted()) {
            return
        }
        val state = pokemon.state
        if (state is ShoulderedState || state !is ActivePokemonState) {
            val position = player.raycastToNearbyGround(12.0, 5.0, RaycastContext.FluidHandling.ANY)

            if (position != null) {
                var safeLocation = true
                val posState = player.world.getBlockState(position.toBlockPos().down())

                // TODO: Move all the "is the sendout location safe" logic to its own method. Create a "raycastToSafePosition" method using that one
                // TODO: Investigate unusual results from debug below. The raycastToNearbyGround method may be returning unexpected results
                // DEBUG: println("FireImmune: " + pokemon.isFireImmune() + ", Target block: " + player.world.getBlockState(position.toBlockPos()).block + ", Below target block: " + player.world.getBlockState(position.toBlockPos().down()).block)

                if (!pokemon.isFireImmune()) {
                    if (posState.block is FireBlock ||
                        posState.block is MagmaBlock ||
                        posState.block is CampfireBlock ||
                        posState.fluidState.isIn(FluidTags.LAVA)
                        )
                    {
                        safeLocation = false
                    }
                }

                if (posState.block is CactusBlock) { safeLocation = false }

                if (safeLocation) {
                    pokemon.sendOutWithAnimation(player, player.serverWorld, position)
                } else {
                    // TODO: Add this string to lang files
                    val message: String = "That location isn't safe to send out " + pokemon.getDisplayName().string + "!"
                    player.sendMessage(text(message).red(), true)
                }
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