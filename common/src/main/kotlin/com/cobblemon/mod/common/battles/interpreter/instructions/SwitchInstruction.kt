package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.battles.model.actor.EntityBackedBattleActor
import com.cobblemon.mod.common.battles.ActiveBattlePokemon
import com.cobblemon.mod.common.battles.dispatch.*
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.net.messages.client.battle.BattleSwitchPokemonPacket
import com.cobblemon.mod.common.util.swap
import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import java.util.concurrent.CompletableFuture

/**
 * Format: |switch|POKEMON|DETAILS|HP STATUS or |drag|POKEMON|DETAILS|HP STATUS
 *
 * POKEMON has switched in (if there was an old Pokémon at that position, it is switched out).
 * POKEMON|DETAILS represents all the information that can be used to tell Pokémon apart.
 * The switched Pokémon has HP health points and STATUS status.
 * @author Deltric
 * @since January 22, 2022
 */
class SwitchInstruction(val instructionSet: InstructionSet, val battleActor: BattleActor, val publicMessage: BattleMessage, val privateMessage: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        val (pnx, pokemonID) = publicMessage.pnxAndUuid(0) ?: return
        if (!battle.started) {
            val (actor, activePokemon) = battle.getActorAndActiveSlotFromPNX(pnx)
            val pokemon = battle.getBattlePokemon(pnx, pokemonID)
            val entity = if (actor is EntityBackedBattleActor<*>) actor.entity else null

            activePokemon.battlePokemon = pokemon
            val pokemonEntity = pokemon.entity
            if (pokemonEntity == null && entity != null) {
                val targetPos = battleActor.getSide().getOppositeSide().actors.filterIsInstance<EntityBackedBattleActor<*>>().firstOrNull()?.entity?.pos?.let { pos ->
                    val offset = pos.subtract(entity.pos)
                    val idealPos = entity.pos.add(offset.multiply(0.33))
                    idealPos
                } ?: entity.pos

                actor.stillSendingOutCount++
                pokemon.effectedPokemon.sendOutWithAnimation(
                        source = entity,
                        battleId = battle.battleId,
                        level = entity.world as ServerWorld,
                        doCry = false,
                        position = targetPos
                ).thenApply {
                    actor.stillSendingOutCount--
                }
            }
        } else {
            battle.dispatchInsert {
                val (actor, activePokemon) = battle.getActorAndActiveSlotFromPNX(pnx)
                val pokemon = battle.getBattlePokemon(pnx, pokemonID)
                val entity = if (actor is EntityBackedBattleActor<*>) actor.entity else null
                pokemon.sendUpdate()

                if (activePokemon.battlePokemon == pokemon) {
                    return@dispatchInsert emptySet() // Already switched in, Showdown does this if the pokemon is going to die before it can switch
                }

                activePokemon.battlePokemon?.let { oldPokemon ->
                    if (publicMessage.effect()?.id == "batonpass") oldPokemon.contextManager.swap(pokemon.contextManager, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
                    oldPokemon.contextManager.clear(BattleContext.Type.VOLATILE, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
                    battle.majorBattleActions[oldPokemon.uuid] = publicMessage
                }
                battle.majorBattleActions[pokemon.uuid] = publicMessage

                setOf(
                        BattleDispatch {
                            if (entity != null) {
                                createEntitySwitch(battle, actor, entity, pnx, activePokemon, pokemon)
                            } else {
                                createNonEntitySwitch(battle, actor, pnx, activePokemon, pokemon)
                            }
                        }
                )
            }
        }
    }

    companion object{
        fun createEntitySwitch(battle: PokemonBattle, actor: BattleActor, entity: LivingEntity, pnx: String, activePokemon: ActiveBattlePokemon, newPokemon: BattlePokemon): DispatchResult {
            val pokemonEntity = activePokemon.battlePokemon?.entity
            // If we can't find the entity for some reason then we're going to skip the recall animation
            val sendOutFuture = CompletableFuture<Unit>()
            (pokemonEntity?.recallWithAnimation() ?: CompletableFuture.completedFuture(Unit)).thenApply {
                // Queue actual swap and send-in after the animation has ended
                actor.pokemonList.swap(actor.activePokemon.indexOf(activePokemon), actor.pokemonList.indexOf(newPokemon))
                activePokemon.battlePokemon = newPokemon
                battle.sendSidedUpdate(actor, BattleSwitchPokemonPacket(pnx, newPokemon, true), BattleSwitchPokemonPacket(pnx, newPokemon, false))
                if (newPokemon.entity != null) {
                    newPokemon.entity?.cry()
                    sendOutFuture.complete(Unit)
                } else {
                    val lastPosition = activePokemon.position
                    // Send out at previous Pokémon's location if it is known, otherwise actor location
                    val world = lastPosition?.first ?: entity.world as ServerWorld
                    val pos = lastPosition?.second ?: entity.pos
                    newPokemon.effectedPokemon.sendOutWithAnimation(
                            source = entity,
                            battleId = battle.battleId,
                            level = world,
                            position = pos
                    ).thenAccept { sendOutFuture.complete(Unit) }
                }
            }

            return UntilDispatch { sendOutFuture.isDone }
        }

        fun createNonEntitySwitch(battle: PokemonBattle, actor: BattleActor, pnx: String, activePokemon: ActiveBattlePokemon, newPokemon: BattlePokemon): DispatchResult {
            actor.pokemonList.swap(actor.activePokemon.indexOf(activePokemon), actor.pokemonList.indexOf(newPokemon))
            activePokemon.battlePokemon = newPokemon
            battle.sendSidedUpdate(actor, BattleSwitchPokemonPacket(pnx, newPokemon, true), BattleSwitchPokemonPacket(pnx, newPokemon, false))
            return WaitDispatch(1.5F)
        }

    }

}