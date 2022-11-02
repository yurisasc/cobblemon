/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common

import com.cablemc.pokemod.common.api.events.PokemodEvents
import com.cablemc.pokemod.common.api.events.net.MessageBuiltEvent
import com.cablemc.pokemod.common.api.net.NetworkPacket
import com.cablemc.pokemod.common.net.PacketHandler
import com.cablemc.pokemod.common.net.messages.client.battle.BattleApplyCaptureResponsePacket
import com.cablemc.pokemod.common.net.messages.client.battle.BattleCaptureEndPacket
import com.cablemc.pokemod.common.net.messages.client.battle.BattleCaptureShakePacket
import com.cablemc.pokemod.common.net.messages.client.battle.BattleCaptureStartPacket
import com.cablemc.pokemod.common.net.messages.client.battle.BattleEndPacket
import com.cablemc.pokemod.common.net.messages.client.battle.BattleFaintPacket
import com.cablemc.pokemod.common.net.messages.client.battle.BattleHealthChangePacket
import com.cablemc.pokemod.common.net.messages.client.battle.BattleInitializePacket
import com.cablemc.pokemod.common.net.messages.client.battle.BattleMakeChoicePacket
import com.cablemc.pokemod.common.net.messages.client.battle.BattleMessagePacket
import com.cablemc.pokemod.common.net.messages.client.battle.BattleQueueRequestPacket
import com.cablemc.pokemod.common.net.messages.client.battle.BattleSetTeamPokemonPacket
import com.cablemc.pokemod.common.net.messages.client.battle.BattleSwitchPokemonPacket
import com.cablemc.pokemod.common.net.messages.client.battle.BattleUpdateTeamPokemonPacket
import com.cablemc.pokemod.common.net.messages.client.battle.ChallengeNotificationPacket
import com.cablemc.pokemod.common.net.messages.client.data.AbilityRegistrySyncPacket
import com.cablemc.pokemod.common.net.messages.client.data.MovesRegistrySyncPacket
import com.cablemc.pokemod.common.net.messages.client.data.PropertiesCompletionRegistrySyncPacket
import com.cablemc.pokemod.common.net.messages.client.data.SpeciesRegistrySyncPacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.AbilityUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.AspectsUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.BenchedMovesUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.CaughtBallUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.ExperienceUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.FriendshipUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.GenderUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.HealthUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.LevelUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.MoveSetUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.NatureUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.PokemonStateUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.ShinyUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.SpeciesUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.StatusUpdatePacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.evolution.AddEvolutionPacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.evolution.ClearEvolutionsPacket
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.evolution.RemoveEvolutionPacket
import com.cablemc.pokemod.common.net.messages.client.settings.ServerSettingsPacket
import com.cablemc.pokemod.common.net.messages.client.starter.OpenStarterUIPacket
import com.cablemc.pokemod.common.net.messages.client.starter.SetClientPlayerDataPacket
import com.cablemc.pokemod.common.net.messages.client.storage.RemoveClientPokemonPacket
import com.cablemc.pokemod.common.net.messages.client.storage.SwapClientPokemonPacket
import com.cablemc.pokemod.common.net.messages.client.storage.party.InitializePartyPacket
import com.cablemc.pokemod.common.net.messages.client.storage.party.MoveClientPartyPokemonPacket
import com.cablemc.pokemod.common.net.messages.client.storage.party.SetPartyPokemonPacket
import com.cablemc.pokemod.common.net.messages.client.storage.party.SetPartyReferencePacket
import com.cablemc.pokemod.common.net.messages.client.storage.pc.ClosePCPacket
import com.cablemc.pokemod.common.net.messages.client.storage.pc.InitializePCPacket
import com.cablemc.pokemod.common.net.messages.client.storage.pc.MoveClientPCPokemonPacket
import com.cablemc.pokemod.common.net.messages.client.storage.pc.OpenPCPacket
import com.cablemc.pokemod.common.net.messages.client.storage.pc.SetPCBoxPokemonPacket
import com.cablemc.pokemod.common.net.messages.client.storage.pc.SetPCPokemonPacket
import com.cablemc.pokemod.common.net.messages.client.ui.SummaryUIPacket
import com.cablemc.pokemod.common.net.messages.server.BattleChallengePacket
import com.cablemc.pokemod.common.net.messages.server.BenchMovePacket
import com.cablemc.pokemod.common.net.messages.server.RequestMoveSwapPacket
import com.cablemc.pokemod.common.net.messages.server.SelectStarterPacket
import com.cablemc.pokemod.common.net.messages.server.SendOutPokemonPacket
import com.cablemc.pokemod.common.net.messages.server.battle.BattleSelectActionsPacket
import com.cablemc.pokemod.common.net.messages.server.pokemon.update.evolution.AcceptEvolutionPacket
import com.cablemc.pokemod.common.net.messages.server.starter.RequestStarterScreenPacket
import com.cablemc.pokemod.common.net.messages.server.storage.SwapPCPartyPokemonPacket
import com.cablemc.pokemod.common.net.messages.server.storage.party.MovePartyPokemonPacket
import com.cablemc.pokemod.common.net.messages.server.storage.party.ReleasePartyPokemonPacket
import com.cablemc.pokemod.common.net.messages.server.storage.party.SwapPartyPokemonPacket
import com.cablemc.pokemod.common.net.messages.server.storage.pc.MovePCPokemonPacket
import com.cablemc.pokemod.common.net.messages.server.storage.pc.MovePCPokemonToPartyPacket
import com.cablemc.pokemod.common.net.messages.server.storage.pc.MovePartyPokemonToPCPacket
import com.cablemc.pokemod.common.net.messages.server.storage.pc.ReleasePCPokemonPacket
import com.cablemc.pokemod.common.net.messages.server.storage.pc.SwapPCPokemonPacket
import com.cablemc.pokemod.common.util.getServer
import java.util.concurrent.CompletableFuture
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Registers Pokémon Cobbled packets. Packet handlers are set up on handling the [MessageBuiltEvent] dispatched from here.
 *
 * This class also contains short functions for dispatching our packets to a player, all players, or to the entire server.
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
object PokemodNetwork {
    const val PROTOCOL_VERSION = "1"

