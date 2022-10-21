/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net

import com.cablemc.pokemod.common.client.net.battle.BattleApplyCaptureResponseHandler
import com.cablemc.pokemod.common.client.net.battle.BattleCaptureEndHandler
import com.cablemc.pokemod.common.client.net.battle.BattleCaptureShakeHandler
import com.cablemc.pokemod.common.client.net.battle.BattleCaptureStartHandler
import com.cablemc.pokemod.common.client.net.battle.BattleEndHandler
import com.cablemc.pokemod.common.client.net.battle.BattleFaintHandler
import com.cablemc.pokemod.common.client.net.battle.BattleHealthChangeHandler
import com.cablemc.pokemod.common.client.net.battle.BattleInitializeHandler
import com.cablemc.pokemod.common.client.net.battle.BattleMakeChoiceHandler
import com.cablemc.pokemod.common.client.net.battle.BattleMessageHandler
import com.cablemc.pokemod.common.client.net.battle.BattleQueueRequestHandler
import com.cablemc.pokemod.common.client.net.battle.BattleSetTeamPokemonHandler
import com.cablemc.pokemod.common.client.net.battle.BattleSwitchPokemonHandler
import com.cablemc.pokemod.common.client.net.battle.BattleUpdateTeamPokemonHandler
import com.cablemc.pokemod.common.client.net.battle.ChallengeNotificationHandler
import com.cablemc.pokemod.common.client.net.gui.SummaryUIPacketHandler
import com.cablemc.pokemod.common.client.net.pokemon.update.EvolutionUpdatePacketHandler
import com.cablemc.pokemod.common.client.net.pokemon.update.SingleUpdatePacketHandler
import com.cablemc.pokemod.common.client.net.settings.ServerSettingsPacketHandler
import com.cablemc.pokemod.common.client.net.starter.StarterUIPacketHandler
import com.cablemc.pokemod.common.client.net.storage.RemoveClientPokemonHandler
import com.cablemc.pokemod.common.client.net.storage.SwapClientPokemonHandler
import com.cablemc.pokemod.common.client.net.storage.party.InitializePartyHandler
import com.cablemc.pokemod.common.client.net.storage.party.MoveClientPartyPokemonHandler
import com.cablemc.pokemod.common.client.net.storage.party.SetPartyPokemonHandler
import com.cablemc.pokemod.common.client.net.storage.party.SetPartyReferenceHandler
import com.cablemc.pokemod.common.client.net.storage.pc.ClosePCHandler
import com.cablemc.pokemod.common.client.net.storage.pc.InitializePCHandler
import com.cablemc.pokemod.common.client.net.storage.pc.MoveClientPCPokemonHandler
import com.cablemc.pokemod.common.client.net.storage.pc.OpenPCHandler
import com.cablemc.pokemod.common.client.net.storage.pc.SetPCBoxPokemonHandler
import com.cablemc.pokemod.common.client.net.storage.pc.SetPCPokemonHandler
import com.cablemc.pokemod.common.net.SidedPacketRegistrar
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

/**
 * Registers packet handlers that the client will need. This is separated from the server ones
 * not because they have to be, but because it helps us guarantee client access safety in a CI
 * job.
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
object ClientPacketRegistrar : SidedPacketRegistrar() {
    override fun registerHandlers() {
        // Don't forget to register packets in CobbledNetwork!

        registerHandler<ExperienceUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<LevelUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<SpeciesUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<FriendshipUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<PokemonStateUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<ShinyUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<NatureUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<MoveSetUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<HealthUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<StatusUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<CaughtBallUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<BenchedMovesUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<GenderUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<AspectsUpdatePacket>(SingleUpdatePacketHandler())

        // Party storage
        registerHandler(InitializePartyHandler)
        registerHandler(SetPartyPokemonHandler)
        registerHandler(MoveClientPartyPokemonHandler)
        registerHandler(SetPartyReferenceHandler)

        // PC storage
        registerHandler(InitializePCHandler)
        registerHandler(MoveClientPCPokemonHandler)
        registerHandler(SetPCBoxPokemonHandler)
        registerHandler(SetPCPokemonHandler)
        registerHandler(OpenPCHandler)
        registerHandler(ClosePCHandler)

        // General storage
        registerHandler(RemoveClientPokemonHandler)
        registerHandler(SwapClientPokemonHandler)

        registerHandler(SetClientPlayerDataHandler)

        registerHandler(SummaryUIPacketHandler)
        registerHandler(StarterUIPacketHandler)
        registerHandler<AddEvolutionPacket>(EvolutionUpdatePacketHandler())
        registerHandler<RemoveEvolutionPacket>(EvolutionUpdatePacketHandler())
        registerHandler<ClearEvolutionsPacket>(EvolutionUpdatePacketHandler())

        // Battle handlers
        registerHandler(BattleEndHandler)
        registerHandler(BattleInitializeHandler)
        registerHandler(BattleFaintHandler)
        registerHandler(BattleQueueRequestHandler)
        registerHandler(BattleMakeChoiceHandler)
        registerHandler(BattleHealthChangeHandler)
        registerHandler(BattleSetTeamPokemonHandler)
        registerHandler(BattleSwitchPokemonHandler)
        registerHandler(BattleMessageHandler)
        registerHandler(BattleApplyCaptureResponseHandler)
        registerHandler(BattleCaptureStartHandler)
        registerHandler(BattleCaptureShakeHandler)
        registerHandler(BattleCaptureEndHandler)
        registerHandler(ChallengeNotificationHandler)
        registerHandler(BattleUpdateTeamPokemonHandler)

        // Settings
        registerHandler(ServerSettingsPacketHandler)
    }
}

