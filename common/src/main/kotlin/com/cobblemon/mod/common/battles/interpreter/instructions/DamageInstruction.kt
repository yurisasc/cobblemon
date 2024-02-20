/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.battles.ShowdownInterpreter
import com.cobblemon.mod.common.battles.dispatch.GO
import com.cobblemon.mod.common.battles.dispatch.InstructionSet
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.battles.dispatch.UntilDispatch
import com.cobblemon.mod.common.battles.dispatch.WaitDispatch
import com.cobblemon.mod.common.net.messages.client.animation.PlayPoseableAnimationPacket
import com.cobblemon.mod.common.net.messages.client.battle.BattleHealthChangePacket
import com.cobblemon.mod.common.pokemon.evolution.progress.DamageTakenEvolutionProgress
import com.cobblemon.mod.common.pokemon.evolution.progress.RecoilEvolutionProgress
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.util.lang
import java.util.concurrent.CompletableFuture
import kotlin.math.roundToInt
import net.minecraft.text.Text

class DamageInstruction(
    val instructionSet: InstructionSet,
    val actor: BattleActor,
    val publicMessage: BattleMessage,
    val privateMessage: BattleMessage
) : InterpreterInstruction {
    var future = CompletableFuture.completedFuture(Unit)
    val battlePokemon = publicMessage.getBattlePokemon(0, actor.battle)

    override fun invoke(battle: PokemonBattle) {
        battlePokemon ?: return
        val recoiling = privateMessage.optionalArgument("from")?.equals("recoil", true) == true
        val lastCauser  = instructionSet.getMostRecentCauser(comparedTo = this)
        if (recoiling) {
            battlePokemon.effectedPokemon.let { pokemon ->
                if (RecoilEvolutionProgress.supports(pokemon)) {
                    val newPercentage = privateMessage.argumentAt(1)?.split("/")?.getOrNull(0)?.toIntOrNull() ?: 0
                    val newHealth = (pokemon.hp * (newPercentage / 100.0)).roundToInt()
                    val difference = pokemon.currentHealth - newHealth
                    if (difference > 0) {
                        val progress = pokemon.evolutionProxy.current().progressFirstOrCreate({ it is RecoilEvolutionProgress }) { RecoilEvolutionProgress() }
                        progress.updateProgress(RecoilEvolutionProgress.Progress(progress.currentProgress().recoil + difference))
                    }
                }
            }
        }
        val newHealth = privateMessage.argumentAt(1)?.split(" ")?.get(0) ?: return
        val effect = privateMessage.effect()
        val pokemonName = battlePokemon.getName()
        val sourceName = privateMessage.getSourceBattlePokemon(battle)?.getName() ?: Text.literal("UNKNOWN")
        var causedFaint = newHealth == "0"

        battle.dispatch {
            if (lastCauser is MoveInstruction && recoiling) {
                UntilDispatch { "recoil" !in lastCauser.holds }
            } else {
                GO
            }
        }

        battle.dispatch {
            if (!causedFaint) {

                battlePokemon.entity?.let { pokemonEntity ->
                    val pkt = PlayPoseableAnimationPacket(pokemonEntity.id, setOf("recoil"), emptySet())
                    pkt.sendToPlayersAround(
                        x = pokemonEntity.x,
                        y = pokemonEntity.y,
                        z = pokemonEntity.z,
                        worldKey = pokemonEntity.world.registryKey,
                        distance = 50.0
                    )
                }
            }


            ShowdownInterpreter.broadcastOptionalAbility(battle, effect, sourceName)
            val newHealthRatio: Float
            val remainingHealth = newHealth.split("/")[0].toInt()

            if (effect != null) {
                val lang = when (effect.id) {
                    "blacksludge", "stickybarb" -> battleLang("damage.item", pokemonName, effect.typelessData)
                    "brn", "psn", "tox" -> {
                        val status = Statuses.getStatus(effect.id)?.name?.path ?: return@dispatch GO
                        lang("status.$status.hurt", pokemonName)
                    }
                    "aftermath" -> battleLang("damage.generic", pokemonName)
                    "chloroblast", "steelbeam" -> battleLang("damage.mindblown", pokemonName)
                    "jumpkick" -> battleLang("damage.highjumpkick", pokemonName)
                    else -> battleLang("damage.${effect.id}", pokemonName, sourceName)
                }
                battle.broadcastChatMessage(lang.red())
            }

            if (causedFaint) {
                newHealthRatio = 0F
                battle.dispatch {
                    battlePokemon.effectedPokemon.currentHealth = 0
                    battlePokemon.sendUpdate()
                    GO
                }
                causedFaint = true
            } else {
                val maxHealth = newHealth.split("/")[1].toInt()
                val difference = maxHealth - remainingHealth
                newHealthRatio = remainingHealth.toFloat() / maxHealth
                battle.dispatchToFront {
                    battlePokemon.effectedPokemon.currentHealth = remainingHealth
                    if (difference > 0) {
                        battlePokemon.effectedPokemon.let { pokemon ->
                            if (DamageTakenEvolutionProgress.supports(pokemon)) {
                                val progress = pokemon.evolutionProxy.current().progressFirstOrCreate({ it is DamageTakenEvolutionProgress }) { DamageTakenEvolutionProgress() }
                                progress.updateProgress(DamageTakenEvolutionProgress.Progress(progress.currentProgress().amount + difference))
                            }
                        }
                    }
                    battlePokemon.sendUpdate()
                    GO
                }
            }
            privateMessage.pnxAndUuid(0)?.let { (pnx, _) -> battle.sendSidedUpdate(actor, BattleHealthChangePacket(pnx, remainingHealth.toFloat()), BattleHealthChangePacket(pnx, newHealthRatio)) }

            battle.minorBattleActions[battlePokemon.uuid] = privateMessage

            // If they faint from this damage then don't bother waiting for the damage to be applied.
            if (lastCauser is MoveInstruction && lastCauser.actionEffect != null && !causedFaint) {
                UntilDispatch { lastCauser.future.isDone }
            } else if (causedFaint) {
                GO
            } else {
                WaitDispatch(1F)
            }
        }
    }
}