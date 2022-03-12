package com.cablemc.pokemoncobbled.common.api.battles.model.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.battles.ActiveBattlePokemon
import com.cablemc.pokemoncobbled.common.battles.BattleSide
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import java.util.UUID
import java.util.concurrent.CompletableFuture

abstract class BattleActor(
    val uuid: UUID,
    val pokemonList: List<BattlePokemon>
) {
    init {
        pokemonList.forEach { it.actor = this }
    }

    lateinit var showdownId: String
    lateinit var battle: PokemonBattle

    val activePokemon = mutableListOf<ActiveBattlePokemon>()
    var canDynamax = false
    val toChoose = mutableListOf<ActiveBattlePokemon>()

    fun getSide() = if (this in battle.side1.actors) battle.side1 else battle.side2

    abstract fun getName(): MutableComponent
    abstract fun getChoices(activePokemon: Iterable<ActiveBattlePokemon>): CompletableFuture<Iterable<String>>
    abstract fun getSwitch(activePokemon: Iterable<ActiveBattlePokemon>): CompletableFuture<Iterable<UUID>>
    open fun sendMessage(component: Component) {}
}