/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.serverhandling

import com.cablemc.pokemod.common.net.SidedPacketRegistrar
import com.cablemc.pokemod.common.net.messages.server.pokemon.update.evolution.AcceptEvolutionPacket
import com.cablemc.pokemod.common.net.serverhandling.battle.BattleSelectActionsHandler
import com.cablemc.pokemod.common.net.serverhandling.evolution.EvolutionDisplayUpdatePacketHandler
import com.cablemc.pokemod.common.net.serverhandling.starter.RequestStarterScreenHandler
import com.cablemc.pokemod.common.net.serverhandling.starter.SelectStarterPacketHandler
import com.cablemc.pokemod.common.net.serverhandling.storage.BenchMoveHandler
import com.cablemc.pokemod.common.net.serverhandling.storage.RequestMoveSwapHandler
import com.cablemc.pokemod.common.net.serverhandling.storage.SendOutPokemonHandler
import com.cablemc.pokemod.common.net.serverhandling.storage.SwapPCPartyPokemonHandler
import com.cablemc.pokemod.common.net.serverhandling.storage.party.MovePartyPokemonHandler
import com.cablemc.pokemod.common.net.serverhandling.storage.party.SwapPartyPokemonHandler
import com.cablemc.pokemod.common.net.serverhandling.storage.pc.MovePCPokemonHandler
import com.cablemc.pokemod.common.net.serverhandling.storage.pc.MovePCPokemonToPartyHandler
import com.cablemc.pokemod.common.net.serverhandling.storage.pc.MovePartyPokemonToPCHandler
import com.cablemc.pokemod.common.net.serverhandling.storage.pc.SwapPCPokemonHandler

/**
 * Registers packet handlers that the server will need. This is separated from the client ones
 * not because they have to be, but because it helps us guarantee client access safety in a CI
 * job.
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
object ServerPacketRegistrar : SidedPacketRegistrar() {
    override fun registerHandlers() {
        // Don't forget to register packets in CobbledNetwork!

        registerHandler(SendOutPokemonHandler)
        registerHandler(RequestMoveSwapHandler)
        registerHandler(BenchMoveHandler)
        registerHandler(ChallengeHandler)
        registerHandler<AcceptEvolutionPacket>(EvolutionDisplayUpdatePacketHandler())
        registerHandler(BattleSelectActionsHandler)
        registerHandler(SelectStarterPacketHandler)

        // PC actions
        registerHandler(MovePCPokemonHandler)
        registerHandler(SwapPCPokemonHandler)
        registerHandler(MovePCPokemonToPartyHandler)
        registerHandler(MovePartyPokemonToPCHandler)

        // Party actions
        registerHandler(MovePartyPokemonHandler)
        registerHandler(SwapPartyPokemonHandler)

        // PC and Party actions :)
        registerHandler(SwapPCPartyPokemonHandler)

        registerHandler(RequestStarterScreenHandler)
    }
}