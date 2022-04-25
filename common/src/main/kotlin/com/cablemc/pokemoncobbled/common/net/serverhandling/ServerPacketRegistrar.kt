package com.cablemc.pokemoncobbled.common.net.serverhandling

import com.cablemc.pokemoncobbled.common.net.SidedPacketRegistrar
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.BenchMoveHandler
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.RequestMoveSwapHandler
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.SendOutPokemonHandler

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
    }
}