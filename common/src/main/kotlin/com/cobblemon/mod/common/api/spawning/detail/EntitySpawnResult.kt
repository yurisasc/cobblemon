package com.cobblemon.mod.common.api.spawning.detail

import net.minecraft.entity.Entity

/**
 * A spawn result for spawn actions which involves some number of entities. This is used to maintain
 * spawned entity lists for Spawner implementations that cap the number of entities that may be spawned.
 *
 * @author Hiroku
 * @since January 13th, 2024
 */
class EntitySpawnResult(val entities: List<Entity>)