/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util

import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.item.Item
import net.minecraft.particle.ParticleEffect
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.*
import net.minecraft.util.math.MathHelper.ceil
import net.minecraft.util.math.MathHelper.floor
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.biome.Biome

fun World.playSoundServer(
    position: Vec3d,
    sound: SoundEvent,
    category: SoundCategory = SoundCategory.NEUTRAL,
    volume: Float = 1F,
    pitch: Float = 1F
) = (this as ServerWorld).playSound(null, position.x, position.y, position.z, sound, category, volume, pitch)

fun <T : ParticleEffect> World.sendParticlesServer(
    particleType: T,
    position: Vec3d,
    particles: Int,
    offset: Vec3d,
    speed: Double
) = (this as ServerWorld).spawnParticles(particleType, position.x, position.y, position.z, particles, offset.x, offset.y, offset.z, speed)

fun World.squeezeWithinBounds(pos: BlockPos): BlockPos {
    val border = worldBorder
    return BlockPos(
        pos.x.coerceIn(border.boundWest.toInt(), border.boundEast.toInt()),
        pos.y.coerceIn(bottomY, topY),
        pos.z.coerceIn(border.boundNorth.toInt(), border.boundSouth.toInt())
    )
}

fun ServerWorld.isBoxLoaded(box: Box): Boolean {
    val startChunkX = ChunkSectionPos.getSectionCoord(box.minX)
    val startChunkZ = ChunkSectionPos.getSectionCoord(box.minZ)
    val endChunkX = ChunkSectionPos.getSectionCoord(box.maxX)
    val endChunkZ = ChunkSectionPos.getSectionCoord(box.maxZ)

    for (chunkX in startChunkX..endChunkX) {
        for (chunkZ in startChunkZ..endChunkZ) {
            if (!this.isChunkLoaded(ChunkPos.toLong(chunkX, chunkZ))) {
                return false
            }
        }
    }

    return true
}

fun Box.getRanges(): Triple<IntRange, IntRange, IntRange> {
    return Triple(floor(minX)..ceil(maxX), minY.toInt()..ceil(maxY), minZ.toInt()..ceil(maxZ))
}

fun BlockView.doForAllBlocksIn(box: Box, useMutablePos: Boolean, action: (BlockState, BlockPos) -> Unit) {
    val mutable = BlockPos.Mutable()
    val (xRange, yRange, zRange) = box.getRanges()
    for (x in xRange) {
        for (y in yRange) {
            for (z in zRange) {
                val pos = if (useMutablePos) mutable.set(x, y, z) else BlockPos(x, y, z)
                val state = getBlockState(pos)
                action(state, pos)
            }
        }
    }
}

fun BlockView.getBlockStates(box: Box): Iterable<BlockState> {
    val states = mutableListOf<BlockState>()
    doForAllBlocksIn(box, useMutablePos = true) { state, _ -> states.add(state) }
    return states
}

fun BlockView.getBlockStatesWithPos(box: Box): Iterable<Pair<BlockState, BlockPos>> {
    val states = mutableListOf<Pair<BlockState, BlockPos>>()
    doForAllBlocksIn(box, useMutablePos = true) { state, pos -> states.add(state to pos) }
    return states
}

fun BlockView.getBlockPositions(box: Box): Iterable<BlockPos> {
    val positions = mutableListOf<BlockPos>()
    val (xRange, yRange, zRange) = box.getRanges()
    for (x in xRange) {
        for (y in yRange) {
            for (z in zRange) {
                positions.add(BlockPos(x, y, z))
            }
        }
    }
    return positions
}

fun BlockView.getWaterAndLavaIn(box: Box): Pair<Boolean, Boolean> {
    var hasWater = false
    var hasLava = false

    doForAllBlocksIn(box, useMutablePos = true) { state, _ ->
        if (!hasWater && state.fluidState.isIn(FluidTags.WATER)) {
            hasWater = true
        }
        if (!hasLava && state.fluidState.isIn(FluidTags.LAVA)) {
            hasLava = true
        }
    }

    return hasWater to hasLava
}

fun Entity.canFit(pos: BlockPos) = canFit(pos.toVec3d())

fun Entity.canFit(vec: Vec3d): Boolean {
    val box = boundingBox.offset(vec.subtract(this.pos))
    return world.isSpaceEmpty(box)
}

enum class PositionType {
    LAND,
    WATER,
    SEAFLOOR,
    LAVAFLOOR
}

fun World.canEntityStayAt(position: BlockPos, width: Int = 1, height: Int = 1, positionType: PositionType): Boolean {
    val minX = kotlin.math.floor(position.x + 0.5 - (width - 1) / 2F).toInt()
    val maxX = kotlin.math.ceil(position.x + 0.5 + (width - 1) / 2F).toInt()
    val maxY = position.y + height

    val minZ = kotlin.math.floor(position.z + 0.5 - (width - 1) / 2F).toInt()
    val maxZ = kotlin.math.ceil(position.z + 0.5 + (width - 1) / 2F).toInt()

    val mutable = BlockPos.Mutable()
    for (x in minX until maxX) {
        for (y in position.y..maxY) {
            for (z in minZ until maxZ) {
                val state = getBlockState(mutable.set(x, y, z))

                // If it's a floor check, we likely need to do something different than surrounding blocks
                if (y == position.y) {
                    if (positionType == PositionType.LAND) {
                        // Land entities need to be standing on solid ground
                        if (state.canPathfindThrough(this, mutable, NavigationType.LAND)) {
                            return false
                        }
                    }
                } else {
                    when (positionType) {
                        PositionType.LAND -> {
                            if (!state.canPathfindThrough(this, mutable, NavigationType.LAND)) {
                                return false
                            }
                        }

                        PositionType.WATER, PositionType.SEAFLOOR -> {
                            if (!state.fluidState.isIn(FluidTags.WATER)) {
                                return false
                            }
                        }

                        PositionType.LAVAFLOOR -> {
                            if (!state.fluidState.isIn(FluidTags.LAVA)) {
                                return false
                            }
                        }
                    }
                }
            }
        }
    }

    return true
}

val World.itemRegistry: Registry<Item>
    get() = registryManager.get(RegistryKeys.ITEM)
val World.biomeRegistry: Registry<Biome>
    get() = registryManager.get(RegistryKeys.BIOME)


fun Vec3d.traceDownwards(
    world: World,
    maxDistance: Float = 10F,
    stepDistance: Float = 0.5F,
): TraceResult? {
    var step = stepDistance
    val startPos = Vec3d(x, y, z)
    val direction = Vec3d(0.0, -1.0, 0.0)

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
        if (!block.isAir) {
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