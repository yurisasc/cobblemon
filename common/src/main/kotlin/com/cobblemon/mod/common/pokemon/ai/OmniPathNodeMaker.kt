/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.ai

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.canFit
import com.google.common.collect.Maps
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.minecraft.block.AbstractRailBlock
import net.minecraft.block.Blocks
import net.minecraft.block.FenceGateBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.entity.ai.pathing.LandPathNodeMaker
import net.minecraft.entity.ai.pathing.PathContext
import net.minecraft.entity.ai.pathing.PathNode
import net.minecraft.entity.ai.pathing.TargetPathNode
import net.minecraft.entity.mob.MobEntity
import net.minecraft.fluid.FluidState
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.FluidTags
import net.minecraft.util.Mth
import net.minecraft.world.chunk.ChunkCache
import net.minecraft.world.level.pathfinder.NodeEvaluator
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.level.pathfinder.PathType
import net.minecraft.world.phys.Vec3
import java.util.*

/**
 * A path node maker that constructs paths knowing that the entity might be capable of
 * traveling across land, water, and air. This most closely resembles the aquatic
 * node maker.
 *
 * @author Hiroku
 * @since September 10th, 2022
 */
class OmniPathNodeMaker : NodeEvaluator() {
    private val nodePosToType: Long2ObjectMap<PathType> = Long2ObjectOpenHashMap()

    var canPathThroughFire: Boolean = false

    override fun init(cachedWorld: ChunkCache, entity: MobEntity) {
        super.init(cachedWorld, entity)
        nodePosToType.clear()
    }

    override fun clear() {
        super.clear()
        nodePosToType.clear()
    }

    override fun getNode(x: Double, y: Double, z: Double): TargetPathNode {
        return TargetPathNode(super.getNode(Mth.floor(x), Mth.floor(y + 0.5), Mth.floor(z)))
    }

    override fun getStart(): PathNode? {
        val x = Mth.floor(entity.boundingBox.minX)
        val y = Mth.floor(entity.boundingBox.minY + 0.5)
        val z = Mth.floor(entity.boundingBox.minZ)
        val node = super.getNode(x, y, z)
        node.type = this.getNodeType(entity, node.blockPos)
        node.penalty = entity.getPathfindingPenalty(node.type)
        return node
    }

    fun getNodeType(entity: MobEntity, pos: BlockPos): PathType? {
        return this.getNodeType(entity, pos.x, pos.y, pos.z)
    }

    fun getNodeType(entity: MobEntity, x: Int, y: Int, z: Int): PathType? {
        return this.nodePosToType.computeIfAbsent(
            BlockPos.asLong(x, y, z),
            Long2ObjectFunction<PathType?> {
                this.getNodeType(
                    context, x, y, z,
                    entity
                )
            })
    }
    
