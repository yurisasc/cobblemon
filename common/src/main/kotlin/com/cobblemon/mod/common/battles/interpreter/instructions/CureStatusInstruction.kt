package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.interpreter.Effect
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.battles.ShowdownInterpreter
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction
import com.cobblemon.mod.common.net.messages.client.battle.BattlePersistentStatusPacket
import com.cobblemon.mod.common.util.asTranslated
import com.cobblemon.mod.common.util.battleLang

/**
 * Format: |-curestatus|POKEMON|STATUS
 *
 * The POKEMON has recovered from its STATUS.
 * @author Hiroku
 * @since November 5, 2022
 */
class CureStatusInstruction(val message: BattleMessage): InterpreterInstruction {

    override fun invoke(battle: PokemonBattle) {
        val maybeActivePokemon = message.actorAndActivePokemon(0, battle)?.second?.battlePokemon
        val maybePartyPokemon = message.getBattlePokemon(0, battle)
        val pokemon = maybeActivePokemon ?: maybePartyPokemon ?: return
        val pokemonName = pokemon.getName()
        val status = message.argumentAt(1)?.let(Statuses::getStatus) ?: return
        val effect = message.effect()
        ShowdownInterpreter.broadcastOptionalAbility(battle, effect, pokemonName)

        battle.dispatchWaiting {
            pokemon.effectedPokemon.status = null
            pokemon.sendUpdate()

            if (maybeActivePokemon != null) {
                message.pnxAndUuid(0)?.let {
                    battle.sendUpdate(BattlePersistentStatusPacket(it.first, null))
                }
            }
            val lang = when (effect?.type) {
                Effect.Type.ABILITY -> battleLang("curestatus.${effect.id}", pokemonName)
                else -> status.removeMessage.asTranslated(pokemonName)
            }
            battle.broadcastChatMessage(lang)
            pokemon.contextManager.remove(status.showdownName, BattleContext.Type.STATUS)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }
}