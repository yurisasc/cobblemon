/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.EntityBackedBattleActor
import com.cobblemon.mod.common.api.entity.PokemonSender
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent
import com.cobblemon.mod.common.api.scheduling.delayedFuture
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.battles.ShowdownInterpreter
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.net.messages.client.battle.BattleFaintPacket
import com.cobblemon.mod.common.util.battleLang
import java.util.concurrent.CompletableFuture

class FaintInstruction(battle: PokemonBattle, val message: BattleMessage) : InterpreterInstruction {
    var waitTime = 2.5F
    val faintingPokemon = message.getBattlePokemon(0, battle)!!

    override fun invoke(battle: PokemonBattle) {
        val faintMessage = battleLang("fainted", faintingPokemon.getName()).red()

        battle.dispatchFuture {
            val (pnx, _) = message.pnxAndUuid(0) ?: return@dispatchFuture CompletableFuture.completedFuture(Unit)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchFuture CompletableFuture.completedFuture(Unit)
            battle.sendUpdate(BattleFaintPacket(pnx))
            val actor = pokemon.actor
            pokemon.effectedPokemon.currentHealth = 0
            val preamble = if (actor is EntityBackedBattleActor<*>) {
                (actor.entity as? PokemonSender)?.let { sender -> pokemon.entity?.recallWithAnimation()}
            } else {
                null
            } ?: delayedFuture(seconds = 2.5F)

            preamble.thenAccept {
                pokemon.sendUpdate()
                battle.broadcastChatMessage(faintMessage)
                val context = ShowdownInterpreter.getContextFromFaint(pokemon, battle)
                CobblemonEvents.BATTLE_FAINTED.post(BattleFaintedEvent(battle, pokemon, context))

                battle.getActorAndActiveSlotFromPNX(pnx).second.battlePokemon = null
                pokemon.contextManager.add(context)
                pokemon.contextManager.clear(BattleContext.Type.STATUS, BattleContext.Type.VOLATILE, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
                battle.majorBattleActions[pokemon.uuid] = message
            }
        }
    }
}