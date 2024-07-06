package com.cobblemon.mod.common.entity.ai

import com.cobblemon.mod.common.CobblemonMemories
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.task.LookAtMobTask
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskTriggerer

object LookAtMobTaskWrapper {
    fun create(range:Float): SingleTickTask<LivingEntity> {
        return TaskTriggerer.task {
            it.group(
                    it.queryMemoryAbsent(CobblemonMemories.POKEMON_SLEEPING),
            ).apply(it) { isSleeping ->
                return@apply LookAtMobTask.create(range)
            }
        }
    }
}