package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.context.RegisteredSpawningContext

/**
 * A dummy type that allows a JSON serializer to work on what it rules as complex keys.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
class ContextPropertyMap : HashMap<RegisteredSpawningContext<*>, ContextProperties>()