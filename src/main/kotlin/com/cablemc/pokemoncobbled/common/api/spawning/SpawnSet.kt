package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import net.minecraftforge.fml.ModList

/**
 * A simple collection of spawns to make it more straightforward to read from
 * a file.
 *
 * @author Hiroku
 * @since January 27th, 2022
 */
class SpawnSet : Iterable<SpawnDetail> {
    var id = ""

    var version = "1"
    var preventOverwrite = false
    var neededInstalledMods = listOf<String>()
    var neededUninstalledMods = listOf<String>()
    var spawns = mutableListOf<SpawnDetail>()

    fun isValid(): Boolean {
        return if (neededInstalledMods.isNotEmpty() && neededInstalledMods.any { !ModList.get().isLoaded(it) }) {
            false
        } else if (neededUninstalledMods.isNotEmpty() && neededUninstalledMods.any { ModList.get().isLoaded(it) }) {
            false
        } else {
            true
        }
    }

    override fun iterator() = spawns.iterator()
}