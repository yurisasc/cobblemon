package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.google.common.collect.ImmutableMap
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.EntityLookTarget
import net.minecraft.entity.ai.brain.MemoryModuleState
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.WalkTarget
import net.minecraft.entity.ai.brain.task.MultiTickTask
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
        private const val SEARCH_RADIUS = 20
        private const val ATTACK_RANGE = 2.0
        private const val WALK_SPEED = 0.5f // Adjust this value to change the speed
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
        val canSeePlayer = targetEntity.isPresent && entity.canSee(targetEntity.get())
        // if entity cannot see player then searching is true
        if (!canSeePlayer)
            searching = true

        val isStillSearching = searching && searchTime < MAX_SEARCH_DURATION

        //return canSeePlayer && isStillSearching   // commented this out for testing
        if (canSeePlayer || isStillSearching)
            return true
        else
            return false
    }

    private fun findPlayer(world: ServerWorld, entity: LivingEntity): Optional<LivingEntity> {
        return world.players
                // filter all players that are alive that the entity can see and are within a certain radius of the entity
                .filter { it.isAlive && entity.canSee(it) && entity.squaredDistanceTo(it) <= SEARCH_RADIUS * SEARCH_RADIUS }
                // find the player with the minimum distance to the entity
                .minByOrNull { entity.squaredDistanceTo(it) }
                ?.let { Optional.of(it) } ?: Optional.empty()
    }

    override fun run(world: ServerWorld, entity: LivingEntity, time: Long) {
        addLookWalkTargets(entity)
        searching = false
        searchTime = 0

        super.run(world, entity, time)
    }

    private fun addLookWalkTargets(entity: LivingEntity) {
        targetEntity.ifPresent { target ->
            val lookTarget = EntityLookTarget(target, true)
            entity.brain.remember(MemoryModuleType.LOOK_TARGET, lookTarget)
            entity.brain.remember(MemoryModuleType.WALK_TARGET, WalkTarget(lookTarget, WALK_SPEED, 1))
        }
    }

    override fun finishRunning(world: ServerWorld, entity: LivingEntity, time: Long) {
        targetEntity = Optional.empty()
        searching = false
    }

    override fun keepRunning(world: ServerWorld, entity: LivingEntity, time: Long) {
        // if in the searching state
        if (searching) {
            // increase the searchTime counter
            searchTime++

            // if searchTime is above max search time then end run
            if (searchTime >= MAX_SEARCH_DURATION) {
                finishRunning(world, entity, time)
                return
            }
        }

        // if the target is present and entity can see the target player
        if (targetEntity.isPresent && entity.canSee(targetEntity.get())) {
            // get target
            val target = targetEntity.get()

            // if target is alive
            if (target.isAlive) {
                // if entity is in range of target
                if (entity.squaredDistanceTo(target) <= ATTACK_RANGE * ATTACK_RANGE) {
                    // try to attack the target
                    entity.tryAttack(target as Entity)
                } else {
                    // add target to LookWalkTarget memory
                    addLookWalkTargets(entity)
                }

                // reset last known location of target
                lastKnownLocation = Optional.of(target.blockPos)

                // Reset search time when target is visible
                searchTime = 0
            } else {
                // begin search for new target
                startSearching(entity)
            }
            // if player is not present or unable to be seen by the entity then perform a search
        } else if (searching) {
            performSearch(entity)
        } else {
            targetEntity = findPlayer(world, entity)
        }
    }

    private fun startSearching(entity: LivingEntity) {
        // if there is a last known location of the player
        if (lastKnownLocation.isPresent) {
            searching = true
            searchTime = 0
            entity.brain.remember(MemoryModuleType.WALK_TARGET, WalkTarget(lastKnownLocation.get(), WALK_SPEED, 1))
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
            entity.brain.remember(MemoryModuleType.WALK_TARGET, WalkTarget(randomPos, WALK_SPEED, 1))
        }
        if (searchTime >= MAX_SEARCH_DURATION) {
            searching = false
            targetEntity = findPlayer(entity.world as ServerWorld, entity)
        }

        // while searching listen for any disturbances to be used even
        val disturbanceLocation = entity.brain.getOptionalMemory(MemoryModuleType.DISTURBANCE_LOCATION)
        disturbanceLocation?.ifPresent {
            onEntityHeard(entity, it)
        }
    }

    fun onEntityHeard(entity: LivingEntity, pos: BlockPos) {
        if (searching && !targetEntity.isPresent) {
            searchTime = 0
            lastKnownLocation = Optional.of(pos)
            entity.brain.remember(MemoryModuleType.WALK_TARGET, WalkTarget(pos, WALK_SPEED, 1))
        }
    }
}
