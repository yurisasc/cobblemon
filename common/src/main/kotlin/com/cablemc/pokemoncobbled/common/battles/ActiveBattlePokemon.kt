package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import kotlin.math.abs

class ActiveBattlePokemon(val actor: BattleActor, var battlePokemon: BattlePokemon? = null) {
    val battle = actor.battle
    var selectableMoves: List<InBattleMove> = emptyList()


    fun getOppositeSide() = actor.getSide().getOppositeSide()
    fun getSide() = actor.getSide()

    fun getAdjacent(): List<ActiveBattlePokemon> {
        val digit = getDigit()
        val sideSize = battle.format.battleType.slotsPerActor * battle.format.battleType.actorsPerSide
        return battle.activePokemon.filter {
            val sameSideDigit = if (it.getSide() == getSide()) {
                it.getDigit()
            } else {
                sideSize - it.getDigit() + 1
            }
            val digitDistance = abs(sameSideDigit - digit)
            return@filter digitDistance <= 1 && it != this
        }
    }

    fun getAdjacentAllies() = getAdjacent().filter { it.isAllied(this) }
    fun getAdjacentOpponents() = getAdjacent().filterNot { it.isAllied(this) }
    fun isAllied(other: ActiveBattlePokemon) = getSide() == other.getSide()

    fun getSignedDigitRelativeTo(other: ActiveBattlePokemon): String {
        val digit = getDigitRelativeTo(other)
        return if (isAllied(other)) {
            "-$digit"
        } else {
            "+$digit"
        }
    }
    fun getDigitRelativeTo(other: ActiveBattlePokemon) = getDigit(asAlly = isAllied(other))
    fun getDigit(asAlly: Boolean = true): Int {
        var digit = 1
        for (activePokemon in getSide().activePokemon) {
            if (activePokemon == this) {
                return digit
            } else {
                digit++
            }
        }
        return digit * if (asAlly) 1 else -1
    }

    fun getLetter(): Char {
        var index = 0
        for (activePokemon in getSide().activePokemon) {
            if (activePokemon == this) {
                break
            } else {
                index++
            }
        }

        return when(index) {
            0 -> 'a'
            1 -> 'b'
            2 -> 'c'
            3 -> 'd'
            4 -> 'e'
            5 -> 'f'
            else -> throw IllegalStateException("Battle has more than 6 in the active slot, makes no sense.")
        }
    }
}