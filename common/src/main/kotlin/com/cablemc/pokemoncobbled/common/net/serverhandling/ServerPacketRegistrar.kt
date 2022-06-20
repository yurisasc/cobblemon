package com.cablemc.pokemoncobbled.common.net.serverhandling

import com.cablemc.pokemoncobbled.common.net.SidedPacketRegistrar
import com.cablemc.pokemoncobbled.common.net.serverhandling.battle.BattleSelectActionsHandler
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.BenchMoveHandler
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.RequestMoveSwapHandler
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.SendOutPokemonHandler
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.party.MovePartyPokemonHandler
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.party.SwapPartyPokemonHandler
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.pc.MovePCPokemonHandler
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.pc.MovePCPokemonToPartyHandler
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.pc.MovePartyPokemonToPCHandler
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.pc.SwapPCPokemonHandler

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
        registerHandler(BattleSelectActionsHandler)

        // PC actions
        registerHandler(MovePCPokemonHandler)
        registerHandler(SwapPCPokemonHandler)
        registerHandler(MovePCPokemonToPartyHandler)
        registerHandler(MovePartyPokemonToPCHandler)

        // Party actions
        registerHandler(MovePartyPokemonHandler)
        registerHandler(SwapPartyPokemonHandler)
    }
}