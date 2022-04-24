package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import com.cablemc.pokemoncobbled.common.battles.actor.PlayerBattleActor
import com.cablemc.pokemoncobbled.common.battles.actor.PokemonBattleActor
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.battleLang
import com.cablemc.pokemoncobbled.common.util.getPlayer
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import java.util.Optional
import java.util.UUID

class BattleBuilder {
    companion object {
        fun pvp1v1(
            player1: ServerPlayer,
            player2: ServerPlayer,
            battleFormat: BattleFormat = BattleFormat.GEN_8_SINGLES,
            cloneParties: Boolean = false,
            healFirst: Boolean = false,
            partyAccessor: (ServerPlayer) -> PartyStore = { it.party() }
        ): BattleStartResult {
            val team1 = partyAccessor(player1).toBattleTeam(clone = cloneParties, checkHealth = !healFirst)
            val team2 = partyAccessor(player2).toBattleTeam(clone = cloneParties, checkHealth = !healFirst)

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
                SuccessfulBattleStart(
                    BattleRegistry.startBattle(
                        battleFormat = battleFormat,
                        side1 = BattleSide(player1Actor),
                        side2 = BattleSide(player2Actor)
                    )
                )
            } else {
                errors
            }
        }

        fun pve(
            player: ServerPlayer,
            pokemonEntity: PokemonEntity,
            leadingPokemon: UUID? = null,
            battleFormat: BattleFormat = BattleFormat.GEN_8_SINGLES,
            cloneParties: Boolean = false,
            healFirst: Boolean = false,
            partyAccessor: (ServerPlayer) -> PartyStore = { it.party() }
        ): BattleStartResult {
            val playerTeam = partyAccessor(player).toBattleTeam(clone = cloneParties, checkHealth = !healFirst, leadingPokemon = leadingPokemon)
            val playerActor = PlayerBattleActor(player.uuid, playerTeam)
            val wildActor = PokemonBattleActor(pokemonEntity.pokemon.uuid, BattlePokemon(pokemonEntity.pokemon))
            val errors = ErroredBattleStart()

            if (playerActor.pokemonList.size < battleFormat.battleType.slotsPerActor) {
                errors.participantErrors[playerActor] += BattleStartError.insufficientPokemon(
                    player = player,
                    requiredCount = battleFormat.battleType.slotsPerActor,
                    hadCount = playerActor.pokemonList.size
                )
            }

            for (actor in arrayOf(playerActor, wildActor)) {
                if (BattleRegistry.getBattleByParticipatingPlayer(player) != null) {
                    errors.participantErrors[actor] += BattleStartError.alreadyInBattle(actor)
                }
            }

            return if (errors.isEmpty) {
                val battle = BattleRegistry.startBattle(
                    battleFormat = battleFormat,
                    side1 = BattleSide(playerActor),
                    side2 = BattleSide(wildActor)
                )
                if (!cloneParties) {
                    pokemonEntity.battleId.set(Optional.of(battle.battleId))
                }
                SuccessfulBattleStart(battle)
            } else {
                errors
            }
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
    val message: MutableComponent

    companion object {
        fun alreadyInBattle(player: ServerPlayer) = AlreadyInBattleError(player.displayName)
        fun alreadyInBattle(pokemonEntity: PokemonEntity) = AlreadyInBattleError(pokemonEntity.displayName)
        fun alreadyInBattle(actor: BattleActor) = AlreadyInBattleError(actor.getName())
        fun insufficientPokemon(
            player: ServerPlayer,
            requiredCount: Int,
            hadCount: Int
        ) = InsufficientPokemonError(player, requiredCount, hadCount)
    }
}

enum class CommonBattleStartError : BattleStartError {

}


open class SimpleBattleStartError(override val message: MutableComponent) : BattleStartError {
    constructor(message: String) : this(message.asTranslated())
}

class InsufficientPokemonError(
    val player: ServerPlayer,
    val requiredCount: Int,
    val hadCount: Int
) : SimpleBattleStartError(
    battleLang(
        "error.insufficient-pokemon",
        player.displayName,
        requiredCount,
        hadCount
    )
)
class AlreadyInBattleError(name: Component): SimpleBattleStartError(battleLang("error.in-battle", name))

open class BattleActorErrors : HashMap<BattleActor, Set<BattleStartError>>() {
    protected val map = mutableMapOf<BattleActor, MutableSet<BattleStartError>>()

    override operator fun get(key: BattleActor): MutableSet<BattleStartError> {
        return map[key] ?: mutableSetOf<BattleStartError>().also { map[key] = it }
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

    fun sendTo(entity: Entity, transformer: (MutableComponent) -> (MutableComponent) = { it }) {
        errors.forEach { entity.sendServerMessage(transformer(it.message)) }
    }

    inline fun <reified T : BattleStartError> ifHasError(action: () -> Unit): ErroredBattleStart {
        if (errors.filterIsInstance<T>().isNotEmpty()) {
            action()
        }
        return this
    }

    val isEmpty: Boolean
        get() = generalErrors.isEmpty() && participantErrors.values.all { it.isEmpty() }

    fun isPlayerToBlame(player: ServerPlayer) = generalErrors.isEmpty()
        && participantErrors.size == 1
        && participantErrors.entries.first().let { it.key.uuid == player.uuid }

    fun isSomePlayerToBlame() = generalErrors.isEmpty() && participantErrors.isNotEmpty()

    val playersToBlame: Iterable<ServerPlayer>
        get() = participantErrors.keys.mapNotNull { it.uuid.getPlayer() }

    val actorsToBlame: Iterable<BattleActor>
        get() = participantErrors.keys

    val errors: Iterable<BattleStartError>
        get() = generalErrors + participantErrors.flatMap { it.value }
}