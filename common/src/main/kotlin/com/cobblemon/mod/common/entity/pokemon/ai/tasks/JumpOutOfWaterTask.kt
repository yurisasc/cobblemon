package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.google.common.collect.ImmutableMap
import net.minecraft.entity.ai.brain.MemoryModuleState
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.task.MultiTickTask
import net.minecraft.fluid.FluidState
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

class JumpOutOfWaterTask : MultiTickTask<PokemonEntity>(
    ImmutableMap.of(
        MemoryModuleType.IS_IN_WATER, MemoryModuleState.VALUE_PRESENT,
        MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT
    )
){
    var inWater = false
    companion object {
        private const val MAX_DURATION = 80
        private const val CHANCE = 10
        private val OFFSET_MULTIPLIERS = intArrayOf(0, 1, 4, 5, 6, 7)
    }

    override fun shouldRun(world: ServerWorld, entity: PokemonEntity): Boolean {
        if(entity.random.nextInt(CHANCE) != 0) {
            return false
        } else {
            val direction = entity.movementDirection
            val x = direction.offsetX
            val z = direction.offsetZ
            val blockPos = entity.blockPos
            val offsets = OFFSET_MULTIPLIERS
            for (offset in offsets) {
                if (!this.isWater(entity, blockPos, x, z, offset) || !this.isAirAbove(entity, blockPos, x, z, offset)) {
                    return false
                }
            }
            return true
        }
    }

    override fun shouldKeepRunning(world: ServerWorld, entity: PokemonEntity, time: Long): Boolean {
        val d: Double = entity.getVelocity().y
        return (!(d * d < 0.029999999329447746) || entity.getPitch() == 0.0f || !(Math.abs(entity.getPitch()) < 10.0f) || !entity.isTouchingWater()) && !entity.isOnGround()
    }

    override fun run(world: ServerWorld, entity: PokemonEntity, time: Long) {
        val direction: Direction = entity.getMovementDirection()
        entity.setVelocity(
            entity.getVelocity().add(direction.offsetX.toDouble() * 0.6, 0.7, direction.offsetZ.toDouble() * 0.6)
        )
        entity.getNavigation().stop()
    }

    override fun keepRunning(world: ServerWorld, entity: PokemonEntity, time: Long) {
        val bl: Boolean = this.inWater
        if (!bl) {
            val fluidState: FluidState = entity.getWorld().getFluidState(entity.getBlockPos())
            this.inWater = fluidState.isIn(FluidTags.WATER)
        }

        if (this.inWater && !bl) {
            entity.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0f, 1.0f)
        }

        val vec3d: Vec3d = entity.getVelocity()
        if (vec3d.y * vec3d.y < 0.029999999329447746 && entity.getPitch() != 0.0f) {
            entity.setPitch(MathHelper.lerpAngleDegrees(0.2f, entity.getPitch(), 0.0f))
        } else if (vec3d.length() > 9.999999747378752E-6) {
            val d = vec3d.horizontalLength()
            val e = Math.atan2(-vec3d.y, d) * 57.2957763671875
            entity.setPitch(e.toFloat())
        }
    }

    
    
    private fun isWater(entity: PokemonEntity, pos: BlockPos, offsetX: Int, offsetZ: Int, multiplier: Int): Boolean {
        val blockPos = pos.add(offsetX * multiplier, 0, offsetZ * multiplier)
        return entity.world.getFluidState(blockPos).isIn(FluidTags.WATER) && !entity.world
            .getBlockState(blockPos).blocksMovement()
    }

    private fun isAirAbove(entity: PokemonEntity, pos: BlockPos, offsetX: Int, offsetZ: Int, multiplier: Int): Boolean {
        return entity.world.getBlockState(pos.add(offsetX * multiplier, 1, offsetZ * multiplier))
            .isAir && entity.world.getBlockState(pos.add(offsetX * multiplier, 2, offsetZ * multiplier))
            .isAir
    }
}