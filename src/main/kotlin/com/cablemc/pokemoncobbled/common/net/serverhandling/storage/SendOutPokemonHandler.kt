package com.cablemc.pokemoncobbled.common.net.serverhandling.storage

import com.cablemc.pokemoncobbled.common.api.scheduling.after
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStoreManager
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.server.SendOutPokemonPacket
import com.cablemc.pokemoncobbled.common.sound.SoundRegistry
import com.cablemc.pokemoncobbled.common.util.playSoundServer
import com.cablemc.pokemoncobbled.common.util.toVec3
import com.cablemc.pokemoncobbled.common.util.traceBlockCollision
import net.minecraft.core.Direction
import net.minecraftforge.fmllegacy.network.NetworkEvent

object SendOutPokemonHandler : PacketHandler<SendOutPokemonPacket> {
    override fun invoke(packet: SendOutPokemonPacket, ctx: NetworkEvent.Context) {
        val player = ctx.sender ?: return
        val slot = packet.slot.takeIf { it >= 0 } ?: return
        ctx.enqueueWork {
            val party = PokemonStoreManager.getParty(player)
            val pokemon = party.get(slot) ?: return@enqueueWork
            val entity = pokemon.entity

            if (entity == null) {
                val trace = player.traceBlockCollision(maxDistance = 15F)
                if (trace != null && trace.direction == Direction.UP && !player.level.getBlockState(trace.blockPos.above()).material.isSolid) {
                    val position = trace.blockPos.above().toVec3().add(trace.location.x - trace.location.x.toInt(), 0.0, trace.location.z - trace.location.z.toInt())
                    pokemon.sendOut(player.getLevel(), position) {
                        player.getLevel().playSoundServer(position, SoundRegistry.SEND_OUT.get(), volume = 0.2F)
                        it.phasingTargetId.set(player.id)
                        it.beamModeEmitter.set(1)

                        after(seconds = 1.5F) {
                            it.phasingTargetId.set(-1)
                            it.beamModeEmitter.set(0)
                        }
                    }
                }
            } else if (entity.phasingTargetId.get() == -1) {
                player.getLevel().playSoundServer(entity.position(), SoundRegistry.RECALL.get(), volume = 0.2F)
                entity.phasingTargetId.set(player.id)
                entity.beamModeEmitter.set(2)

                after(seconds = 1.5F) { pokemon.recall() }
            }
        }
    }
}