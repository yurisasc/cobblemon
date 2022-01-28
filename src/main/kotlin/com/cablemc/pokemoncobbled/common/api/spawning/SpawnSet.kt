package com.cablemc.pokemoncobbled.common.api.spawning

class SpawnSet : Iterable<SpawnDetail> {
    var id = ""

    var spawns = mutableListOf<SpawnDetail>()

    override fun iterator() = spawns.iterator()
}