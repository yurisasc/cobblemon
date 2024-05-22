package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.google.common.collect.ImmutableMap
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.EntityLookTarget
import net.minecraft.entity.ai.brain.MemoryModuleState
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.WalkTarget
import net.minecraft.entity.ai.brain.task.MultiTickTask
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import java.util.Optional

class HuntPlayerTask : MultiTickTask<LivingEntity>(
        ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT,
                MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT,
                MemoryModuleType.DISTURBANCE_LOCATION, MemoryModuleState.VALUE_PRESENT
        )
) {

    companion object {
        private const val MAX_SEARCH_DURATION = 1000
        private const val SEARCH_RADIUS = 10
        private const val ATTACK_RANGE = 2.0
    }

    private var targetEntity: Optional<LivingEntity> = Optional.empty()
    private var lastKnownLocation: Optional<BlockPos> = Optional.empty()
    private var searching = false
    private var searchTime = 0

    override fun shouldRun(world: ServerWorld, entity: LivingEntity): Boolean {
        this.targetEntity = findPlayer(world, entity)
        return targetEntity.isPresent
    }

    override fun shouldKeepRunning(world: ServerWorld, entity: LivingEntity, time: Long): Boolean {
        return targetEntity.isPresent || (searching && searchTime < MAX_SEARCH_DURATION)
    }

    private fun findPlayer(world: ServerWorld, entity: LivingEntity): Optional<LivingEntity> {
        return world.players
                .filter { it.isAlive && entity.squaredDistanceTo(it) <= SEARCH_RADIUS * SEARCH_RADIUS }
                .minByOrNull { entity.squaredDistanceTo(it) }
                ?.let { Optional.of(it) } ?: Optional.empty()
    }

    override fun run(world: ServerWorld, entity: LivingEntity, time: Long) {
        addLookWalkTargets(entity)
        searching = false
        searchTime = 0

        super.run(world, entity, time)

        val disturbanceLocation = entity.brain.getOptionalMemory(MemoryModuleType.DISTURBANCE_LOCATION)
        if (disturbanceLocation != null) {
            disturbanceLocation.ifPresent {
                onEntityHeard(entity, it)
            }
        }
    }

    private fun addLookWalkTargets(entity: LivingEntity) {
        targetEntity.ifPresent { target ->
            val lookTarget = EntityLookTarget(target, true)
            entity.brain.remember(MemoryModuleType.LOOK_TARGET, lookTarget)
            entity.brain.remember(MemoryModuleType.WALK_TARGET, WalkTarget(lookTarget, 1.0f, 1))
        }
    }

    override fun finishRunning(world: ServerWorld, entity: LivingEntity, time: Long) {
        targetEntity = Optional.empty()
        searching = false
    }

    override fun keepRunning(world: ServerWorld, entity: LivingEntity, time: Long) {
        if (searching) {
            searchTime++
            if (searchTime >= MAX_SEARCH_DURATION) {
                finishRunning(world, entity, time)
                return
            }
        }

        if (targetEntity.isPresent) {
            val target = targetEntity.get()
            if (target.isAlive && entity.canSee(target)) {
                if (entity.squaredDistanceTo(target) <= ATTACK_RANGE * ATTACK_RANGE) {
                    entity.tryAttack(target as Entity)
                } else {
                    addLookWalkTargets(entity)
                }
                lastKnownLocation = Optional.of(target.blockPos)
                searchTime = 0  // Reset search time when target is visible
            } else {
                startSearching(entity)
            }
        } else if (searching) {
            performSearch(entity)
        } else {
            targetEntity = findPlayer(world, entity)
        }
    }

    private fun startSearching(entity: LivingEntity) {
        if (lastKnownLocation.isPresent) {
            searching = true
            searchTime = 0
            entity.brain.remember(MemoryModuleType.WALK_TARGET, WalkTarget(lastKnownLocation.get(), 1.0f, 1))
        }
    }

    private fun performSearch(entity: LivingEntity) {
        if (!lastKnownLocation.isPresent) {
            searching = false
            return
        }

        searchTime++
        if (searchTime % 20 == 0) {
            // Move to a new random position within a radius around the last known location
            val randomPos = lastKnownLocation.get().add(
                    entity.random.nextInt(SEARCH_RADIUS * 2) - SEARCH_RADIUS,
                    0,
                    entity.random.nextInt(SEARCH_RADIUS * 2) - SEARCH_RADIUS
            )
            entity.brain.remember(MemoryModuleType.WALK_TARGET, WalkTarget(randomPos, 1.0f, 1))
        }
        if (searchTime >= MAX_SEARCH_DURATION) {
            searching = false
            targetEntity = findPlayer(entity.world as ServerWorld, entity)
        }
    }

    fun onEntityHeard(entity: LivingEntity, pos: BlockPos) {
        if (!targetEntity.isPresent) {
            searching = true
            //searchTime = 0
            lastKnownLocation = Optional.of(pos)
            entity.brain.remember(MemoryModuleType.WALK_TARGET, WalkTarget(pos, 1.0f, 1))
        }
    }
}
