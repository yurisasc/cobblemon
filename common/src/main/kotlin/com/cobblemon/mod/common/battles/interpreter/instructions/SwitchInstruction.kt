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
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.battles.model.actor.EntityBackedBattleActor
import com.cobblemon.mod.common.api.scheduling.afterOnServer
import com.cobblemon.mod.common.battles.ActiveBattlePokemon
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor
import com.cobblemon.mod.common.battles.dispatch.*
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.entity.pokemon.effects.IllusionEffect
import com.cobblemon.mod.common.net.messages.client.battle.BattleSwitchPokemonPacket
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.util.swap
import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import java.util.concurrent.CompletableFuture

/**
 * Format: |switch|POKEMON|DETAILS|HP STATUS
 *
 * POKEMON has switched in (if there was an old Pokémon at that position, it is switched out).
 * POKEMON|DETAILS represents all the information that can be used to tell Pokémon apart.
 * The switched Pokémon has HP health points and STATUS status.
 * @author Deltric
 * @since January 22nd, 2022
 */
class SwitchInstruction(val instructionSet: InstructionSet, val battleActor: BattleActor, val publicMessage: BattleMessage, val privateMessage: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {

        val (pnx, _) = publicMessage.pnxAndUuid(0) ?: return
        val (actor, activePokemon) = battle.getActorAndActiveSlotFromPNX(pnx)
        val entity = if (actor is EntityBackedBattleActor<*>) actor.entity else null

        val imposter = instructionSet.getNextInstruction<TransformInstruction>(this)?.expectedTarget != null
        val illusion = publicMessage.battlePokemonFromOptional(battle, "is")
        val pokemon = publicMessage.battlePokemon(0, battle) ?: return

        if (!battle.started) {  // battle 'starts' at beginning of dispatches; see InitializeInstruction

            val pokemonEntity = pokemon.entity

            // pokemon entities starting on the field should already have battlePokemon init; see InitializeInstruction
            if (pokemonEntity != null && actor !is PokemonBattleActor) {
                illusion?.let { IllusionEffect(it.effectedPokemon).start(pokemonEntity) }   // initialize.docries is happening before the effect takes place
                battle.dispatchWaiting { broadcastSwitch(battle, actor, pokemon, illusion) }
                return
            }
            else if (pokemonEntity == null && entity != null) {
                activePokemon.battlePokemon = pokemon
                activePokemon.illusion = illusion

                val targetPos = battleActor.getSide().getOppositeSide().actors.filterIsInstance<EntityBackedBattleActor<*>>().firstOrNull()?.entity?.pos?.let { pos ->
                    val offset = pos.subtract(entity.pos)
                    val idealPos = entity.pos.add(offset.multiply(0.33))
                    idealPos
                } ?: entity.pos

                actor.stillSendingOutCount++
                battle.sendSidedUpdate(actor, BattleSwitchPokemonPacket(pnx, pokemon, true, illusion), BattleSwitchPokemonPacket(pnx, pokemon, false, illusion))
                broadcastSwitch(battle, actor, pokemon, illusion)
                pokemon.effectedPokemon.sendOutWithAnimation(
                    source = entity,
                    battleId = battle.battleId,
                    level = entity.world as ServerWorld,
                    doCry = false,
                    position = targetPos,
                    illusion = illusion?.let { IllusionEffect(it.effectedPokemon) }
                ).thenApply {
                    actor.stillSendingOutCount--
                }
            }
        }
        else {
            battle.dispatchInsert {
                pokemon.sendUpdate()

                if (activePokemon.battlePokemon == pokemon) {
                    return@dispatchInsert emptySet() // Already switched in, Showdown does this if the pokemon is going to die before it can switch
                }

                activePokemon.battlePokemon?.let { oldPokemon ->
                    if (publicMessage.effect()?.id == "batonpass") oldPokemon.contextManager.swap(pokemon.contextManager, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
                    oldPokemon.contextManager.clear(BattleContext.Type.VOLATILE, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
                    battle.majorBattleActions[oldPokemon.uuid] = publicMessage

                    val publicName = (activePokemon.illusion ?: oldPokemon).effectedPokemon.getDisplayName()
                    actor.sendMessage(battleLang("withdraw.self", publicName))
                    battle.actors.filter { it != actor }.forEach { it.sendMessage(battleLang("withdraw.other", actor.getName(), publicName)) }
                }
                battle.majorBattleActions[pokemon.uuid] = publicMessage

                setOf(
                    BattleDispatch {
                        if (entity != null) {
                            createEntitySwitch(battle, actor, entity, pnx, activePokemon, pokemon, illusion, imposter)
                        } else {
                            createNonEntitySwitch(battle, actor, pnx, activePokemon, pokemon, illusion)
                        }
                    }
                )
            }
        }
    }

    companion object{
        fun createEntitySwitch(
            battle: PokemonBattle,
            actor: BattleActor,
            entity: LivingEntity,
            pnx: String,
            activePokemon: ActiveBattlePokemon,
            newPokemon: BattlePokemon,
            illusion: BattlePokemon? = null,
            imposter: Boolean = false
        ): DispatchResult {
            val pokemonEntity = activePokemon.battlePokemon?.entity
            // If we can't find the entity for some reason then we're going to skip the recall animation
            val sendOutFuture = CompletableFuture<Unit>()
            // skip if handled by respective EntityEffect
            val doCry = illusion == null && !imposter
            (pokemonEntity?.recallWithAnimation() ?: CompletableFuture.completedFuture(Unit)).thenApply {
                // Queue actual swap and send-in after the animation has ended
                actor.pokemonList.swap(actor.activePokemon.indexOf(activePokemon), actor.pokemonList.indexOf(newPokemon))
                activePokemon.battlePokemon = newPokemon
                activePokemon.illusion = illusion
                battle.sendSidedUpdate(actor, BattleSwitchPokemonPacket(pnx, newPokemon, true, illusion), BattleSwitchPokemonPacket(pnx, newPokemon, false, illusion))
                if (newPokemon.entity != null) {
                    illusion?.let { IllusionEffect(it.effectedPokemon).start(newPokemon.entity!!) }
                    if (doCry) newPokemon.entity?.cry()
                    afterOnServer(seconds = 2.0F) { sendOutFuture.complete(Unit) } // try this
                } else {
                    val lastPosition = activePokemon.position
                    // Send out at previous Pokémon's location if it is known, otherwise actor location
                    val world = lastPosition?.first ?: entity.world as ServerWorld
                    val pos = lastPosition?.second ?: entity.pos
                    newPokemon.effectedPokemon.sendOutWithAnimation(
                        source = entity,
                        battleId = battle.battleId,
                        level = world,
                        position = pos,
                        doCry = doCry,
                        illusion = illusion?.let { IllusionEffect(it.effectedPokemon) }
                    ).thenAccept { sendOutFuture.complete(Unit) }
                }

                broadcastSwitch(battle, actor, newPokemon, illusion)
            }

            return UntilDispatch { sendOutFuture.isDone }
        }

        fun createNonEntitySwitch(battle: PokemonBattle, actor: BattleActor, pnx: String, activePokemon: ActiveBattlePokemon, newPokemon: BattlePokemon, illusion: BattlePokemon? = null): DispatchResult {
            actor.pokemonList.swap(actor.activePokemon.indexOf(activePokemon), actor.pokemonList.indexOf(newPokemon))
            activePokemon.battlePokemon = newPokemon
            activePokemon.illusion = illusion
            battle.sendSidedUpdate(actor, BattleSwitchPokemonPacket(pnx, newPokemon, true, illusion), BattleSwitchPokemonPacket(pnx, newPokemon, false, illusion))
            broadcastSwitch(battle, actor, newPokemon, illusion)
            return WaitDispatch(1.5F)
        }

        private fun broadcastSwitch(battle: PokemonBattle, actor: BattleActor, newPokemon: BattlePokemon, illusion: BattlePokemon?) {
            val publicPokemon = (illusion ?: newPokemon).effectedPokemon
            val publicLang = publicPokemon.nickname?.let { nickname ->
                battleLang("switch.other.nickname", actor.getName(), nickname, publicPokemon.species.translatedName)
            } ?: battleLang("switch.other", actor.getName(), publicPokemon.getDisplayName())
            actor.sendMessage(battleLang("switch.self", publicPokemon.getDisplayName()))
            battle.actors.filter { it != actor }.forEach { it.sendMessage(publicLang) }
        }
    }

}