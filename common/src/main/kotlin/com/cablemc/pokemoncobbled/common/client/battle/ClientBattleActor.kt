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

    val pokemon = mutableListOf<Pokemon>()
    val activePokemon = mutableListOf<ActiveClientBattlePokemon>()
}