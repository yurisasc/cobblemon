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
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.battles.dispatch.WaitDispatch
import com.cobblemon.mod.common.net.messages.client.battle.BattleHealthChangePacket
import com.cobblemon.mod.common.pokemon.evolution.progress.DamageTakenEvolutionProgress
import com.cobblemon.mod.common.pokemon.evolution.progress.RecoilEvolutionProgress
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.util.lang
import kotlin.math.roundToInt
import net.minecraft.text.Text

class DamageInstruction(
    val actor: BattleActor,
    val publicMessage: BattleMessage,
    val privateMessage: BattleMessage
) : InterpreterInstruction {
    override fun invoke(battle: PokemonBattle) {
        val battlePokemon = publicMessage.getBattlePokemon(0, battle) ?: return
        if (privateMessage.optionalArgument("from")?.equals("recoil", true) == true) {
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
        val sourceName = privateMessage.getSourceBattlePokemon(battle)?.getName() ?: Text.literal("UNKOWN")
        ShowdownInterpreter.broadcastOptionalAbility(battle, effect, sourceName)

        battle.dispatch {
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

            var causedFaint = false
            if (newHealth == "0") {
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
            if (causedFaint) {
                GO
            } else {
                WaitDispatch(1F)
            }
        }
    }
}