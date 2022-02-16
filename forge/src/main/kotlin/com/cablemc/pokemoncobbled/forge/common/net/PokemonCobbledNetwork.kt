package com.cablemc.pokemoncobbled.forge.common.net

import com.cablemc.pokemoncobbled.forge.common.api.event.net.MessageBuiltEvent
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.pokemon.update.LevelUpdatePacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.pokemon.update.MoveSetUpdatePacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.pokemon.update.NatureUpdatePacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.pokemon.update.ShinyUpdatePacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.pokemon.update.SpeciesUpdatePacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.pokemon.update.PokemonStateUpdatePacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party.InitializePartyPacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party.MovePartyPokemonPacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party.RemovePartyPokemonPacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party.SetPartyPokemonPacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party.SetPartyReferencePacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party.SwapPartyPokemonPacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.client.ui.SummaryUIPacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.server.SendOutPokemonPacket
import com.cablemc.pokemoncobbled.forge.common.net.messages.server.RequestMoveSwapPacket
import com.cablemc.pokemoncobbled.forge.common.net.serializers.Vec3DataSerializer
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.forge.mod.PokemonCobbledMod
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor

/**
 * Registers Pokémon Cobbled packets. Packet handlers are set up on handling the [MessageBuiltEvent] dispatched from here.
 *
 * This class also contains short functions for dispatching our packets to a player, all players, or to the entire server.
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
object PokemonCobbledNetwork {
    const val PROTOCOL_VERSION = "1"

    private var discriminator = 0

    fun ServerPlayer.sendPacket(packet: NetworkPacket) =
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.sendToPlayer(this, packet)
    fun sendToPlayer(player: ServerPlayer, packet: NetworkPacket) = com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.channel.send(PacketDistributor.PLAYER.with { player }, packet)
    fun sendToServer(packet: NetworkPacket) = com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.channel.sendToServer(packet)
    fun sendToAllPlayers(packet: NetworkPacket) = com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.channel.sendToServer(packet)
    fun sendToPlayers(players: Iterable<ServerPlayer>, packet: NetworkPacket) = players.forEach {
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.sendToPlayer(
            it,
            packet
        )
    }

    val channel = NetworkRegistry.newSimpleChannel(
        cobbledResource("main"),
        { com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.PROTOCOL_VERSION },
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.PROTOCOL_VERSION::equals,
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.PROTOCOL_VERSION::equals
    )

    fun register() {
        EntityDataSerializers.registerSerializer(Vec3DataSerializer)

        /**
         * Client Packets
         */

        // Pokemon Update Packets
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildClientMessage<LevelUpdatePacket>()
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildClientMessage<MoveSetUpdatePacket>()
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildClientMessage<NatureUpdatePacket>()
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildClientMessage<ShinyUpdatePacket>()
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildClientMessage<SpeciesUpdatePacket>()

        // Storage Packets
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildClientMessage<InitializePartyPacket>()
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildClientMessage<SetPartyPokemonPacket>()
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildClientMessage<RemovePartyPokemonPacket>()
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildClientMessage<MovePartyPokemonPacket>()
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildClientMessage<SwapPartyPokemonPacket>()
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildClientMessage<SetPartyReferencePacket>()
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildClientMessage<PokemonStateUpdatePacket>()

        // UI Packets
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildClientMessage<SummaryUIPacket>()

        /**
         * Server Packets
         */

        // Storage Packets
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildServerMessage<SendOutPokemonPacket>()
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildServerMessage<RequestMoveSwapPacket>()
    }

    private inline fun <reified P : NetworkPacket> buildClientMessage() =
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildMessage<P>(NetworkDirection.PLAY_TO_CLIENT)
    private inline fun <reified P : NetworkPacket> buildServerMessage() =
        com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.buildMessage<P>(NetworkDirection.PLAY_TO_SERVER)

    private inline fun <reified P : NetworkPacket> buildMessage(direction: NetworkDirection) {
        val messageBuilder = com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.channel.messageBuilder(P::class.java, com.cablemc.pokemoncobbled.forge.common.net.PokemonCobbledNetwork.discriminator++, direction)
            .encoder { packet, buffer -> packet.encode(buffer) }
            .decoder { buffer -> P::class.java.newInstance().also { it.decode(buffer) } }
        PokemonCobbledMod.EVENT_BUS.post(MessageBuiltEvent(P::class.java, messageBuilder))
        messageBuilder.add()
    }
}