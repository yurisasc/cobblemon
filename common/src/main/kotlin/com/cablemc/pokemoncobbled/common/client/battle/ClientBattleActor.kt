package com.cablemc.pokemoncobbled.common.client.battle

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.text.MutableText
import java.util.UUID

class ClientBattleActor(
    /** The showdown pIndexing, p0, p2, etc*/
    val showdownId: String,
    val displayName: MutableText,
    val uuid: UUID
) {
    lateinit var side: ClientBattleSide

    var pokemon = listOf<Pokemon>()
    val activePokemon = mutableListOf<ActiveClientBattlePokemon>()
}