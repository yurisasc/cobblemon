/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.battles.BattleStartedPreEvent
import com.cobblemon.mod.common.api.storage.party.PartyStore
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.cobblemon.mod.common.api.storage.player.PokedexPlayerData
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.net.messages.client.starter.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemon.mod.common.util.party
import java.util.Optional
import java.util.UUID
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text

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
            CobblemonEvents.BATTLE_STARTED_PRE.postThen(
                    BattleStartedPreEvent(listOf(player1Actor, player2Actor), battleFormat, true, false, false))
            {
                return SuccessfulBattleStart(
                        BattleRegistry.startBattle(
                                battleFormat = battleFormat,
                                side1 = BattleSide(player1Actor),
                                side2 = BattleSide(player2Actor)
                        )
                )
            }
            errors
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
            CobblemonEvents.BATTLE_STARTED_PRE.postThen(
                    BattleStartedPreEvent(listOf(playerActor, wildActor), battleFormat, false, false, true))
            {
                val battle = BattleRegistry.startBattle(
                        battleFormat = battleFormat,
                        side1 = BattleSide(playerActor),
                        side2 = BattleSide(wildActor)
                )
                if (!cloneParties) {
                    pokemonEntity.battleId.set(Optional.of(battle.battleId))
                }
                val pokedex = Cobblemon.playerDataManager.getPokedexData(player)
                pokedex.wildPokemonEncountered(pokemonEntity.pokemon)
                player.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedex.toClientData()))
                return SuccessfulBattleStart(battle)
            }
            errors
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
    }
}

enum class CommonBattleStartError : BattleStartError {

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