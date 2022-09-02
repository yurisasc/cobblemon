package com.cablemc.pokemoncobbled.common.entity.pokemon.ai

import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.math.geometry.toDegrees
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.entity.ai.control.MoveControl
import net.minecraft.entity.ai.pathing.PathNodeType
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.tag.BlockTags
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper

class PokemonMoveControl(val pokemonEntity: PokemonEntity) : MoveControl(pokemonEntity) {
    override fun tick() {
        val behaviour = pokemonEntity.behaviour
        val mediumSpeed = if (pokemonEntity.getPoseType() in setOf(PoseType.FLY, PoseType.HOVER)) {
            behaviour.moving.fly.flySpeedHorizontal
        } else if (pokemonEntity.getPoseType() in setOf(PoseType.FLOAT, PoseType.SWIM)) {
            behaviour.moving.swim.swimSpeed
        } else {
            behaviour.moving.walk.walkSpeed
        }

        if (state == State.STRAFE) {
            val baseSpeed = entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED).toFloat()
            val adjustedSpeed = speed.toFloat() * baseSpeed * mediumSpeed
            var movingDistanceTotal = MathHelper.sqrt(forwardMovement * forwardMovement + sidewaysMovement * sidewaysMovement)
            if (movingDistanceTotal < 1.0f) {
                movingDistanceTotal = 1.0f
            }

            movingDistanceTotal = adjustedSpeed / movingDistanceTotal

            val adjustedForward = forwardMovement * movingDistanceTotal
            val adjustedStrafe = sidewaysMovement * movingDistanceTotal

            val xComponent = MathHelper.sin(entity.yaw.toRadians())
            val zComponent = MathHelper.cos(entity.yaw.toRadians())
            val xMovement = adjustedForward * zComponent - adjustedStrafe * xComponent
            val zMovement = adjustedStrafe * zComponent + adjustedForward * xComponent
            if (!isWalkable(xMovement, zMovement)) {
                forwardMovement = 1.0f
                sidewaysMovement = 0.0f
            }
            entity.movementSpeed = adjustedSpeed
            entity.setForwardSpeed(forwardMovement)
            entity.setSidewaysSpeed(sidewaysMovement)
            state = State.WAIT
        } else if (state == State.MOVE_TO) {
            state = State.WAIT
            val xDist = targetX - entity.x
            val zDist = targetZ - entity.z
            val yDist = targetY - entity.y
            val distanceFromTarget = xDist * xDist + yDist * yDist + zDist * zDist
            if (distanceFromTarget < 2.500000277905201E-7) {
                entity.setForwardSpeed(0.0f)
                return
            }
            val movingAngle = MathHelper.atan2(zDist, xDist).toDegrees() - 90.0f
            entity.yaw = wrapDegrees(entity.yaw, movingAngle, 90.0f)
            entity.movementSpeed = (speed * entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)).toFloat()
            val blockPos = entity.blockPos
            val blockState = entity.world.getBlockState(blockPos)
            val voxelShape = blockState.getCollisionShape(entity.world, blockPos)
            if (yDist > entity.stepHeight.toDouble() &&
                xDist * xDist + zDist * zDist < Math.max(1.0f, entity.width).toDouble() ||
                !voxelShape.isEmpty && entity.y < voxelShape.getMax(Direction.Axis.Y) + blockPos.y.toDouble() &&
                !blockState.isIn(BlockTags.DOORS) &&
                !blockState.isIn(BlockTags.FENCES)
            ) {
                entity.jumpControl.setActive()
                state = State.JUMPING
            }
        } else if (state == State.JUMPING) {
            entity.movementSpeed = (speed * entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)).toFloat()
            if (entity.isOnGround) {
                state = State.WAIT
            }
        } else {
            entity.setForwardSpeed(0.0f)
        }
    }

    private fun isWalkable(xMovement: Float, zMovement: Float): Boolean {
        val entityNavigation = entity.navigation
        if (entityNavigation != null) {
            val pathNodeMaker = entityNavigation.nodeMaker
            if (pathNodeMaker != null &&
                pathNodeMaker.getDefaultNodeType(
                    entity.world,
                    MathHelper.floor(entity.x + xMovement.toDouble()),
                    entity.blockY,
                    MathHelper.floor(entity.z + zMovement.toDouble())
                ) != PathNodeType.WALKABLE
            ) {
                return false
            }
        }
        return true
    }
}