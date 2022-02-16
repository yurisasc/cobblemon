package com.cablemc.pokemoncobbled.forge.common.net.serverhandling

import com.cablemc.pokemoncobbled.forge.common.api.net.SidedPacketRegistrar
import com.cablemc.pokemoncobbled.forge.common.net.serverhandling.storage.SendOutPokemonHandler
import com.cablemc.pokemoncobbled.forge.common.net.serverhandling.storage.RequestMoveSwapHandler

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
        registerHandler(SendOutPokemonHandler)
        registerHandler(RequestMoveSwapHandler)
    }
}