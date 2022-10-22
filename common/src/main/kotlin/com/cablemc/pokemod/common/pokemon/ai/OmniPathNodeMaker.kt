/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.ai

import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import com.google.common.collect.Maps
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.ai.pathing.PathNode
import net.minecraft.entity.ai.pathing.PathNodeMaker
import net.minecraft.entity.ai.pathing.PathNodeType
import net.minecraft.entity.ai.pathing.TargetPathNode
import net.minecraft.entity.mob.MobEntity
import net.minecraft.fluid.FluidState
import net.minecraft.tag.FluidTags
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.world.BlockView
import net.minecraft.world.chunk.ChunkCache

/**
 * A path node maker that constructs paths knowing that the entity might be capable of
 * traveling across land, water, and air. This most closely resembles the aquatic
 * node maker.
 *
 * @author Hiroku
 * @since September 10th, 2022
 */
class OmniPathNodeMaker : PathNodeMaker() {
    private val nodePosToType: Long2ObjectMap<PathNodeType> = Long2ObjectOpenHashMap()

    fun canWalk(): Boolean {
        return if (this.entity is PokemonEntity) {
            (this.entity as PokemonEntity).behaviour.moving.walk.canWalk
        } else {
            true
        }
    }

    fun canSwimUnderwater(): Boolean {
        return if (this.entity is PokemonEntity) {
            (this.entity as PokemonEntity).behaviour.moving.swim.canBreatheUnderwater
        } else {
            false
        }
    }

    fun canSwimUnderlava(): Boolean {
        return if (this.entity is PokemonEntity) {
            (this.entity as PokemonEntity).behaviour.moving.swim.canBreatheUnderlava
        } else {
            false
        }
    }

    fun canSwimUnderFluid(fluidState: FluidState): Boolean {
        return if (this.entity is PokemonEntity) {
            if (fluidState.isIn(FluidTags.LAVA)) {
                (this.entity as PokemonEntity).behaviour.moving.swim.canSwimInLava
            } else if (fluidState.isIn(FluidTags.WATER)) {
                (this.entity as PokemonEntity).behaviour.moving.swim.canSwimInWater
            } else {
                false
            }
        } else {
            false
        }
    }

    fun canFly(): Boolean {
        return if (this.entity is PokemonEntity) {
            (this.entity as PokemonEntity).behaviour.moving.fly.canFly
        } else {
            false
        }
    }

    override fun init(cachedWorld: ChunkCache, entity: MobEntity) {
        super.init(cachedWorld, entity)
        nodePosToType.clear()
    }

    override fun clear() {
        super.clear()
        nodePosToType.clear()
    }

    override fun getStart(): PathNode? {
        return super.getNode(
            MathHelper.floor(entity.boundingBox.minX),
            MathHelper.floor(entity.boundingBox.minY + 0.5),
            MathHelper.floor(entity.boundingBox.minZ)
        )
    }

