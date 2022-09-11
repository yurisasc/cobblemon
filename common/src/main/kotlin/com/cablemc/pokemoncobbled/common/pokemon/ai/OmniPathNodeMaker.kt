package com.cablemc.pokemoncobbled.common.pokemon.ai

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
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

    override fun getSuccessors(successors: Array<PathNode?>, node: PathNode): Int {
        var i = 0
        val map = Maps.newEnumMap<Direction, PathNode?>(Direction::class.java)
        val upperMap = Maps.newEnumMap<Direction, PathNode?>(Direction::class.java)
        val lowerMap = Maps.newEnumMap<Direction, PathNode?>(Direction::class.java)

        // Non-diagonal surroundings in 3d space
        for (direction in Direction.values()) {
            val pathNode = this.getNode(node.x + direction.offsetX, node.y + direction.offsetY, node.z + direction.offsetZ)
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
            val pathNode2 = this.getNode(x, node.y, z)
            if (isAccessibleDiagonal(pathNode2, map[direction], map[direction2])) {
                successors[i++] = pathNode2
            }
        }

        // Upward non-diagonals
        for (direction in Direction.Type.HORIZONTAL.iterator()) {
            val pathNode2 = getNode(node.x + direction.offsetX, node.y + 1, node.z + direction.offsetZ)
            if (isAccessibleDiagonal(pathNode2, map[direction], map[Direction.UP])) {
                successors[i++] = pathNode2
                upperMap[direction] = pathNode2
            }
        }

        // Upward diagonals
        for (direction in Direction.Type.HORIZONTAL.iterator()) {
            val direction2 = direction.rotateYClockwise()
            val pathNode2 = getNode(node.x + direction.offsetX + direction2.offsetX, node.y + 1, node.z + direction.offsetZ + direction2.offsetZ)
            if (isAccessibleDiagonal(pathNode2, upperMap[direction], upperMap[direction2])) {
                successors[i++] = pathNode2
            }
        }

        // Downward non-diagonals
        for (direction in Direction.Type.HORIZONTAL.iterator()) {
            val pathNode2 = getNode(node.x + direction.offsetX, node.y - 1, node.z + direction.offsetZ)
            if (isAccessibleDiagonal(pathNode2, map[direction], map[Direction.DOWN])) {
                successors[i++] = pathNode2
                lowerMap[direction] = pathNode2
            }
        }

        // Downward diagonals
        for (direction in Direction.Type.HORIZONTAL.iterator()) {
            val direction2 = direction.rotateYClockwise()
            val pathNode2 = getNode(node.x + direction.offsetX + direction2.offsetX, node.y - 1, node.z + direction.offsetZ + direction2.offsetZ)
            if (isAccessibleDiagonal(pathNode2, upperMap[direction], upperMap[direction2])) {
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
        if (pathNodeType == PathNodeType.BREACH && canWalk()) {
            return true
        } else if (pathNodeType in arrayOf(PathNodeType.WATER, PathNodeType.WATER_BORDER) && canSwimUnderwater()) {
            return true
        } else if (pathNodeType == PathNodeType.OPEN && canFly()) {
            return true
        } else if (pathNodeType == PathNodeType.WALKABLE && canWalk()){
            return true
        } else {
            return false
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

    protected fun addPathNodePos(x: Int, y: Int, z: Int): PathNodeType {
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
        for (i in x until x + sizeX) {
            for (j in y until y + sizeY) {
                for (k in z until z + sizeZ) {
                    mutable.set(i, j, k)
                    val fluidState = world.getFluidState(mutable)
                    val blockState = world.getBlockState(mutable)
                    if (fluidState.isEmpty &&
                        blockState.canPathfindThrough(world, mutable.down() as BlockPos, NavigationType.WATER) &&
                        blockState.isAir
                    ) {
                        return PathNodeType.BREACH
                    }
                    if (fluidState.isIn(FluidTags.WATER)) {
                        continue
                    } else if ((canWalk() || canFly()) && blockState.canPathfindThrough(world, mutable.down() as BlockPos, NavigationType.LAND)) {
                        continue
                    } else if (canSwimUnderlava() && fluidState.isIn(FluidTags.LAVA)) {
                        return PathNodeType.WATER
                    }
                    return PathNodeType.BLOCKED
                }
            }
        }

        val below = BlockPos(x, y - 1, z)
        val blockState2 = world.getBlockState(mutable)
        val blockStateBelow = world.getBlockState(below)
        val isWater = blockState2.fluidState.isIn(FluidTags.WATER)
        val isLava = blockState2.fluidState.isIn(FluidTags.LAVA) // NavigationType.WATER is an explicit water check, lava needs more work
        return if (isWater && blockState2.canPathfindThrough(world, mutable, NavigationType.WATER)) {
            PathNodeType.WATER
        } else if (canFly() && blockState2.canPathfindThrough(world, mutable, NavigationType.AIR) && blockStateBelow.canPathfindThrough(world, below, NavigationType.AIR)) {
            PathNodeType.OPEN
        } else if (canWalk() && blockState2.canPathfindThrough(world, mutable, NavigationType.LAND)) {
            PathNodeType.WALKABLE
        } else PathNodeType.BLOCKED
    }
}