    var clientHandlersRegistered = CompletableFuture<Unit>()
    var serverHandlersRegistered = CompletableFuture<Unit>()

    init {
        clientHandlersRegistered.runAfterBoth(serverHandlersRegistered) {
            register()
        }
    }

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
        buildClientMessage<AbilityUpdatePacket>()
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

        // Starter packets
        buildClientMessage<OpenStarterUIPacket>()
        buildClientMessage<SetClientPlayerDataPacket>()

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
        buildClientMessage<BattleCaptureStartPacket>()
        buildClientMessage<BattleCaptureEndPacket>()
        buildClientMessage<BattleCaptureShakePacket>()
        buildClientMessage<BattleApplyCaptureResponsePacket>()
        buildClientMessage<ChallengeNotificationPacket>()
        buildClientMessage<BattleUpdateTeamPokemonPacket>()

        // Settings packets
        buildClientMessage<ServerSettingsPacket>()

        // Data registries
        buildClientMessage<AbilityRegistrySyncPacket>()
        buildClientMessage<MovesRegistrySyncPacket>()
        buildClientMessage<SpeciesRegistrySyncPacket>()
        buildClientMessage<PropertiesCompletionRegistrySyncPacket>()

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
        buildServerMessage<BattleChallengePacket>()

        buildServerMessage<MovePCPokemonToPartyPacket>()
        buildServerMessage<MovePartyPokemonToPCPacket>()
        buildServerMessage<ReleasePartyPokemonPacket>()
        buildServerMessage<ReleasePCPokemonPacket>()

        // Starter packets
        buildServerMessage<SelectStarterPacket>()
        buildServerMessage<RequestStarterScreenPacket>()

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
        PokemodEvents.MESSAGE_BUILT.post(MessageBuiltEvent(P::class.java, toServer, message))
        message.registerMessage()
    }
}