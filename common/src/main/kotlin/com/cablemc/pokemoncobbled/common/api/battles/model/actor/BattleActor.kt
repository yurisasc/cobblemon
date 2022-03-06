package com.cablemc.pokemoncobbled.common.api.battles.model.actor

import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import java.util.UUID

abstract class BattleActor(
    val showdownId: String,
    val gameId: UUID,
    val pokemonList: List<BattlePokemon>
) {
    init {
        pokemonList.forEach { it.actor = this }
    }

    abstract fun getName(): MutableComponent
    open fun sendMessage(component: Component) {}
}