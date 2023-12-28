/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.abilities.Abilities
import com.cobblemon.mod.common.api.abilities.Ability
import com.cobblemon.mod.common.api.abilities.AbilityPool
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.battles.BattleStartedPreEvent
import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.storage.party.PartyStore
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor
import com.cobblemon.mod.common.battles.actor.TrainerBattleActor
import com.cobblemon.mod.common.battles.ai.RandomBattleAI
import com.cobblemon.mod.common.battles.ai.StrongBattleAI
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.util.getBattleTheme
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemon.mod.common.util.party
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.E

object BattleBuilder {
    @JvmOverloads
    fun pvp1v1(
        player1: ServerPlayerEntity,
        player2: ServerPlayerEntity,
        leadingPokemonPlayer1: UUID? = null,
        leadingPokemonPlayer2: UUID? = null,
        battleFormat: BattleFormat = BattleFormat.GEN_9_SINGLES,
        cloneParties: Boolean = false,
        healFirst: Boolean = false,
        partyAccessor: (ServerPlayerEntity) -> PartyStore = { it.party() }
    ): BattleStartResult {
        val team1 = partyAccessor(player1).toBattleTeam(clone = cloneParties, checkHealth = !healFirst, leadingPokemonPlayer1)
        val team2 = partyAccessor(player2).toBattleTeam(clone = cloneParties, checkHealth = !healFirst, leadingPokemonPlayer2)

        val player1Actor = PlayerBattleActor(player1.uuid, team1)
        val player2Actor = PlayerBattleActor(player2.uuid, team2)

        val errors = ErroredBattleStart()

        for ((player, actor) in arrayOf(player1 to player1Actor, player2 to player2Actor)) {
            if (actor.pokemonList.size < battleFormat.battleType.slotsPerActor) {
                errors.participantErrors[actor] += BattleStartError.insufficientPokemon(
                    player = player,
                    requiredCount = battleFormat.battleType.slotsPerActor,
                    hadCount = actor.pokemonList.size
                )
            }

            if (BattleRegistry.getBattleByParticipatingPlayer(player) != null) {
                errors.participantErrors[actor] += BattleStartError.alreadyInBattle(player)
            }
        }

        return if (errors.isEmpty) {
            BattleRegistry.startBattle(
                battleFormat = battleFormat,
                side1 = BattleSide(player1Actor),
                side2 = BattleSide(player2Actor)
            ).ifSuccessful {
                player1Actor.battleTheme = player2.getBattleTheme()
                player2Actor.battleTheme = player1.getBattleTheme()
            }
        } else {
            errors
        }
    }

    /**
     * Attempts to create a PvE battle against the given Pokémon.
     *
     * @param player The player battling the wild Pokémon.
     * @param pokemonEntity The Pokémon to battle.
     * @param leadingPokemon The Pokémon in the player's party to send out first. If null, it uses the first in the party.
     * @param battleFormat The format to use for the battle. By default it is [BattleFormat.GEN_9_SINGLES].
     * @param cloneParties Whether the player's party should be cloned so that damage will not affect their party afterwards. Defaults to false.
     * @param healFirst Whether the player's Pokémon should be healed before the battle starts. Defaults to false.
     * @param fleeDistance How far away the player must get to flee the Pokémon. If the value is -1, it cannot be fled.
     * @param party The party of the player to use for the battle. This does not need to be their actual party. Defaults to it though.
     */
    @JvmOverloads
    fun pve(
        player: ServerPlayerEntity,
        pokemonEntity: PokemonEntity,
        leadingPokemon: UUID? = null,
        battleFormat: BattleFormat = BattleFormat.GEN_9_SINGLES,
        cloneParties: Boolean = false,
        healFirst: Boolean = false,
        fleeDistance: Float = Cobblemon.config.defaultFleeDistance,
        party: PartyStore = player.party()
    ): BattleStartResult {
        val playerTeam = party.toBattleTeam(clone = cloneParties, checkHealth = !healFirst, leadingPokemon = leadingPokemon)
        val playerActor = PlayerBattleActor(player.uuid, playerTeam)
        val wildActor = PokemonBattleActor(pokemonEntity.pokemon.uuid, BattlePokemon(pokemonEntity.pokemon), fleeDistance)
        val errors = ErroredBattleStart()

        if (playerActor.pokemonList.size < battleFormat.battleType.slotsPerActor) {
            errors.participantErrors[playerActor] += BattleStartError.insufficientPokemon(
                player = player,
                requiredCount = battleFormat.battleType.slotsPerActor,
                hadCount = playerActor.pokemonList.size
            )
        }

        if (BattleRegistry.getBattleByParticipatingPlayer(player) != null) {
            errors.participantErrors[playerActor] += BattleStartError.alreadyInBattle(playerActor)
        }

        if (pokemonEntity.battleId.get().isPresent) {
            errors.participantErrors[wildActor] += BattleStartError.alreadyInBattle(wildActor)
        }

        return if (errors.isEmpty) {
            BattleRegistry.startBattle(
                battleFormat = battleFormat,
                side1 = BattleSide(playerActor),
                side2 = BattleSide(wildActor)
            ).ifSuccessful {
                if (!cloneParties) {
                    pokemonEntity.battleId.set(Optional.of(it.battleId))
                }
                playerActor.battleTheme = pokemonEntity.getBattleTheme()
            }
        } else {
            errors
        }
    }

