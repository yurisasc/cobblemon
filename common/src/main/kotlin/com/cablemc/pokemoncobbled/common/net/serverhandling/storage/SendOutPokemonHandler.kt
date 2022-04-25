package com.cablemc.pokemoncobbled.common.net.serverhandling.storage

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.CobbledSounds
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.scheduling.afterOnMain
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.server.SendOutPokemonPacket
import com.cablemc.pokemoncobbled.common.pokemon.activestate.ActivePokemonState
import com.cablemc.pokemoncobbled.common.util.playSoundServer
import com.cablemc.pokemoncobbled.common.util.runOnServer
import com.cablemc.pokemoncobbled.common.util.toVec3
import com.cablemc.pokemoncobbled.common.util.traceBlockCollision
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3

object SendOutPokemonHandler : PacketHandler<SendOutPokemonPacket> {
    override fun invoke(packet: SendOutPokemonPacket, ctx: CobbledNetwork.NetworkContext) {
        val player = ctx.player ?: return
        val slot = packet.slot.takeIf { it >= 0 } ?: return
        runOnServer {
            val party = PokemonCobbled.storage.getParty(player)
            val pokemon = party.get(slot) ?: return@runOnServer
            val state = pokemon.state

            if (state !is ActivePokemonState) {
                val trace = player.traceBlockCollision(maxDistance = 15F)
                if (trace != null && trace.direction == Direction.UP && !player.level.getBlockState(trace.blockPos.above()).material.isSolid) {
                    val position = Vec3(trace.location.x, trace.blockPos.above().toVec3().y, trace.location.z)
                    pokemon.sendOut(player.getLevel(), position) {
                        player.getLevel().playSoundServer(position, CobbledSounds.SEND_OUT.get(), volume = 0.2F)
                        it.phasingTargetId.set(player.id)
                        it.beamModeEmitter.set(1)

                        afterOnMain(seconds = 1.5F) {
                            it.phasingTargetId.set(-1)
                            it.beamModeEmitter.set(0)
                        }
                    }
                }
            } else {
                val entity = state.entity
                if (entity != null && entity.phasingTargetId.get() == -1) {
                    player.getLevel().playSoundServer(entity.position(), CobbledSounds.RECALL.get(), volume = 0.2F)
                    entity.phasingTargetId.set(player.id)
                    entity.beamModeEmitter.set(2)
                    afterOnMain(seconds = 1.5F) { pokemon.recall() }
                } else {
                    pokemon.recall()
                }
            }
        }
    }
}