    override fun getSuccessors(successors: Array<PathNode?>, node: PathNode): Int {
        var i = 0
        val map = Maps.newEnumMap<Direction, PathNode?>(Direction::class.java)
        val upperMap = Maps.newEnumMap<Direction, PathNode?>(Direction::class.java)
        val lowerMap = Maps.newEnumMap<Direction, PathNode?>(Direction::class.java)

        val upIsOpen = entity.canFit(node.blockPos.up())

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
        for (direction in Direction.Plane.HORIZONTAL.iterator()) {
            val direction2 = direction.rotateYClockwise()
            val x = node.x + direction.offsetX + direction2.offsetX
            val z = node.z + direction.offsetZ + direction2.offsetZ
            val pathNode2 = this.getNode(x, node.y, z) ?: continue
            // Skip 'inaccessible' diagonals if we're pathing from a blocked node since we're trying to get unstuck
            if (isAccessibleDiagonal(pathNode2, map[direction], map[direction2]) || (node.type == PathType.BLOCKED && !pathNode2.visited)) {
                successors[i++] = pathNode2
            }
        }

        // Upward non-diagonals
        for (direction in Direction.Plane.HORIZONTAL.iterator()) {
            val pathNode2 = getNode(node.x + direction.offsetX, node.y + 1, node.z + direction.offsetZ) ?: continue
            if (upIsOpen && hasNotVisited(pathNode2)) {
                successors[i++] = pathNode2
                upperMap[direction] = pathNode2
            }
        }

        // Upward diagonals
        for (direction in Direction.Plane.HORIZONTAL.iterator()) {
            val direction2 = direction.rotateYClockwise()
            val pathNode2 = getNode(node.x + direction.offsetX + direction2.offsetX, node.y + 1, node.z + direction.offsetZ + direction2.offsetZ) ?: continue
            if (isAccessibleDiagonal(pathNode2, upperMap[direction], upperMap[direction2])) {
                successors[i++] = pathNode2
            }
        }

        val connectingBlockPos = BlockPos.MutableBlockPos()
        // Downward non-diagonals
        for (direction in Direction.Plane.HORIZONTAL.iterator()) {
            connectingBlockPos.set(node.blockPos.add(direction.vector))
            val blockState = context.getBlockState(connectingBlockPos)
            val traversableByTangent = blockState.isPathfindable(PathComputationType.AIR)
            val pathNode2 = getNode(node.x + direction.offsetX, node.y - 1, node.z + direction.offsetZ) ?: continue
            if (hasNotVisited(pathNode2) && traversableByTangent) {
                successors[i++] = pathNode2
                lowerMap[direction] = pathNode2
            }
        }

        // Downward diagonals
        for (direction in Direction.Plane.HORIZONTAL.iterator()) {
            val direction2 = direction.rotateYClockwise()
            val pathNode2 = getNode(node.x + direction.offsetX + direction2.offsetX, node.y - 1, node.z + direction.offsetZ + direction2.offsetZ) ?: continue
            if (isAccessibleDiagonal(pathNode2, lowerMap[direction], lowerMap[direction2])) {
                successors[i++] = pathNode2
            }
        }

        // If they're in a blocked node and there are multiple successors, choose whichever is closest to get out of the blocked position.
        // This addresses instances where they're next to a fence and should move away from the fence in the nearest open
        // direction before regular pathing.
        if (entity.getPathfindingPenalty(node.type) < 0 && i > 1) {
            val x = entity.boundingBox.minX
            val y = entity.boundingBox.minY + 0.5
            val z = entity.boundingBox.minZ
            val pos = Vec3(x, y, z)

            var n = 1
            var closestSuccessor = successors[0]!!
            var closestDistance = closestSuccessor.pos.add(0.5, 0.0, 0.5).distanceTo(pos)

            while (n < i) {
                val next = successors[n]!!
                val nextDist = next.pos.add(0.5, 0.0, 0.5).distanceTo(pos)
                if (nextDist < closestDistance) {
                    closestSuccessor = next
                    closestDistance = nextDist
                }

                n++
            }

            successors[0] = closestSuccessor
            i = 1
        }

        return i
    }

    fun hasNotVisited(pathNode: PathNode?): Boolean {
        return pathNode != null && !pathNode.visited
    }

    fun isAccessibleDiagonal(pathNode: PathNode?, vararg borderNodes: PathNode?): Boolean {
        return hasNotVisited(pathNode) && borderNodes.all { it != null && it.penalty >= 0.0F }
    }

    fun isValidPathType(PathType: PathType): Boolean {
        return when {
            (PathType == PathType.BREACH || PathType == PathType.WATER || PathType == PathType.WATER_BORDER) && canSwimInWater() -> true
            PathType == PathType.OPEN && canFly() -> true
            PathType == PathType.WALKABLE && (canWalk() || canFly()) -> true
            else -> false
        }
    }

    override fun getNode(x: Int, y: Int, z: Int): PathNode? {
        var nodePenalty = 0F
        var pathNode: PathNode? = null

        val PathType = addPathNodePos(x, y, z)
        if (isValidPathType(PathType) &&
            entity.getPathfindingPenalty(PathType).also { nodePenalty = it } >= 0.0f &&
            super.getNode(x, y, z).also { pathNode = it } != null
        ) {
            pathNode!!.type = PathType
            pathNode!!.penalty = pathNode!!.penalty.coerceAtLeast(nodePenalty)
        }
        return pathNode
    }