    // player versus computer testing
    @JvmOverloads
    fun pvc(player: ServerPlayerEntity,
            battleAI: String,
            battleLevel: Int = 50,
            teamTyping: String = "random",
            cloneParties: Boolean = false,
            npcParty: MutableList<BattlePokemon> = mutableListOf(),
            battleFormat: BattleFormat = BattleFormat.GEN_9_SINGLES,
            healFirst: Boolean = false,
            playerParty: PartyStore = player.party()
    ): BattleStartResult {
        // choose battleAI AI type
        val battleAIType = when (battleAI) {
            "strong" -> StrongBattleAI()
            else -> RandomBattleAI()
        }

        val playerTeam = playerParty.toBattleTeam(clone = cloneParties, checkHealth = !healFirst, leadingPokemon = null)
        val playerActor = PlayerBattleActor(player.uuid, playerTeam)

        if (npcParty.isEmpty()) {
            when (teamTyping.lowercase()) {
                "random" -> {
                    repeat(6) {
                        // todo generate random party of 6 BattlePokemon
                        val npcPokemon = Pokemon()

                        npcPokemon.uuid = UUID.randomUUID()
                        npcPokemon.level = battleLevel
                        npcPokemon.initialize() // This will generate everything else about the pokemon

                        npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                    }
                }
                "cynthia" -> {
                    // Spiritomb
                    var npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("spiritomb")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,6)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.SITRUS_BERRY), false) //sitrus berry
                    npcPokemon.ability = Abilities.get("pressure")!!.create()
                    npcPokemon.setMoveset(listOf("darkpulse", "shadowball", "willowisp", "suckerpunch"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    //PokemonProperties.parse() // todo try to use this for smaller code later

                    // Porygon-Z
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("porygonz")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,6)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false)  //expert belt
                    npcPokemon.ability = Abilities.get("adaptability")!!.create()
                    npcPokemon.setMoveset(listOf("hyperbeam", "shadowball", "icebeam", "thunderbolt"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Togekiss
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("togekiss")!!
                    npcPokemon.nature = Natures.getNature("timid")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,6)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    //npcPokemon.item  leftovers
                    npcPokemon.ability = Abilities.get("serenegrace")!!.create()
                    npcPokemon.setMoveset(listOf("airslash", "dazzlinggleam", "aurasphere", "thunderwave"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Lucario
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("lucario")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,6)
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.LIFE_ORB), false)  //focus sash
                    npcPokemon.ability = Abilities.get("innerfocus")!!.create()
                    npcPokemon.setMoveset(listOf("closecombat", "meteormash", "earthquake", "extremespeed"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Milotic
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("milotic")!!
                    npcPokemon.nature = Natures.getNature("bold")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,252)
                    npcPokemon.setEV(Stats.DEFENCE,252)
                    npcPokemon.setEV(Stats.SPECIAL_ATTACK,6)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.FLAME_ORB), false) //  focus sash
                    npcPokemon.ability = Abilities.get("marvelscale")!!.create()
                    npcPokemon.setMoveset(listOf("recover", "mirrorcoat", "icebeam", "scald"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))

