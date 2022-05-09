package com.cablemc.pokemoncobbled.common.client.net

import com.cablemc.pokemoncobbled.common.client.net.gui.SummaryUIPacketHandler
import com.cablemc.pokemoncobbled.common.client.net.pokemon.update.EvolutionUpdatePacketHandler
import com.cablemc.pokemoncobbled.common.client.net.pokemon.update.SingleUpdatePacketHandler
import com.cablemc.pokemoncobbled.common.client.net.storage.party.*
import com.cablemc.pokemoncobbled.common.net.SidedPacketRegistrar
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.*
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution.AddEvolutionPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution.ClearEvolutionsPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution.RemoveEvolutionPacket

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
        registerHandler<LevelUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<SpeciesUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<FriendshipUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<PokemonStateUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<ShinyUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<NatureUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<MoveSetUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<HealthUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler<ExperienceUpdatePacket>(SingleUpdatePacketHandler())
        registerHandler(InitializePartyHandler)
        registerHandler(SetPartyPokemonHandler)
        registerHandler(MovePartyPokemonHandler)
        registerHandler(RemovePartyPokemonHandler)
        registerHandler(SwapPartyPokemonHandler)
        registerHandler(SetPartyReferenceHandler)
        registerHandler(SummaryUIPacketHandler)
        registerHandler<AddEvolutionPacket>(EvolutionUpdatePacketHandler())
        registerHandler<RemoveEvolutionPacket>(EvolutionUpdatePacketHandler())
        registerHandler<ClearEvolutionsPacket>(EvolutionUpdatePacketHandler())
    }
}

