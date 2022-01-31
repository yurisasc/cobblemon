package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail

/**
 * A simple collection of spawns to make it more straightforward to read from
 * a file.
 *
 * @author Hiroku
 * @since January 27th, 2022
 */
class SpawnSet : Iterable<SpawnDetail> {
    var id = ""

    var spawns = mutableListOf<SpawnDetail>()

    override fun iterator() = spawns.iterator()
}