                    // Garchomp
                    npcPokemon = Pokemon()
                    npcPokemon.uuid = UUID.randomUUID()
                    npcPokemon.species = PokemonSpecies.getByName("garchomp")!!
                    npcPokemon.nature = Natures.getNature("jolly")!!
                    // IVs
                    npcPokemon.setIV(Stats.HP, 31)
                    npcPokemon.setIV(Stats.ATTACK, 31)
                    npcPokemon.setIV(Stats.DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPECIAL_ATTACK, 31)
                    npcPokemon.setIV(Stats.SPECIAL_DEFENCE, 31)
                    npcPokemon.setIV(Stats.SPEED, 31)
                    // EVs
                    npcPokemon.setEV(Stats.HP,6)
                    npcPokemon.setEV(Stats.ATTACK,252)
                    npcPokemon.setEV(Stats.SPEED,252)
                    npcPokemon.level = battleLevel
                    npcPokemon.swapHeldItem(ItemStack(CobblemonItems.YACHE_BERRY), false)  //focus sash
                    npcPokemon.ability = Abilities.get("roughskin")!!.create()
                    npcPokemon.setMoveset(listOf("dragonclaw", "earthquake", "swordsdance", "poisonjab"))

                    // add to party
                    npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                }
                else -> {
                    val typing = ElementalTypes.get(teamTyping.lowercase())

                    repeat(6) {
                        // todo generate random party of 6 BattlePokemon
                        val npcPokemon = Pokemon()

                        npcPokemon.uuid = UUID.randomUUID()
                        npcPokemon.level = battleLevel
                        npcPokemon.initialize(typing!!) // This will generate everything else about the pokemon

                        npcParty.add(BattlePokemon.safeCopyOf(npcPokemon))
                    }
                }
            }

        }


        val npcUUID = UUID.randomUUID()

        val npcActor = when (teamTyping.lowercase()) {
            "cynthia" -> TrainerBattleActor("Cynthia", npcUUID, npcParty.toList(), battleAIType)
            else -> TrainerBattleActor("Master of Crabs", npcUUID, npcParty.toList(), battleAIType)
        }

        //val wildActor = PokemonBattleActor(pokemonEntity.pokemon.uuid, BattlePokemon(pokemonEntity.pokemon), fleeDistance)
        val errors = ErroredBattleStart()

        // todo maybe use this error to check for no pokemon in NPC trainer's team
        if (playerActor.pokemonList.size < battleFormat.battleType.slotsPerActor) {
            errors.participantErrors[playerActor] += BattleStartError.insufficientPokemon(
                    player = player,
                    requiredCount = battleFormat.battleType.slotsPerActor,
                    hadCount = playerActor.pokemonList.size
            )
        }

        if (BattleRegistry.getBattleByParticipatingPlayer(player) != null) {
            errors.participantErrors[playerActor] += BattleStartError.alreadyInBattle(playerActor)
        }

        /*if (pokemonEntity.battleId.get().isPresent) {
            errors.participantErrors[npcActor] += BattleStartError.alreadyInBattle(npcActor)
        }*/

        return if (errors.isEmpty) {
            BattleRegistry.startBattle(
                    battleFormat = battleFormat,
                    side1 = BattleSide(playerActor),
                    side2 = BattleSide(npcActor)
            ).ifSuccessful {
                if (!cloneParties) {
                    // todo I don't think I need this
                    //pokemonEntity.battleId.set(Optional.of(it.battleId))
                }
                // todo set the NPC trainer battle music
                //playerActor.battleTheme = pokemonEntity.getBattleTheme()
            }
        } else {
            errors
        }
    }
}

abstract class BattleStartResult {
    open fun ifSuccessful(action: (PokemonBattle) -> Unit): BattleStartResult {
        return this
    }

    open fun ifErrored(action: (ErroredBattleStart) -> Unit): BattleStartResult {
        return this
    }
}
class SuccessfulBattleStart(
    val battle: PokemonBattle
) : BattleStartResult() {
    override fun ifSuccessful(action: (PokemonBattle) -> Unit): BattleStartResult {
        action(battle)
        return this
    }
}

