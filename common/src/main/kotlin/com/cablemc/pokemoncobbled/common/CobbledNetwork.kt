package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import com.cablemc.pokemoncobbled.common.api.events.net.MessageBuiltEvent
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.FriendshipUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.HealthUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.LevelUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.MoveSetUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.NatureUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.PokemonStateUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.ShinyUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.SpeciesUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.InitializePartyPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.MovePartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.RemovePartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SetPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SetPartyReferencePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SwapPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.ui.SummaryUIPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.RequestMoveSwapPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.SendOutPokemonPacket
import com.cablemc.pokemoncobbled.common.util.getServer
import net.minecraft.server.level.ServerPlayer

/**
 * Registers Pokémon Cobbled packets. Packet handlers are set up on handling the [MessageBuiltEvent] dispatched from here.
 *
 * This class also contains short functions for dispatching our packets to a player, all players, or to the entire server.
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
object CobbledNetwork {
    const val PROTOCOL_VERSION = "1"

    lateinit var networkDelegate: NetworkDelegate

    fun ServerPlayer.sendPacket(packet: NetworkPacket) = sendToPlayer(this, packet)
    fun sendToPlayer(player: ServerPlayer, packet: NetworkPacket) = networkDelegate.sendPacketToPlayer(player, packet)
    fun sendToServer(packet: NetworkPacket) = networkDelegate.sendPacketToServer(packet)
    fun sendToAllPlayers(packet: NetworkPacket) = sendToPlayers(getServer()!!.playerList.players, packet)
    fun sendToPlayers(players: Iterable<ServerPlayer>, packet: NetworkPacket) = players.forEach { sendToPlayer(it, packet) }

    interface PreparedMessage<T : NetworkPacket> {
        fun registerMessage()
        fun registerHandler(handler: PacketHandler<T>)
    }

    interface NetworkContext {
        val player: ServerPlayer?
    }


    fun register() {
        /**
         * Client Packets
         */

        // Pokemon Update Packets
        buildClientMessage<LevelUpdatePacket>()
        buildClientMessage<FriendshipUpdatePacket>()
        buildClientMessage<MoveSetUpdatePacket>()
        buildClientMessage<NatureUpdatePacket>()
        buildClientMessage<ShinyUpdatePacket>()
        buildClientMessage<SpeciesUpdatePacket>()
        buildClientMessage<HealthUpdatePacket>()

        // Storage Packets
        buildClientMessage<InitializePartyPacket>()
        buildClientMessage<SetPartyPokemonPacket>()
        buildClientMessage<RemovePartyPokemonPacket>()
        buildClientMessage<MovePartyPokemonPacket>()
        buildClientMessage<SwapPartyPokemonPacket>()
        buildClientMessage<SetPartyReferencePacket>()
        buildClientMessage<PokemonStateUpdatePacket>()

        // UI Packets
        buildClientMessage<SummaryUIPacket>()

        /**
         * Server Packets
         */

        // Storage Packets
        buildServerMessage<SendOutPokemonPacket>()
        buildServerMessage<RequestMoveSwapPacket>()
    }

    private inline fun <reified P : NetworkPacket> buildClientMessage() =
        buildMessage<P>(toServer = false)
    private inline fun <reified P : NetworkPacket> buildServerMessage() =
        buildMessage<P>(toServer = true)

    private inline fun <reified P : NetworkPacket> buildMessage(toServer: Boolean) {
        val message = networkDelegate.buildMessage(P::class.java, toServer)
        // Gives client or server the chance to attach its packet handler
        CobbledEvents.MESSAGE_BUILT.post(MessageBuiltEvent(P::class.java, toServer, message))
        message.registerMessage()
    }
}