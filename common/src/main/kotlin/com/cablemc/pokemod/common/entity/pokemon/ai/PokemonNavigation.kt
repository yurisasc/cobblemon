/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.entity.pokemon.ai

import com.cablemc.pokemod.common.entity.pokemon.PokemonBehaviourFlag
import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemod.common.pokemon.ai.OmniPathNodeMaker
import com.cablemc.pokemod.common.util.toVec3d
import com.google.common.collect.ImmutableSet
import net.minecraft.entity.Entity
import net.minecraft.entity.ai.pathing.MobNavigation
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.ai.pathing.Path
import net.minecraft.entity.ai.pathing.PathNode
import net.minecraft.entity.ai.pathing.PathNodeNavigator
import net.minecraft.entity.ai.pathing.PathNodeType
import net.minecraft.tag.FluidTags
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class PokemonNavigation(val world: World, val pokemonEntity: PokemonEntity) : MobNavigation(pokemonEntity, world) {
    val moving = pokemonEntity.behaviour.moving

    override fun createPathNodeNavigator(range: Int): PathNodeNavigator {
        this.nodeMaker = OmniPathNodeMaker()
        nodeMaker.setCanEnterOpenDoors(true)
        return PathNodeNavigator(nodeMaker, range)
    }

    override fun isAtValidPosition(): Boolean {
        return (this.entity.isOnGround && moving.walk.canWalk) ||
                (!entity.isInLava && !entity.isSubmergedIn(FluidTags.LAVA) && moving.fly.canFly) ||
                (entity.isInLava && moving.swim.canSwimInLava) ||
                (entity.isSubmergedIn(FluidTags.WATER) && moving.swim.canSwimInWater) ||
                this.entity.hasVehicle();
    }

    override fun canSwim(): Boolean {
        return moving.swim.canSwimInWater
    }

    override fun getPos() = Vec3d(entity.x, getPathfindingY().toDouble(), entity.z)

    override fun continueFollowingPath() {
        val vec3d = this.pos
        nodeReachProximity = (if (entity.width > 0.75f) entity.width / 2.0f else 0.75f - entity.width / 2.0f) + 0.05F
        val vec3i = currentPath!!.currentNodePos
        val d = Math.abs(entity.x - (vec3i.x.toDouble() + 0.5))
        val e = Math.abs(entity.y - vec3i.y.toDouble())
        val f = Math.abs(entity.z - (vec3i.z.toDouble() + 0.5))
        val closeToNode = d < nodeReachProximity.toDouble() && f < this.nodeReachProximity.toDouble() && e < 1.0
        if (closeToNode || entity.canJumpToNextPathNode(currentPath!!.currentNode.type) && shouldJumpToNextNode(vec3d)) {
            currentPath!!.next()
        }
        checkTimeouts(vec3d)
    }

    override fun tick() {
        super.tick()
        val node = getCurrentPath()?.lastNode

        val isFlying = pokemonEntity.getBehaviourFlag(PokemonBehaviourFlag.FLYING)
        val canWalk = pokemonEntity.behaviour.moving.walk.canWalk
        if (node != null) {
            if (node.type == PathNodeType.OPEN) {
                val canFly = moving.fly.canFly
                if (canFly && !pokemonEntity.getBehaviourFlag(PokemonBehaviourFlag.FLYING)) {
                    pokemonEntity.setBehaviourFlag(PokemonBehaviourFlag.FLYING, true)
                }
            } else if (node.type != PathNodeType.OPEN && isFlying && canWalk) {
                pokemonEntity.setBehaviourFlag(PokemonBehaviourFlag.FLYING, false)
            }
        } else if (isFlying && canWalk && !pokemonEntity.world.getBlockState(pokemonEntity.blockPos).canPathfindThrough(pokemonEntity.world, pokemonEntity.blockPos.down(), NavigationType.LAND)) {
            pokemonEntity.setBehaviourFlag(PokemonBehaviourFlag.FLYING, false)
        }
    }

    override fun findPathTo(target: BlockPos, distance: Int): Path? {
        return this.findPathTo(ImmutableSet.of(target), 8, false, distance)?.also {
            try {
                var i = 0
                while (true) {
                    val node = it.getNode(i)
//                    entity.world.spawnEntity(
//                        ItemEntity(entity.world, node.blockPos.x.toDouble(), node.blockPos.y.toDouble(), node.blockPos.z.toDouble(), ItemStack(Items.PUFFERFISH))
//                            .also {
//                                it.setNoGravity(true)
//                                it.velocity = Vec3d.ZERO
//                                after(seconds = 5F, serverThread = true) {
//                                    it.discard()
//                                }
//                            }
//                    )
//                    entity.world.setBlockState(node.blockPos, Blocks.GOLD_BLOCK.defaultState)
                    i++
                }
            } catch(e: Exception) {
            }
//            entity.world.setBlockState(entity.blockPos, Blocks.DIAMOND_BLOCK.defaultState)
//            entity.remove(Entity.RemovalReason.DISCARDED)
        }
    }

    override fun findPathTo(entity: Entity, distance: Int): Path? {
        return this.findPathTo(entity.blockPos, distance)
    }

    override fun adjustPath() {
        super.adjustPath()
        val path = getCurrentPath() ?: return
        var i = 2
        while (i < path.length) {
            val firstNode = path.getNode(i - 2)
            val middleNode = path.getNode(i - 1)
            val nextNode = path.getNode(i)

            val directionToMiddle = middleNode.blockPos.subtract(firstNode.blockPos).toVec3d().normalize()
            val nodeType = firstNode.type
            if (nodeType != middleNode.type || nodeType != nextNode.type) {
                i++
                continue
            }

            val directionToEnd = nextNode.blockPos.subtract(middleNode.blockPos).toVec3d().normalize()
            if (directionToEnd != directionToMiddle) {
                i++
                continue
            }

            // 3 of the same node in a row in the same direction - the second node is unnecessary
            val remainingNodes = mutableListOf<PathNode>()
            var j = i
            while (true) {
                try {
                    remainingNodes.add(path.getNode(j))
                    j++
                } catch(e: Exception) { break }
            }

            path.length = i + remainingNodes.size - 1
            for (k in remainingNodes.indices) {
                path.setNode(i - 1 + k, remainingNodes[k])
            }
        }

        // Could check for direct sunlight or rain or something
//        if (avoidSunlight) {
//            if (this.world.isSkyVisible(BlockPos(entity.x, entity.y + 0.5, entity.z))) {
//                return
//            }
//            for (i in 0 until currentPath!!.length) {
//                val pathNode = currentPath!!.getNode(i)
//                if (!this.world.isSkyVisible(BlockPos(pathNode.x, pathNode.y, pathNode.z))) continue
//                currentPath!!.length = i
//                return
//            }
//        }
    }

    fun getPathfindingY(): Int {
        val inSwimmableFluid = (entity.isSubmergedIn(FluidTags.WATER) && moving.swim.canSwimInWater) ||
                (entity.isSubmergedIn(FluidTags.LAVA) && moving.swim.canSwimInLava)
        if (!inSwimmableFluid) {
            return MathHelper.floor(entity.y)
        }

        return entity.blockY

//        if (!entity.isTouchingWater || !canSwim()) {
//            return MathHelper.floor(entity.y + 0.5)
//        }

//        var i = entity.blockY
//        var blockState = this.world.getBlockState(BlockPos(entity.x, i.toDouble(), entity.z))
//        var j = 0
//        while (blockState.isOf(Blocks.WATER)) {
//            blockState = this.world.getBlockState(BlockPos(entity.x, (++i).toDouble(), entity.z))
//            if (++j <= 16) continue
//            return entity.blockY
//        }
//        return i
    }


//    fun onPoseChange(newPoseType: PoseType) {
//        if (newPoseType in setOf(PoseType.FLY, PoseType.HOVER)) {
//            if (nodeMaker !is BirdPathNodeMaker) {
//                nodeMaker = BirdPathNodeMaker()
//                recalculatePath()
//            }
//        } else if (nodeMaker !is AmphibiousPathNodeMaker) {
//            nodeMaker = AmphibiousPathNodeMaker(!pokemonEntity.behaviour.moving.swim.canBreatheUnderwater)
//            recalculatePath()
//        }
//    }
}