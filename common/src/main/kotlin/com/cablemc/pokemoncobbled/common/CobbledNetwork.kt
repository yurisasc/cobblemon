package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import com.cablemc.pokemoncobbled.common.api.events.net.MessageBuiltEvent
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.*
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.*
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.RemoveClientPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.SwapClientPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution.*
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.InitializePartyPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.MoveClientPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SetPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SetPartyReferencePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.ClosePCPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.InitializePCPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.MoveClientPCPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.OpenPCPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.SetPCBoxPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.SetPCPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.ui.SummaryUIPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.BenchMovePacket
import com.cablemc.pokemoncobbled.common.net.messages.server.ChallengePacket
import com.cablemc.pokemoncobbled.common.net.messages.server.RequestMoveSwapPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.SendOutPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.pokemon.update.evolution.*
import com.cablemc.pokemoncobbled.common.net.messages.server.battle.BattleSelectActionsPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.SwapPCPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.party.MovePartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.party.SwapPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc.MovePCPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc.MovePCPokemonToPartyPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc.MovePartyPokemonToPCPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc.SwapPCPokemonPacket
import com.cablemc.pokemoncobbled.common.util.getServer
import net.minecraft.server.network.ServerPlayerEntity

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


    fun ServerPlayerEntity.sendPacket(packet: NetworkPacket) = sendToPlayer(this, packet)
    fun sendToPlayer(player: ServerPlayerEntity, packet: NetworkPacket) = networkDelegate.sendPacketToPlayer(player, packet)
    fun sendToServer(packet: NetworkPacket) = networkDelegate.sendPacketToServer(packet)
    fun sendToAllPlayers(packet: NetworkPacket) = sendToPlayers(getServer()!!.playerManager.playerList, packet)
    fun sendToPlayers(players: Iterable<ServerPlayerEntity>, packet: NetworkPacket) = players.forEach { sendToPlayer(it, packet) }

    interface PreparedMessage<T : NetworkPacket> {
        fun registerMessage()
        fun registerHandler(handler: PacketHandler<T>)
    }

    interface NetworkContext {
        val player: ServerPlayerEntity?
    }


    fun register() {
        // Don't forget to register handlers in either ClientPacketRegistrar or ServerPacketRegistrar!

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
        buildClientMessage<ExperienceUpdatePacket>()
        buildClientMessage<StatusUpdatePacket>()
        buildClientMessage<CaughtBallUpdatePacket>()
        buildClientMessage<BenchedMovesUpdatePacket>()
        buildClientMessage<GenderUpdatePacket>()
        buildClientMessage<AspectsUpdatePacket>()
        // Evolution start
        buildClientMessage<AddEvolutionPacket>()
        buildClientMessage<ClearEvolutionsPacket>()
        buildClientMessage<RemoveEvolutionPacket>()
        // Evolution End

        buildClientMessage<PokemonStateUpdatePacket>()

        // Storage Packets
        buildClientMessage<InitializePartyPacket>()
        buildClientMessage<SetPartyPokemonPacket>()
        buildClientMessage<MoveClientPartyPokemonPacket>()
        buildClientMessage<SetPartyReferencePacket>()

        buildClientMessage<InitializePCPacket>()
        buildClientMessage<MoveClientPCPokemonPacket>()
        buildClientMessage<SetPCBoxPokemonPacket>()
        buildClientMessage<SetPCPokemonPacket>()
        buildClientMessage<OpenPCPacket>()
        buildClientMessage<ClosePCPacket>()

        buildClientMessage<SwapClientPokemonPacket>()
        buildClientMessage<RemoveClientPokemonPacket>()

        // UI Packets
        buildClientMessage<SummaryUIPacket>()

        // Battle packets
        buildClientMessage<BattleEndPacket>()
        buildClientMessage<BattleInitializePacket>()
        buildClientMessage<BattleQueueRequestPacket>()
        buildClientMessage<BattleFaintPacket>()
        buildClientMessage<BattleMakeChoicePacket>()
        buildClientMessage<BattleHealthChangePacket>()
        buildClientMessage<BattleSetTeamPokemonPacket>()
        buildClientMessage<BattleSwitchPokemonPacket>()
        buildClientMessage<BattleMessagePacket>()

        /**
         * Server Packets
         */

        // Pokemon Update Packets
        // Evolution start
        buildServerMessage<AcceptEvolutionPacket>()
        // Evolution End

        // Storage Packets
        buildServerMessage<SendOutPokemonPacket>()
        buildServerMessage<RequestMoveSwapPacket>()
        buildServerMessage<BenchMovePacket>()
        buildServerMessage<ChallengePacket>()

        buildServerMessage<MovePCPokemonToPartyPacket>()
        buildServerMessage<MovePartyPokemonToPCPacket>()

        buildServerMessage<SwapPCPokemonPacket>()
        buildServerMessage<SwapPartyPokemonPacket>()

        buildServerMessage<MovePCPokemonPacket>()
        buildServerMessage<MovePartyPokemonPacket>()

        buildServerMessage<SwapPCPartyPokemonPacket>()

        // Battle packets
        buildServerMessage<BattleSelectActionsPacket>()
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