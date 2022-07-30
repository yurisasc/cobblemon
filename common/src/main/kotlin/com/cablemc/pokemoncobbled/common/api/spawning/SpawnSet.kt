package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.PokemonCobbled.implementation
import com.cablemc.pokemoncobbled.common.api.ModDependant
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import java.nio.file.Path

/**
 * A simple collection of spawns to make it more straightforward to read from
 * a file.
 *
 * @author Hiroku
 * @since January 27th, 2022
 */
class SpawnSet : Iterable<SpawnDetail>, ModDependant {
    var id = ""

    var enabled = true
    var version = "1"
    var preventOverwrite = false
    override var neededInstalledMods = listOf<String>()
    override var neededUninstalledMods = listOf<String>()
    var spawns = mutableListOf<SpawnDetail>()
    lateinit var path: Path

    fun isEnabled(): Boolean = enabled && isModDependencySatisfied()

    override fun iterator() = spawns.iterator()
}