package com.cablemc.pokemoncobbled.common.client.net

import com.cablemc.pokemoncobbled.common.client.net.battle.*
import com.cablemc.pokemoncobbled.common.client.net.gui.SummaryUIPacketHandler
import com.cablemc.pokemoncobbled.common.client.net.pokemon.update.EvolutionUpdatePacketHandler
import com.cablemc.pokemoncobbled.common.client.net.pokemon.update.SingleUpdatePacketHandler
import com.cablemc.pokemoncobbled.common.client.net.storage.RemoveClientPokemonHandler
import com.cablemc.pokemoncobbled.common.client.net.storage.SwapClientPokemonHandler
import com.cablemc.pokemoncobbled.common.client.net.storage.party.InitializePartyHandler
import com.cablemc.pokemoncobbled.common.client.net.storage.party.MoveClientPartyPokemonHandler
import com.cablemc.pokemoncobbled.common.client.net.storage.party.SetPartyPokemonHandler
import com.cablemc.pokemoncobbled.common.client.net.storage.party.SetPartyReferenceHandler
import com.cablemc.pokemoncobbled.common.client.net.storage.pc.ClosePCHandler
import com.cablemc.pokemoncobbled.common.client.net.storage.pc.InitializePCHandler
import com.cablemc.pokemoncobbled.common.client.net.storage.pc.MoveClientPCPokemonHandler
import com.cablemc.pokemoncobbled.common.client.net.storage.pc.OpenPCHandler
import com.cablemc.pokemoncobbled.common.client.net.storage.pc.SetPCBoxPokemonHandler
import com.cablemc.pokemoncobbled.common.client.net.storage.pc.SetPCPokemonHandler
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

        registerHandler(SummaryUIPacketHandler)
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
    }
}

