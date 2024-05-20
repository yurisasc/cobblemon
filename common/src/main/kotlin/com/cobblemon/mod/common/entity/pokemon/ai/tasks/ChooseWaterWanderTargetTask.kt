package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import net.minecraft.entity.ai.brain.BlockPosLookTarget
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.WalkTarget
import net.minecraft.entity.ai.brain.task.LookTargetUtil
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer
import net.minecraft.entity.mob.PathAwareEntity

object ChooseWaterWanderTargetTask {
    fun create(chance: Int, horizontalRange: Int, verticalRange: Int, swimSpeed: Float, completionRange: Int): SingleTickTask<PathAwareEntity> {
        return TaskTriggerer.task {
            it.group(
                it.queryMemoryAbsent(MemoryModuleType.WALK_TARGET),
                it.queryMemoryOptional(MemoryModuleType.LOOK_TARGET)
            ).apply(it) { walkTarget, lookTarget ->
                TaskRunnable { world, entity, time ->
                    if (world.random.nextInt(chance) != 0) return@TaskRunnable false
                    val targetVec = LookTargetUtil.find(entity, horizontalRange, verticalRange) ?: return@TaskRunnable false
                    walkTarget.remember(WalkTarget(targetVec, swimSpeed, completionRange))
                    lookTarget.remember(BlockPosLookTarget(targetVec.add(0.0, 1.5, 0.0)))
                    return@TaskRunnable true
                }
            }
        }
    }
}