/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.interpreter.instructions

import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.value.MoValue
import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addStandardFunctions
import com.cobblemon.mod.common.api.molang.MoLangFunctions.getQueryStruct
import com.cobblemon.mod.common.api.moves.animations.ActionEffectContext
import com.cobblemon.mod.common.api.moves.animations.UsersProvider
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.battles.ShowdownInterpreter
import com.cobblemon.mod.common.battles.dispatch.CauserInstruction
import com.cobblemon.mod.common.battles.dispatch.GO
import com.cobblemon.mod.common.battles.dispatch.InstructionSet
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.battles.dispatch.UntilDispatch
import com.cobblemon.mod.common.battles.dispatch.WaitDispatch
import com.cobblemon.mod.common.util.battleLang
import net.minecraft.text.Text
import java.util.concurrent.CompletableFuture

/**
 * Format: |-activate|POKEMON|EFFECT
 *
 * A miscellaneous EFFECT has activated for POKEMON. This is triggered whenever an effect could not be better described
 * by one of the other minor messages.
 *
 * @author Hunter
 * @since September 25th, 2022
 */
class ActivateInstruction(val instructionSet: InstructionSet, val message: BattleMessage) : InterpreterInstruction, CauserInstruction {
    var future = CompletableFuture.completedFuture(Unit)
    var holds = mutableSetOf<String>()

    override fun invoke(battle: PokemonBattle) {
        val pokemon = message.battlePokemon(0, battle) ?: return
        val effect = message.effectAt(1) ?: return
        val extraEffect = message.effectAt(2)?.typelessData ?: Text.literal("UNKNOWN")
        ShowdownInterpreter.broadcastOptionalAbility(battle, effect, pokemon)

        battle.dispatch{
            val pokemonName = pokemon.getName()
            val sourceName = message.battlePokemonFromOptional(battle)?.getName() ?: Text.literal("UNKNOWN")
            ShowdownInterpreter.lastCauser[battle.battleId] = message
            battle.minorBattleActions[pokemon.uuid] = message

            val lang = when (effect.id) {
                // Includes a 3rd argument being the magnitude level as a number
                "magnitude" -> battleLang("activate.magnitude", message.argumentAt(2)?.toIntOrNull() ?: 1)
                // Includes spited move and the PP it was reduced by
                "spite", "eeriespell" -> battleLang("activate.spite", pokemonName, extraEffect, message.argumentAt(3)!!)
                // Don't need additional lang, announced elsewhere
                "toxicdebris", "shedskin" -> return@dispatch GO
                // Add activation to each Pokemon's history
                "destinybond" -> {
                    battle.activePokemon.mapNotNull { it.battlePokemon?.uuid }.forEach { battle.minorBattleActions[it] = message }
                    battleLang("activate.destinybond", pokemonName)
                }
                "focussash", "focusband" -> battleLang("activate.focusband", pokemonName, effect.typelessData)
                "maxguard", "protect" -> battleLang("activate.protect", pokemonName)
                "shadowforce", "hyperspacefury", "hyperspacehole" -> battleLang("activate.phantomforce", pokemonName)
                else -> battleLang("activate.${effect.id}", pokemonName, sourceName, extraEffect)
            }
            battle.broadcastChatMessage(lang)
            battle.dispatchToFront {
                val status = Statuses.getStatus(effect.id)
                val actionEffect = status?.getActionEffect() ?: return@dispatchToFront GO
                val providers = mutableListOf<Any>(battle)
                pokemon.effectedPokemon.entity?.let { UsersProvider(it) }?.let(providers::add)
                val runtime = MoLangRuntime().also {
                    battle.addQueryFunctions(it.environment.getQueryStruct()).addStandardFunctions()
                }

                val context = ActionEffectContext(
                    actionEffect = actionEffect,
                    runtime = runtime,
                    providers = providers
                )
                this.future = actionEffect.run(context)
                holds = context.holds // Reference so future things can check on this action effect's holds
                future.thenApply { holds.clear() }
                return@dispatchToFront UntilDispatch { "effects" !in context.holds }
            }
            WaitDispatch(1F)
        }
    }
}