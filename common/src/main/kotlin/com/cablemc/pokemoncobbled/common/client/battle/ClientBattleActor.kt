package com.cablemc.pokemoncobbled.common.client.battle

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.ActorType
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.text.MutableText
import java.util.UUID

class ClientBattleActor(
    /** The showdown pIndexing, p0, p2, etc*/
    val showdownId: String,
    val displayName: MutableText,
    val uuid: UUID,
    val type: ActorType
) {
    lateinit var side: ClientBattleSide

    var pokemon = mutableListOf<Pokemon>()
    val activePokemon = mutableListOf<ActiveClientBattlePokemon>()
}