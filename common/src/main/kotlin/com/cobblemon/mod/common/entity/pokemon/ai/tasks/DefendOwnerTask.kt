package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.google.common.collect.ImmutableMap
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.brain.MemoryModuleState
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.WalkTarget
import net.minecraft.entity.ai.brain.task.MultiTickTask
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.server.world.ServerWorld

class DefendOwnerTask : MultiTickTask<PokemonEntity>(
        ImmutableMap.of(
                CobblemonMemories.NEAREST_VISIBLE_ATTACKER, MemoryModuleState.VALUE_PRESENT
        )
) {

    private var target: LivingEntity? = null
    private val targetPredicate = TargetPredicate.createAttackable()

    override fun shouldRun(world: ServerWorld, entity: PokemonEntity): Boolean {
        val nearestAttacker = entity.brain.getOptionalMemory(CobblemonMemories.NEAREST_VISIBLE_ATTACKER)
        if (nearestAttacker != null) {
            return nearestAttacker.isPresent
        }
        return false
    }

    override fun run(world: ServerWorld, entity: PokemonEntity, time: Long) {
        target = entity.brain.getOptionalMemory(CobblemonMemories.NEAREST_VISIBLE_ATTACKER)?.get()
        entity.target = target
    }

    override fun shouldKeepRunning(world: ServerWorld, entity: PokemonEntity, time: Long): Boolean {
        return target != null && target!!.isAlive && entity.isTarget(target!!, targetPredicate)
    }

    override fun keepRunning(world: ServerWorld, entity: PokemonEntity, time: Long) {
        target?.let {
            entity.target = it
            val followRange = entity.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE)?.value

            if (followRange != null && entity.distanceTo(it) <= followRange) {
                if (entity.distanceTo(it) > 2.0) {
                    entity.brain.remember(
                            MemoryModuleType.WALK_TARGET,
                            WalkTarget(it, 1.0f, 1)
                    )
                } else {
                    entity.tryAttack(it)
                }
            }
        }
    }

    override fun finishRunning(world: ServerWorld, entity: PokemonEntity, time: Long) {
        target = null
        entity.target = null
        entity.brain.forget(MemoryModuleType.WALK_TARGET)
    }
}
