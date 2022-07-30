package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.AIBattleActor
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.ActorType
import com.cablemc.pokemoncobbled.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.battles.ai.RandomBattleAI
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import java.util.UUID
import net.minecraft.text.MutableText

class MultiPokemonBattleActor(
    pokemonList: List<BattlePokemon>,
    artificialDecider: BattleAI = RandomBattleAI(),
    uuid: UUID = UUID.randomUUID()
) : AIBattleActor(uuid, pokemonList, artificialDecider) {
    override fun getName(): MutableText = "Wild Pokémon".text() // TODO probably remove by making it nullable
    override val type = ActorType.WILD
}