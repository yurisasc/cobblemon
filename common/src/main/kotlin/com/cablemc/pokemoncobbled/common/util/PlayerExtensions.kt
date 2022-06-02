package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import java.util.*

// Stuff like getting their party
fun ServerPlayerEntity.party() = PokemonCobbled.storage.getParty(this)
fun UUID.getPlayer() = getServer()?.playerManager?.getPlayer(this)

fun Entity.sendServerMessage(component: Text) {
    sendSystemMessage(component, Util.NIL_UUID)
}

fun Entity.sendTranslatedServerMessage(str: String) {
    sendServerMessage(str.asTranslated())
}

class TraceResult(
    val location: Vec3d,
    val blockPos: BlockPos,
    val direction: Direction
)

fun Entity.isLookingAt(other: Entity, maxDistance: Float = 10F, stepDistance: Float = 0.01F): Boolean {
    var step = stepDistance
    val startPos = eyePos
    val direction = rotationVector

    while (step <= maxDistance) {
        val location = startPos.add(direction.multiply(step.toDouble()))
        step += stepDistance

        if (location in other.boundingBox) {
            return true
        }
    }
    return false
}

class EntityTraceResult<T : Entity>(
    val location: Vec3d,
    val entities: Iterable<T>
)

fun <T : Entity> PlayerEntity.traceFirstEntityCollision(
    maxDistance: Float = 10F,
    stepDistance: Float = 0.05F,
    entityClass: Class<T>
): T? {
    return traceEntityCollision(
        maxDistance,
        stepDistance,
        entityClass
    )?.let { it.entities.minByOrNull { it.distanceTo(this) } }
}

fun <T : Entity> PlayerEntity.traceEntityCollision(
    maxDistance: Float = 10F,
    stepDistance: Float = 0.05F,
    entityClass: Class<T>
): EntityTraceResult<T>? {
    var step = stepDistance
    val startPos = eyePos
    val direction = rotationVector
    val maxDistanceVector = Vec3d(1.0, 1.0, 1.0).multiply(maxDistance.toDouble())

    val entities = world.getOtherEntities(
        null,
        Box(startPos.subtract(maxDistanceVector), startPos.add(maxDistanceVector)),
        { entityClass.isInstance(it) }
    )

    while (step <= maxDistance) {
        val location = startPos.add(direction.multiply(step.toDouble()))
        step += stepDistance

        val collided = entities.filter { location in it.boundingBox }.filterIsInstance(entityClass)

        if (collided.isNotEmpty()) {
            return EntityTraceResult(location, collided)
        }
    }

    return null
}

fun PlayerEntity.traceBlockCollision(
    maxDistance: Float = 10F,
    stepDistance: Float = 0.05F,
    blockFilter: (BlockState) -> Boolean = { it.material.isSolid }
): TraceResult? {
    var step = stepDistance
    val startPos = eyePos
    val direction = rotationVector

    var lastBlockPos = startPos.toBlockPos()

    while (step <= maxDistance) {
        val location = startPos.add(direction.multiply(step.toDouble()))
        step += stepDistance

        val blockPos = location.toBlockPos()

        if (blockPos == lastBlockPos) {
            continue
        } else {
            lastBlockPos = blockPos
        }

        val block = world.getBlockState(blockPos)
        if (blockFilter(block)) {
            val dir = findDirectionForIntercept(startPos, location, blockPos)
            return TraceResult(
                location = location,
                blockPos = blockPos,
                direction = dir
            )
        }
    }


    return null
}

fun findDirectionForIntercept(p0: Vec3d, p1: Vec3d, blockPos: BlockPos): Direction {
    val xFunc: (Double) -> Double = { p0.x + (p1.x - p0.x) * it }
    val yFunc: (Double) -> Double = { p0.y + (p1.y - p0.y) * it }
    val zFunc: (Double) -> Double = { p0.z + (p1.z - p0.z) * it }

    val tForX: (Double) -> Double = { if (p0.x != p1.x) { (it - p0.x) / (p1.x - p0.x) } else p0.x }
    val tForY: (Double) -> Double = { if (p0.y != p1.y) { (it - p0.y) / (p1.y - p0.y) } else p0.y }
    val tForZ: (Double) -> Double = { if (p0.z != p1.z) { (it - p0.z) / (p1.z - p0.z) } else p0.z }

    val xRange = blockPos.x.toDouble()..(blockPos.x + 1.0)
    val yRange = blockPos.y.toDouble()..(blockPos.y + 1.0)
    val zRange = blockPos.z.toDouble()..(blockPos.z + 1.0)

    val tAtNorth = tForZ(blockPos.z.toDouble())
    val tAtSouth = tForZ(blockPos.z + 1.0)
    val tAtEast = tForX(blockPos.x + 1.0)
    val tAtWest = tForX(blockPos.x.toDouble())
    val tAtUp = tForY(blockPos.y + 1.0)
    val tAtDown = tForY(blockPos.y.toDouble())

    val northCollision = yFunc(tAtNorth) in yRange && xFunc(tAtNorth) in xRange
    val southCollision = yFunc(tAtSouth) in yRange && xFunc(tAtSouth) in xRange

    val eastCollision = yFunc(tAtEast) in yRange && zFunc(tAtEast) in zRange
    val westCollision = yFunc(tAtWest) in yRange && zFunc(tAtWest) in zRange

    val upCollision = zFunc(tAtUp) in zRange && xFunc(tAtUp) in xRange
    val downCollision = zFunc(tAtDown) in zRange && xFunc(tAtDown) in xRange

    var minDirection: Direction = Direction.UP
    var minTime = Double.MAX_VALUE

    if (northCollision && tAtNorth < minTime) {
        minDirection = Direction.NORTH
        minTime = tAtNorth
    }
    if (southCollision && tAtSouth < minTime) {
        minDirection = Direction.SOUTH
        minTime = tAtSouth
    }
    if (eastCollision && tAtEast < minTime) {
        minDirection = Direction.EAST
        minTime = tAtEast
    }
    if (westCollision && tAtWest < minTime) {
        minDirection = Direction.WEST
        minTime = tAtWest
    }
    if (upCollision && tAtUp < minTime) {
        minDirection = Direction.UP
        minTime = tAtUp
    }
    if (downCollision && tAtDown < minTime) {
        return Direction.DOWN
    }

    return minDirection
}