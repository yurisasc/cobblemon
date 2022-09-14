package com.cablemc.pokemoncobbled.common.battles.pokemon

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.moves.MoveSet
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.battles.actor.MultiPokemonBattleActor
import com.cablemc.pokemoncobbled.common.battles.actor.PokemonBattleActor
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleUpdateTeamPokemonPacket
import com.cablemc.pokemoncobbled.common.pokemon.IVs
import com.cablemc.pokemoncobbled.common.pokemon.Nature
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.battleLang
import java.util.UUID
import net.minecraft.text.MutableText

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
    val statChanges = mutableMapOf<Stat, Int>()
    var gone = false
    // etc

    val entity: PokemonEntity?
        get() = effectedPokemon.entity

    var willBeSwitchedIn = false

    /** A set of all the BattlePokemon that they faced during the battle (for exp purposes) */
    val facedOpponents = mutableSetOf<BattlePokemon>()

    open fun getName(): MutableText {
        return if (actor is PokemonBattleActor || actor is MultiPokemonBattleActor) {
            effectedPokemon.species.translatedName
        } else {
            battleLang("owned_pokemon", actor.getName(), effectedPokemon.species.translatedName)
        }
    }

    fun sendUpdate() {
        actor.sendUpdate(BattleUpdateTeamPokemonPacket(effectedPokemon))
    }

    fun isSentOut() = actor.battle.activePokemon.any { it.battlePokemon == this }
    fun canBeSentOut() = !isSentOut() && !willBeSwitchedIn && health > 0
}