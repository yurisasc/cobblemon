package com.cablemc.pokemoncobbled.common.client.battle

class ClientBattleSide {
    lateinit var battle: ClientBattle
    val actors = mutableListOf<ClientBattleActor>()
    val activeClientBattlePokemon: Iterable<ActiveClientBattlePokemon>
        get() = actors.flatMap { it.activePokemon }
}