interface BattleStartError {

    fun getMessageFor(entity: Entity): MutableText

    companion object {
        fun alreadyInBattle(player: ServerPlayerEntity) = AlreadyInBattleError(player.uuid, player.displayName)
        fun alreadyInBattle(pokemonEntity: PokemonEntity) = AlreadyInBattleError(pokemonEntity.uuid, pokemonEntity.displayName)
        fun alreadyInBattle(actor: BattleActor) = AlreadyInBattleError(actor.uuid, actor.getName())

        fun targetIsBusy(targetName: MutableText) = BusyError(targetName)
        fun insufficientPokemon(
            player: ServerPlayerEntity,
            requiredCount: Int,
            hadCount: Int
        ) = InsufficientPokemonError(player, requiredCount, hadCount)

        fun canceledByEvent(reason: MutableText?) = CanceledError(reason)
    }
}

enum class CommonBattleStartError : BattleStartError {

}

class CanceledError(
    val reason: MutableText?
): BattleStartError {
    override fun getMessageFor(entity: Entity) = reason ?: battleLang("error.canceled")
}

class InsufficientPokemonError(
    val player: ServerPlayerEntity,
    val requiredCount: Int,
    val hadCount: Int
) : BattleStartError {
    override fun getMessageFor(entity: Entity): MutableText {
        return if (player == entity) {
            val key = if (hadCount == 0) "no_pokemon" else "insufficient_pokemon.personal"
            battleLang(
                "error.$key",
                requiredCount,
                hadCount
            )
        } else {
            battleLang(
                "error.insufficient_pokemon",
                player.displayName,
                requiredCount,
                hadCount
            )
        }
    }
}
class AlreadyInBattleError(
    val actorUUID: UUID,
    val name: Text
): BattleStartError {
    override fun getMessageFor(entity: Entity): MutableText {
        return if (actorUUID == entity.uuid) {
            battleLang("error.in_battle.personal")
        } else {
            battleLang("error.in_battle", name)
        }
    }
}
class BusyError(
    val targetName: MutableText
): BattleStartError {
    override fun getMessageFor(entity: Entity) = battleLang("errors.busy", targetName)
}

open class BattleActorErrors : HashMap<BattleActor, MutableSet<BattleStartError>>() {
    override operator fun get(key: BattleActor): MutableSet<BattleStartError> {
        return super.get(key) ?: mutableSetOf<BattleStartError>().also { this[key] = it }
    }
}

open class ErroredBattleStart(
    val generalErrors: MutableSet<BattleStartError> = mutableSetOf(),
    val participantErrors: BattleActorErrors = BattleActorErrors()
) : BattleStartResult() {
    override fun ifErrored(action: (ErroredBattleStart) -> Unit): BattleStartResult {
        action(this)
        return this
    }

    inline fun <reified T : BattleStartError> forError(action: (T) -> Unit): ErroredBattleStart {
        errors.filterIsInstance<T>().forEach { action(it) }
        return this
    }

    fun sendTo(entity: Entity, transformer: (MutableText) -> (MutableText) = { it }) {
        errors.forEach { entity.sendMessage(transformer(it.getMessageFor(entity))) }
    }

    inline fun <reified T : BattleStartError> ifHasError(action: () -> Unit): ErroredBattleStart {
        if (errors.filterIsInstance<T>().isNotEmpty()) {
            action()
        }
        return this
    }

    val isEmpty: Boolean
        get() = generalErrors.isEmpty() && participantErrors.values.all { it.isEmpty() }

    fun isPlayerToBlame(player: ServerPlayerEntity) = generalErrors.isEmpty()
        && participantErrors.size == 1
        && participantErrors.entries.first().let { it.key.uuid == player.uuid }

    fun isSomePlayerToBlame() = generalErrors.isEmpty() && participantErrors.isNotEmpty()

    val playersToBlame: Iterable<ServerPlayerEntity>
        get() = participantErrors.keys.mapNotNull { it.uuid.getPlayer() }

    val actorsToBlame: Iterable<BattleActor>
        get() = participantErrors.keys

    val errors: Iterable<BattleStartError>
        get() = generalErrors + participantErrors.flatMap { it.value }
}