package com.cablemc.pokemoncobbled.common.api.moves


/**
 * Registry for all known Moves.
 */
object Moves {
    private lateinit var allMoves: HashMap<String, MoveTemplate>

    fun load() {
        allMoves = MoveLoader.loadFromFiles()
    }

    fun getByName(name: String): MoveTemplate? {
        return allMoves.get(name)
    }

    fun count() = allMoves.size
}