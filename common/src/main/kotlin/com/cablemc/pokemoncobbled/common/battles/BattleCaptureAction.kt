package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.scheduling.taskBuilder
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import java.util.concurrent.CompletableFuture
import net.minecraft.entity.LivingEntity

/**
 * Wrapper object for an attempt at capturing a wild Pokémon during a battle.
 *
 * @author Hiroku
 * @since July 2nd, 2022
 */
class BattleCaptureAction(
    val battle: PokemonBattle,
    val throwingActor: BattleActor,
    val throwingEntity: LivingEntity,
    val targetPokemon: ActiveBattlePokemon,
    val pokeBall: PokeBall
) {
    var shakes = -1
    var pokeBallEntity: EmptyPokeBallEntity? = null
    var pokemonEntity: PokemonEntity? = null
    var pokemon: Pokemon? = null
    var isSuccessful = false
    var lastShakeDirection = true

    val shakeEmitter = SimpleObservable<Boolean>()
    val capturedFuture = CompletableFuture<Boolean>()

    fun begin(pokemon: Pokemon, skipEntity: Boolean = false) {
        this.pokemon = pokemon
        this.pokemonEntity = pokemon.entity

        if (this.pokemonEntity == null || skipEntity) {
            // Do it without a PokéBall
            this.calculateShakes()
            taskBuilder()
                .iterations(shakes + 1)
                .delay(EmptyPokeBallEntity.SECONDS_BEFORE_SHAKE)
                .interval(EmptyPokeBallEntity.SECONDS_BETWEEN_SHAKES)
                .execute {
                    if (shakes == 0) {
                        capturedFuture.complete(isSuccessful)
                        battle.captureActions.remove(this)
                    } else {
                        shakes--
                        lastShakeDirection = !lastShakeDirection
                        shakeEmitter.emit(lastShakeDirection)
                    }
                }
                .build()
        } else {
            val pokemonEntity = this.pokemonEntity!!
            val directionToTarget = pokemonEntity.eyePos.subtract(this.throwingEntity.eyePos)
            // Create the pokeball and attach the shake emitter
        }


    }

    fun calculateShakes() {
        val captureResult = PokemonCobbled.captureCalculator.processCapture(throwingEntity, pokemon!!, pokeBall)

        var rollsRemaining = captureResult.numberOfShakes
        if (rollsRemaining == 4) {
            rollsRemaining--
        }

        isSuccessful = captureResult.isSuccessfulCapture
        shakes = rollsRemaining
    }

    fun finish() {

    }
}
