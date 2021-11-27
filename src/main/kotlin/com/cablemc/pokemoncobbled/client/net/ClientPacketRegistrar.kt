package com.cablemc.pokemoncobbled.client.net

import com.cablemc.pokemoncobbled.common.api.net.SidedPacketRegistrar

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
        registerHandler(PokemonUpdateHandler)
    }
}

