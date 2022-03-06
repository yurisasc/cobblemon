package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.AIBattleActor
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import net.minecraft.network.chat.MutableComponent
import java.util.UUID

class TrainerBattleActor(
    val trainerName: String,
    showdownId: String,
    gameId: UUID,
    pokemonList: List<BattlePokemon>,
    artificialDecider: BattleAI
) : AIBattleActor(showdownId, gameId, pokemonList, artificialDecider) {
    override fun getName() = trainerName.text()
}