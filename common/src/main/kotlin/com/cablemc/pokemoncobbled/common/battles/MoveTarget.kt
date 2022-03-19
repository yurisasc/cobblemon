package com.cablemc.pokemoncobbled.common.battles

enum class MoveTarget(val targetList: (ActiveBattlePokemon) -> List<ActiveBattlePokemon>? = { null }) {
    any({ pokemon -> pokemon.battle.activePokemon.filter { it != pokemon } }),
    all,
    allAdjacent,
    allAdjacentFoes,
    self,
    normal({ pokemon -> pokemon.getAdjacent() }),
    randomNormal,
    allies,
    allySide,
    allyTeam,
    adjacentAlly({ pokemon -> pokemon.getAdjacentAllies() }),
    adjacentAllyOrSelf({ pokemon -> pokemon.getAdjacentAllies() + pokemon }),
    adjacentFoe({ pokemon -> pokemon.getAdjacentOpponents() }),
    foeSide,
    scripted
}