    fun addPathNodePos(x: Int, y: Int, z: Int): PathType {
        return nodePosToType.computeIfAbsent(BlockPos.asLong(x, y, z), Long2ObjectFunction { getNodeType(context, x, y, z, entity) })
    }

    override fun getDefaultNodeType(context: PathContext, x: Int, y: Int, z: Int): PathType? {
        val pos = BlockPos(x, y, z)
        val below = BlockPos(x, y - 1, z)
        val blockState = context.getBlockState(pos)
        val blockStateBelow = context.getBlockState(below)
        val belowSolid = blockStateBelow.isSolid
        val isWater = blockState.fluidState.isIn(FluidTags.WATER)
        val isLava = blockState.fluidState.isIn(FluidTags.LAVA)
        val canBreatheUnderFluid = canSwimUnderFluid(blockState.fluidState)
        val solid = blockState.isSolid

        /*
         * There are a lot of commented out pairs of checks here. I was experimenting with how to simultaneously
         * fix the following situations (without breaking any in the process):
         * - Walking up slabs
         * - Walking up stairs
         * - Lifting off from snow layers
         * - Lifting off from carpets.
         *
         * It seems to work now but nothing works forever so my other attempts are here for reference.
         */

        val figuredNode = if (blockStateBelow.isIn(BlockTags.FENCES) || blockStateBelow.isIn(BlockTags.WALLS) || (blockStateBelow.block is FenceGateBlock && !blockStateBelow.get(FenceGateBlock.OPEN))) {
            PathType.FENCE
        } else if (isWater && belowSolid && !canSwimInWater() && canBreatheUnderFluid) {
            PathType.WALKABLE
        } else if (isWater || (isLava && canSwimUnderlava())) {
            PathType.WATER
            // This breaks lifting off from snow layers and carpets
//        } else if (blockState.canPathfindThrough(world, pos, NavigationType.LAND) && !blockStateBelow.canPathfindThrough(world, below, NavigationType.AIR)) {
//            PathType.WALKABLE
//        } else if (blockState.canPathfindThrough(world, pos, NavigationType.AIR) && blockStateBelow.canPathfindThrough(world, below, NavigationType.AIR)) {
//            PathType.OPEN
        } else if (!solid && belowSolid) {
            PathType.WALKABLE
        } else if (!solid && !belowSolid) {
            PathType.OPEN
            // This breaks walking up slabs
//        } else if (blockState.canPathfindThrough(world, pos, NavigationType.LAND) && blockStateBelow.isSideSolid(world, below, Direction.UP, SideShapeType.FULL)) {
//            PathType.WALKABLE
//        } else if (blockState.canPathfindThrough(world, pos, NavigationType.AIR) && !blockStateBelow.isSideSolid(world, below, Direction.UP, SideShapeType.FULL)) {
//            PathType.OPEN
        } else PathType.BLOCKED

        return adjustNodeType(context, canOpenDoors, canEnterOpenDoors, below, figuredNode)
    }

