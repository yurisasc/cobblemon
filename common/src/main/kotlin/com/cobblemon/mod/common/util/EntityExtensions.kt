/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.block.LeavesBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.ai.pathing.LandPathNodeMaker
import net.minecraft.entity.ai.pathing.PathNodeType
import net.minecraft.entity.passive.TameableEntity
import net.minecraft.server.world.ChunkTicketType
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.function.BooleanBiFunction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes
import kotlin.math.abs

fun Entity.setPositionSafely(pos: Vec3d): Boolean {
    var result = pos
    val eyes = pos.withAxis(Direction.Axis.Y, pos.y + this.standingEyeHeight)

    var box = boundingBox.offset(pos)
    val conflicts = mutableSetOf<Direction>()

    if (!world.getBlockCollisions(this, box).iterator().hasNext()) {
        setPosition(pos)
        return true
    }

    val yChanges = listOf(1.0, -1.0, 2.0, -2.0)
    var previousChange = 0.0
    for (yChange in yChanges) {
        box = box.offset(0.0, yChange - previousChange, 0.0)
        val it = world.getBlockCollisions(this, box).iterator()
        previousChange = yChange
        if (it.hasNext()) {
            continue
        } else {
            val roundedY = (pos.y + yChange).toInt()
            box = box.offset(0.0, roundedY - pos.y, 0.0)
            // If the rounded position actually collides again, then don't round at all.
            if (world.getBlockCollisions(this, box).iterator().hasNext()) {
                setPosition(pos.add(0.0, yChange, 0.0))
                return true
            }
            setPosition(Vec3d(pos.x, roundedY.toDouble(), pos.z))
            return true
        }
    }

    for (target in BlockPos.stream(box)) {
        val blockState = this.world.getBlockState(target)
        val collides = !blockState.isAir &&
                blockState.shouldSuffocate(this.world, target) &&
                VoxelShapes.matchesAnywhere(blockState.getCollisionShape(this.world, target)
                    .offset(target.x.toDouble(), target.y.toDouble(), target.z.toDouble()),
                    VoxelShapes.cuboid(box),
                    BooleanBiFunction.AND
                )
        if (collides) {
            val x = BlockPos(eyes)
            for (direction in Direction.values()) {
                if (conflicts.contains(direction)) continue

                val conflict = target.toVec3d()
                if (x.add(direction.vector) == target) {
                    conflicts.add(direction)
                    when (direction) {
                        Direction.UP -> return false
                        Direction.NORTH -> {
                            result = result.add(Vec3d(0.0, 0.0, 1 + (conflict.z - box.minZ + (1.0 / 8.0))))
                        }
                        Direction.SOUTH -> {
                            result = result.add(Vec3d(0.0, 0.0, -1 * (1 - (conflict.z - box.minZ) + (1.0 / 8.0))))
                        }
                        Direction.WEST -> {
                            result = result.add(Vec3d(1 + (conflict.x - box.minX  + (1.0 / 8.0)), 0.0, 0.0))
                        }
                        Direction.EAST -> {
                            result = result.add(Vec3d(-1 * (1 - (conflict.x - box.minX) + (1.0 / 8.0)), 0.0, 0.0))
                        }
                        else -> {}
                    }
                }
            }

        }
    }

    if (conflicts.size >= 3) {
        this.setPosition(pos)
    }

    this.setPosition(result)
    return true
}

fun Entity.distanceTo(pos: BlockPos): Double {
    val difference = pos.toVec3d().subtract(this.pos)
    return difference.length()
}

fun Entity.closestPosition(positions: Iterable<BlockPos>, filter: (BlockPos) -> Boolean = { true }): BlockPos? {
    var closest: BlockPos? = null
    var closestDistance = Double.MAX_VALUE

    val iterator = positions.iterator()
    while (iterator.hasNext()) {
        val position = iterator.next()
        if (filter(position)) {
            val distance = distanceTo(position)
            if (distance < closestDistance) {
                closest = BlockPos(position)
                closestDistance = distance
            }
        }
    }

    return closest
}

/**
 * Attempts to teleport near the owner.
 * If the teleport fails, it recalls the PokemonEntity to the owner's party.
 * Returns true if the teleport was successful.
 */
fun PokemonEntity.teleportToOwnerOrRecall() : Boolean {
    val success = this.teleportToOwner()
    if(!success) {
        // We need to load the chunk via ticket so the entity being recalled doesn't get stuck in the unloaded chunk.
        val chunkPos = ChunkPos(BlockPos(x, y, z))
        (world as ServerWorld).chunkManager.addTicket(
            ChunkTicketType.POST_TELEPORT, chunkPos, 0,
            id
        )
        pokemon.recall()
    }
    return success
}

/**
 * Attempts to teleport near the owner.
 * Returns true if successful.
 */
fun TameableEntity.teleportToOwner() : Boolean {
    // Derivative of net.minecraft.entity.ai.goal.FollowOwnerGoal.tryTeleport
    val blockPos = this.owner!!.blockPos
    var hasTeleported = false
    for (i in 0..24) {
        // Since the original method runs 10x every tick,
        // we make this one run 25x when attempted,
        // which makes it a much lower chance that it'll fail when it shouldn't.
        // This is sadly unavoidable, and can only be changed by changing how many times this loops.
        val j = this.random.nextInt(7) - 3
        val k = this.random.nextInt(3) - 1
        val l = this.random.nextInt(7) - 3
        hasTeleported = this.tryTeleportTo(blockPos.x + j, blockPos.y + k, blockPos.z + l)
        if (hasTeleported) {
            break
        }
    }
    return hasTeleported
}

private fun TameableEntity.tryTeleportTo(x : Int, y : Int, z : Int) : Boolean {
    return if (abs(x - this.owner!!.x) < 2.0 && abs(z - this.owner!!.z) < 2.0) {
        false
    } else if(!canTeleportTo(BlockPos(x,y,z))) {
        false
    } else {
        this.refreshPositionAndAngles(
            x.toDouble() + 0.5,
            y.toDouble(),
            z.toDouble() + 0.5,
            this.yaw,
            this.pitch
        )
        navigation.stop()
        true
    }
}

private fun TameableEntity.canTeleportTo(pos: BlockPos): Boolean {
    val pathNodeType = LandPathNodeMaker.getLandNodeType(world, pos.mutableCopy())
    return if (pathNodeType != PathNodeType.WALKABLE) {
        false
    } else {
        val blockState = world.getBlockState(pos.down())
        if (blockState.block is LeavesBlock) {
            false
        } else {
            val blockPos = pos.subtract(this.blockPos)
            world.isSpaceEmpty(this, this.boundingBox.offset(blockPos))
            true
        }
    }
}