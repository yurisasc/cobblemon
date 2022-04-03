package com.cablemc.pokemoncobbled.common.battles.pokemon

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.moves.MoveSet
import com.cablemc.pokemoncobbled.common.battles.actor.MultiPokemonBattleActor
import com.cablemc.pokemoncobbled.common.battles.actor.PokemonBattleActor
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.IVs
import com.cablemc.pokemoncobbled.common.pokemon.Nature
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.battleLang
import net.minecraft.network.chat.MutableComponent
import java.util.UUID

open class BattlePokemon(
    val originalPokemon: Pokemon,
    val effectedPokemon: Pokemon = originalPokemon
) {
    lateinit var actor: BattleActor
    companion object {
        fun safeCopyOf(pokemon: Pokemon): BattlePokemon = BattlePokemon(
            originalPokemon = pokemon,
            effectedPokemon = pokemon.clone()
        )
    }

    val uuid: UUID
        get() = effectedPokemon.uuid
    val health: Int
        get() = effectedPokemon.currentHealth
    val maxHealth: Int
        get() = effectedPokemon.hp
    val ivs: IVs
        get() = effectedPokemon.ivs
    val nature: Nature
        get() = effectedPokemon.nature
    val moveSet: MoveSet
        get() = effectedPokemon.moveSet
    // etc

    val entity: PokemonEntity?
        get() = effectedPokemon.entity
    var willBeSwitchedIn = false

    val facedOpponents = mutableSetOf<BattlePokemon>()

    open fun getName(): MutableComponent {
        return if (actor is PokemonBattleActor || actor is MultiPokemonBattleActor) {
            effectedPokemon.species.translatedName
        } else {
            battleLang("owned_pokemon", actor.getName(), effectedPokemon.species.translatedName)
        }
    }

    fun isSentOut() = actor.battle.activePokemon.any { it.battlePokemon == this }
    fun canBeSentOut() = !isSentOut() && !willBeSwitchedIn && health > 0
}