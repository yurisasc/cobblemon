package com.cablemc.pokemoncobbled.common.api.spawning.prospecting

import com.cablemc.pokemoncobbled.common.api.spawning.WorldSlice
import com.cablemc.pokemoncobbled.common.api.spawning.context.AreaSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.SpawningArea

/**
 * Interface responsible for slicing out an async-save [WorldSlice] that can be used for generating
 * [SpawningContext]s, specifically [AreaSpawningContext]s.
 *
 * @author Hiroku
 * @since January 29th, 2022
 */
interface SpawningProspector {
    fun prospect(spawner: Spawner, area: SpawningArea): WorldSlice
}