    override fun getNode(x: Double, y: Double, z: Double): TargetPathNode? {
        return asTargetPathNode(super.getNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)))
    }

    override fun getSuccessors(successors: Array<PathNode>, node: PathNode): Int {
        var i = 0
        val map = Maps.newEnumMap<Direction, PathNode?>(Direction::class.java)
        val upperMap = Maps.newEnumMap<Direction, PathNode?>(Direction::class.java)
        val lowerMap = Maps.newEnumMap<Direction, PathNode?>(Direction::class.java)

        val upIsOpen = cachedWorld.getBlockState(node.blockPos.up()).canPathfindThrough(cachedWorld, node.blockPos, NavigationType.AIR)

        // Non-diagonal surroundings in 3d space
        for (direction in Direction.values()) {
            val pathNode = this.getNode(node.x + direction.offsetX, node.y + direction.offsetY, node.z + direction.offsetZ) ?: continue
            map[direction] = pathNode
            if (!hasNotVisited(pathNode)) {
                continue
            }
            successors[i++] = pathNode
        }

        // Diagonals
        for (direction in Direction.Type.HORIZONTAL.iterator()) {
            val direction2 = direction.rotateYClockwise()
            val x = node.x + direction.offsetX + direction2.offsetX
            val z = node.z + direction.offsetZ + direction2.offsetZ
            val pathNode2 = this.getNode(x, node.y, z) ?: continue
            if (isAccessibleDiagonal(pathNode2, map[direction], map[direction2])) {
                successors[i++] = pathNode2
            }
        }

        // Upward non-diagonals
        for (direction in Direction.Type.HORIZONTAL.iterator()) {
            val pathNode2 = getNode(node.x + direction.offsetX, node.y + 1, node.z + direction.offsetZ) ?: continue
            if (upIsOpen && hasNotVisited(pathNode2)) {
                successors[i++] = pathNode2
                upperMap[direction] = pathNode2
            }
        }

        // Upward diagonals
        for (direction in Direction.Type.HORIZONTAL.iterator()) {
            val direction2 = direction.rotateYClockwise()
            val pathNode2 = getNode(node.x + direction.offsetX + direction2.offsetX, node.y + 1, node.z + direction.offsetZ + direction2.offsetZ) ?: continue
            if (isAccessibleDiagonal(pathNode2, upperMap[direction], upperMap[direction2])) {
                successors[i++] = pathNode2
            }
        }

        val connectingBlockPos = BlockPos.Mutable()
        // Downward non-diagonals
        for (direction in Direction.Type.HORIZONTAL.iterator()) {
            connectingBlockPos.set(node.blockPos.add(direction.vector))
            val blockState = cachedWorld.getBlockState(connectingBlockPos)
            val traversibleByTangent = blockState.canPathfindThrough(cachedWorld, connectingBlockPos, NavigationType.AIR)
            val pathNode2 = getNode(node.x + direction.offsetX, node.y - 1, node.z + direction.offsetZ) ?: continue
            if (hasNotVisited(pathNode2) && traversibleByTangent) {
                successors[i++] = pathNode2
                lowerMap[direction] = pathNode2
            }
        }

        // Downward diagonals
        for (direction in Direction.Type.HORIZONTAL.iterator()) {
            val direction2 = direction.rotateYClockwise()
            val pathNode2 = getNode(node.x + direction.offsetX + direction2.offsetX, node.y - 1, node.z + direction.offsetZ + direction2.offsetZ) ?: continue
            if (isAccessibleDiagonal(pathNode2, lowerMap[direction], lowerMap[direction2])) {
                successors[i++] = pathNode2
            }
        }

        return i
    }

    fun hasNotVisited(pathNode: PathNode?): Boolean {
        return pathNode != null && !pathNode.visited
    }

    fun isAccessibleDiagonal(pathNode: PathNode?, vararg borderNodes: PathNode?): Boolean {
        return hasNotVisited(pathNode) && borderNodes.all { it != null && it.penalty >= 0.0F }
    }

    fun isValidPathNodeType(pathNodeType: PathNodeType): Boolean {
        return when {
            pathNodeType == PathNodeType.BREACH && (canWalk() || canFly()) -> true
            (pathNodeType == PathNodeType.WATER || pathNodeType == PathNodeType.WATER_BORDER) && canSwimUnderwater() -> true
            pathNodeType == PathNodeType.OPEN && canFly() -> true
            pathNodeType == PathNodeType.WALKABLE && canWalk() -> true
            else -> false
        }
    }

    override fun getNode(x: Int, y: Int, z: Int): PathNode? {
        var nodePenalty = 0F
        var pathNode: PathNode? = null
        val pathNodeType = addPathNodePos(x, y, z)
        if (isValidPathNodeType(pathNodeType) &&
            entity.getPathfindingPenalty(pathNodeType).also { nodePenalty = it } >= 0.0f &&
            super.getNode(x, y, z).also { pathNode = it } != null
        ) {
            pathNode!!.type = pathNodeType
            pathNode!!.penalty = pathNode!!.penalty.coerceAtLeast(nodePenalty)
//            if (cachedWorld.getFluidState(BlockPos(x, y, z)).isEmpty && !canSwimUnderwater()) {
//                pathNode!!.penalty += 8.0f
//            }
        }
        return pathNode
    }

    fun addPathNodePos(x: Int, y: Int, z: Int): PathNodeType {
        return nodePosToType.computeIfAbsent(BlockPos.asLong(x, y, z), Long2ObjectFunction { getDefaultNodeType(cachedWorld, x, y, z) })
    }

    override fun getDefaultNodeType(world: BlockView, x: Int, y: Int, z: Int): PathNodeType {
        return getNodeType(
            world, x, y, z,
            entity, entityBlockXSize, entityBlockYSize, entityBlockZSize,
            canOpenDoors(), canEnterOpenDoors()
        )
    }

    override fun getNodeType(
        world: BlockView,
        x: Int,
        y: Int,
        z: Int,
        mob: MobEntity?,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        canOpenDoors: Boolean,
        canEnterOpenDoors: Boolean
    ): PathNodeType {
        val mutable = BlockPos.Mutable()
        for (j in y until y + sizeY) {
            mutable.set(x, j, z)
            val fluidState = world.getFluidState(mutable)
            val blockState = world.getBlockState(mutable)
            val airMovable = blockState.canPathfindThrough(world, mutable, NavigationType.AIR)
            val below = cachedWorld.getBlockState(mutable.down())
            val waterBelow = below.fluidState.isIn(FluidTags.WATER)
            val lavaBelow = below.fluidState.isIn(FluidTags.LAVA)
            if (((lavaBelow && canSwimUnderlava()) || waterBelow) && airMovable) {
                return PathNodeType.BREACH
            } else if (fluidState.isIn(FluidTags.WATER)) {
                continue
//                    } else if (blockState.canPathfindThrough(world, mutable.down() as BlockPos, NavigationType.LAND)) {
            } else if (airMovable) {
                continue
            } else if (canSwimUnderlava() && fluidState.isIn(FluidTags.LAVA)) {
                continue
            }
            return PathNodeType.BLOCKED
        }

        val below = BlockPos(x, y - 1, z)
        val blockState = world.getBlockState(mutable.set(x, y, z))
        val blockStateBelow = world.getBlockState(below)
        val isWater = blockState.fluidState.isIn(FluidTags.WATER)
        val isLava = blockState.fluidState.isIn(FluidTags.LAVA) // NavigationType.WATER is an explicit water check, lava needs more work
        return if ((isWater && canSwimUnderwater()) || (isLava && canSwimUnderlava())) {
            PathNodeType.WATER
        } else if (canFly() && blockState.canPathfindThrough(world, mutable, NavigationType.AIR) && blockStateBelow.canPathfindThrough(world, below, NavigationType.AIR)) {
            PathNodeType.OPEN
        } else if (canWalk() && blockState.canPathfindThrough(world, mutable, NavigationType.LAND) && !blockStateBelow.canPathfindThrough(world, below, NavigationType.AIR)) {
            PathNodeType.WALKABLE
        } else PathNodeType.BLOCKED
    }
}