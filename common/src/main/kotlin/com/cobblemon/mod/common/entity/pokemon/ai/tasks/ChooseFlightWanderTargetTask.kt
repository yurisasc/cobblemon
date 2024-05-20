package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.cobblemon.mod.common.entity.pokemon.PokemonBehaviourFlag
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.ai.AboveGroundTargeting
import net.minecraft.entity.ai.NoPenaltySolidTargeting
import net.minecraft.entity.ai.brain.BlockPosLookTarget
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.WalkTarget
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer
import net.minecraft.entity.mob.PathAwareEntity

object ChooseFlightWanderTargetTask {
    fun create(chance: Int, horizontalRange: Int, verticalRange: Int, flySpeed: Float, completionRange: Int): SingleTickTask<PathAwareEntity> {
        return TaskTriggerer.task {
            it.group(
                it.queryMemoryAbsent(MemoryModuleType.WALK_TARGET),
                it.queryMemoryOptional(MemoryModuleType.LOOK_TARGET)
            ).apply(it) { walkTarget, lookTarget ->
                TaskRunnable { world, entity, time ->
                    if (world.random.nextInt(chance) != 0) return@TaskRunnable false
                    val rotVec = entity.getRotationVec(0.0f)
                    val targetVec = AboveGroundTargeting.find(entity, horizontalRange, verticalRange, rotVec.x, rotVec.z, 1.5707964f, 3, 1) ?: return@TaskRunnable false
                    walkTarget.remember(WalkTarget(targetVec, flySpeed, completionRange))
                    lookTarget.remember(BlockPosLookTarget(targetVec.add(0.0, 1.5, 0.0)))
                    return@TaskRunnable true
                }
            }
        }
    }
}