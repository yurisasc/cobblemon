package com.cablemc.pokemoncobbled.common.battles

class InBattleMove(
    val id: String,
    val move: String,
    val pp: Int,
    val maxpp: Int,
    val target: MoveTarget,
    val disabled: Boolean
) {
    fun getTargets(user: ActiveBattlePokemon) = target.targetList(user)
    fun canBeUsed() = pp > 0 && !disabled
}