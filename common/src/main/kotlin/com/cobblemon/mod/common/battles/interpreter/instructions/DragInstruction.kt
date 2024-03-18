package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.battles.model.actor.EntityBackedBattleActor
import com.cobblemon.mod.common.battles.dispatch.BattleDispatch
import com.cobblemon.mod.common.battles.dispatch.InstructionSet
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.util.battleLang

/**
 * Format: |drag|POKEMON|DETAILS|HP STATUS
 *
 * POKEMON has switched in (if there was an old Pokémon in that position, it is switched out).
 * POKEMON|DETAILS represents all the information that can be used to tell Pokémon apart.
 * The switched Pokémon has HP health points and STATUS status.
 * @author
 */
class DragInstruction(val instructionSet: InstructionSet, val battleActor: BattleActor, val publicMessage: BattleMessage, val privateMessage: BattleMessage): InterpreterInstruction {
    override fun invoke(battle: PokemonBattle) {
        battle.dispatchInsert {
            val (pnx, pokemonID) = publicMessage.pnxAndUuid(0)!!
            val (_, activePokemon) = battle.getActorAndActiveSlotFromPNX(pnx)

            val optionalID = publicMessage.optionalArgument("is")
            val pokemon = battle.getBattlePokemon(pnx, optionalID ?: pokemonID)
            val illusion = optionalID?.let { battle.getBattlePokemon(pnx, pokemonID) }

            battle.broadcastChatMessage(battleLang("dragged_out", pokemon.getName()))
            activePokemon.battlePokemon?.let { oldPokemon ->
                oldPokemon.contextManager.clear(BattleContext.Type.VOLATILE, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
                battle.majorBattleActions[oldPokemon.uuid] = publicMessage
            }
            battle.majorBattleActions[pokemon.uuid] = publicMessage

            val entity = if (battleActor is EntityBackedBattleActor<*>) battleActor.entity else null
            setOf(
                    BattleDispatch {
                        if (entity != null) {
                            SwitchInstruction.createEntitySwitch(battle, battleActor, entity, pnx, activePokemon, pokemon, illusion)
                        } else {
                            SwitchInstruction.createNonEntitySwitch(battle, battleActor, pnx, activePokemon, pokemon, illusion)
                        }
                    }
            )
        }
    }

}