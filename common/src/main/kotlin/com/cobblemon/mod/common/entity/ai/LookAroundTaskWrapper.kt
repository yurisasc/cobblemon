package com.cobblemon.mod.common.entity.ai

import com.cobblemon.mod.common.CobblemonMemories
import net.minecraft.entity.ai.brain.task.LookAroundTask
import net.minecraft.entity.mob.MobEntity
import net.minecraft.server.world.ServerWorld


class LookAroundTaskWrapper(minRunTime: Int, maxRunTime: Int) : LookAroundTask(minRunTime, maxRunTime) {
    override fun shouldRun(world: ServerWorld?, entity: MobEntity?): Boolean {

        return (entity?.brain?.hasMemoryModule(CobblemonMemories.POKEMON_SLEEPING) == false
                || entity?.brain?.hasMemoryModuleWithValue(CobblemonMemories.POKEMON_SLEEPING, false) == true)
                && super.shouldRun(world, entity)
    }
}