    override fun getNodeType(
        context: PathContext,
        x: Int,
        y: Int,
        z: Int,
        mob: MobEntity
    ): PathType? {
        val set = EnumSet.noneOf(PathType::class.java)
        val sizeX = (mob.boundingBox.maxX - mob.boundingBox.minX).toInt() + 1
        val sizeY = (mob.boundingBox.maxY - mob.boundingBox.minY).toInt() + 1
        val sizeZ = (mob.boundingBox.maxZ - mob.boundingBox.minZ).toInt() + 1
        val type = findNearbyNodeTypes(context, x, y, z, sizeX, sizeY, sizeZ, canOpenDoors, canEnterOpenDoors, set, PathType.BLOCKED,
            BlockPos(x, y, z)
        )

        if (PathType.DAMAGE_CAUTIOUS in set) {
            return PathType.DAMAGE_CAUTIOUS
        } else if (PathType.DANGER_OTHER in set) {
            return PathType.DANGER_OTHER
        }
        return if (PathType.FENCE in set) {
            PathType.FENCE
        } else if (PathType.UNPASSABLE_RAIL in set) {
            PathType.UNPASSABLE_RAIL
        } else if (PathType.DAMAGE_OTHER in set) {
            PathType.DAMAGE_OTHER
        } else {
            var PathType2: PathType? = PathType.BLOCKED
            val nearbyTypeIterator = set.iterator()
            while (nearbyTypeIterator.hasNext()) {
                val nearbyType = nearbyTypeIterator.next()
                if (mob.getPathfindingPenalty(nearbyType) < 0) {
                    return nearbyType
                }
                // The || is because we prefer WALKABLE where possible - OPEN is legit but if there's either OPEN or WALKABLE then WALKABLE is better since land pokes can read that.
                if (mob.getPathfindingPenalty(nearbyType) > mob.getPathfindingPenalty(PathType2) || (nearbyType == PathType.WALKABLE)) {
                    PathType2 = nearbyType
                } else if (type == PathType.WATER && nearbyType == PathType.WATER) {
                    PathType2 = PathType.WATER
                }
            }
            if (type == PathType.OPEN && mob.getPathfindingPenalty(PathType2) == 0.0f && sizeX <= 1) {
                PathType.OPEN
            } else {
                PathType2!!
            }
        }
    }

    fun findNearbyNodeTypes(
        context: PathContext,
        x: Int,
        y: Int,
        z: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        canOpenDoors: Boolean,
        canEnterOpenDoors: Boolean,
        nearbyTypes: EnumSet<PathType>,
        type: PathType,
        pos: BlockPos
    ): PathType {
        var type = type
        for (i in 0 until sizeX) {
            for (j in 0 until sizeY) {
                for (k in 0 until sizeZ) {
                    val l = i + x
                    val m = j + y
                    val n = k + z
                    val PathType = getDefaultNodeType(context, l, m, n)
                    if (i == 0 && j == 0 && k == 0) {
                        if (PathType != null) {
                            type = PathType
                        }
                    }
                    nearbyTypes.add(PathType)
                }
            }
        }
        return type
    }

    protected fun adjustNodeType(
        context: PathContext,
        canOpenDoors: Boolean,
        canEnterOpenDoors: Boolean,
        pos: BlockPos,
        type: PathType
    ): PathType {
        val blockState = context.getBlockState(pos)
        val block = blockState.block

        if (blockState.isOf(Blocks.CACTUS) || blockState.isOf(Blocks.SWEET_BERRY_BUSH)) {
            return PathType.DANGER_OTHER
        }

        if (LandPathNodeMaker.isFireDamaging(blockState) && !this.canPathThroughFire) {
            return PathType.DANGER_FIRE
        }

        if (context.getBlockState(pos).fluidState.isIn(FluidTags.WATER)) {
            return PathType.WATER_BORDER
        }

        if (blockState.isOf(Blocks.WITHER_ROSE) || blockState.isOf(Blocks.POINTED_DRIPSTONE)) {
            return PathType.DAMAGE_CAUTIOUS
        }

        return if (type == PathType.DOOR_WOOD_CLOSED && canOpenDoors && canEnterOpenDoors) {
            PathType.WALKABLE_DOOR
        } else if (type == PathType.DOOR_OPEN && !canEnterOpenDoors) {
            PathType.BLOCKED
        } else if (type == PathType.RAIL && block !is AbstractRailBlock && context.getBlockState(pos.down()).block !is AbstractRailBlock) {
            PathType.UNPASSABLE_RAIL
        } else if (type == PathType.LEAVES) {
            PathType.BLOCKED
        } else type
    }

    fun canWalk(): Boolean {
        return if (this.entity is PokemonEntity) {
            (this.entity as PokemonEntity).behaviour.moving.walk.canWalk
        } else {
            true
        }
    }

     fun canSwimInWater(): Boolean {
         return if (this.entity is PokemonEntity) {
             (this.entity as PokemonEntity).behaviour.moving.swim.canSwimInWater
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
                (this.entity as PokemonEntity).behaviour.moving.swim.canBreatheUnderlava
            } else if (fluidState.isIn(FluidTags.WATER)) {
                (this.entity as PokemonEntity).behaviour.moving.swim.canBreatheUnderwater
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
}