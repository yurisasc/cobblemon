package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.PokemonCobbled.implementation
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

    var enabled = true
    var version = "1"
    var preventOverwrite = false
    var neededInstalledMods = listOf<String>()
    var neededUninstalledMods = listOf<String>()
    var spawns = mutableListOf<SpawnDetail>()

    fun isEnabled(): Boolean {
        return if (!enabled) {
            false
        } else if (neededInstalledMods.isNotEmpty() && neededInstalledMods.any { !implementation.isModInstalled(it) }) {
            false
        } else if (neededUninstalledMods.isNotEmpty() && neededUninstalledMods.any { implementation.isModInstalled(it) }) {
            false
        } else {
            true
        }
    }

    override fun iterator() = spawns.iterator()
}