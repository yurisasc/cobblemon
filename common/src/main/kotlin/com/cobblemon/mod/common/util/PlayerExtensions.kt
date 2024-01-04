/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.dialogue.ActiveDialogue
import com.cobblemon.mod.common.api.dialogue.Dialogue
import com.cobblemon.mod.common.api.dialogue.DialogueManager
import com.cobblemon.mod.common.api.reactive.Observable.Companion.filter
import com.cobblemon.mod.common.api.reactive.Observable.Companion.takeFirst
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.platform.events.PlatformEvents
import java.util.UUID
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

// Stuff like getting their party
fun ServerPlayerEntity.party() = Cobblemon.storage.getParty(this)
fun ServerPlayerEntity.pc() = Cobblemon.storage.getPC(this.uuid)
val ServerPlayerEntity.activeDialogue: ActiveDialogue?
    get() = DialogueManager.activeDialogues[uuid]
val ServerPlayerEntity.isInDialogue: Boolean
    get() = DialogueManager.activeDialogues.containsKey(uuid)
fun ServerPlayerEntity.closeDialogue() {
    DialogueManager.stopDialogue(this)
}
fun ServerPlayerEntity.openDialogue(dialogue: Dialogue) {
    DialogueManager.startDialogue(this, dialogue)
}
fun ServerPlayerEntity.extraData(key: String) = Cobblemon.playerData.get(this).extraData[key]
fun ServerPlayerEntity.hasKeyItem(key: Identifier) = Cobblemon.playerData.get(this).keyItems.contains(key)
fun UUID.getPlayer() = server()?.playerManager?.getPlayer(this)

fun ServerPlayerEntity.onLogout(handler: () -> Unit) {
    PlatformEvents.SERVER_PLAYER_LOGOUT.pipe(filter { it.player.uuid == uuid }, takeFirst()).subscribe { handler() }
}

/**
 * Attempts to heal the player party when they're sleeping.
 * This will fail if the sleeping trigger isn't the typical vanilla bed or if [isInBattle] is true.
 *
 * @return If the attempt to heal was successful.
 */
fun ServerPlayerEntity.didSleep(): Boolean {
    if (sleepTimer != 100 || world.timeOfDay.toInt() % 24000 != 0 || this.isInBattle()) {
        return false
    }
    party().didSleep()
    return true
}

fun ServerPlayerEntity.isInBattle() = BattleRegistry.getBattleByParticipatingPlayer(this) != null
fun ServerPlayerEntity.getBattleState(): Pair<PokemonBattle, BattleActor>? {
    val battle = BattleRegistry.getBattleByParticipatingPlayer(this)
    if (battle != null) {
        val actor = battle.getActor(this)
        if (actor != null) {
            return battle to actor
        }
    }
    return null
}

// TODO Player extension for queueing next login?
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
    entityClass: Class<T>,
    ignoreEntity: T? = null
): T? {
    return traceEntityCollision(
        maxDistance,
        stepDistance,
        entityClass,
        ignoreEntity
    )?.let { it.entities.minByOrNull { it.distanceTo(this) } }
}

fun <T : Entity> PlayerEntity.traceEntityCollision(
    maxDistance: Float = 10F,
    stepDistance: Float = 0.05F,
    entityClass: Class<T>,
    ignoreEntity: T? = null
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

        val collided = entities.filter { ignoreEntity != it && location in it.boundingBox }.filter { entityClass.isInstance(it) }

        if (collided.isNotEmpty()) {
            return EntityTraceResult(location, collided.filterIsInstance(entityClass))
        }
    }

    return null
}

fun PlayerEntity.traceBlockCollision(
    maxDistance: Float = 10F,
    stepDistance: Float = 0.05F,
    blockFilter: (BlockState) -> Boolean = { it.isSolid }
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

fun PlayerInventory.usableItems() = offHand + main

/**
 * Utility function meant to emulate the behavior seen across Minecraft when attempting to give items directly to player but there's not enough room for the entire stack.
 * This will drop any remainder of the stack on the ground with the associate player marked as the owner.
 * Keep in mind in creative attempting to insert stacks into the player inventory never fails instead they're simply consumed, this is not custom Cobblemon behavior.
 *
 * @param stack The [ItemStack] being given.
 * @param playSound If the pickup sound should be played for any successfully added items.
 */
fun PlayerEntity.giveOrDropItemStack(stack: ItemStack, playSound: Boolean = true) {
    val inserted = this.inventory.insertStack(stack)
    if (inserted && stack.isEmpty) {
        stack.count = 1
        this.dropItem(stack, false)?.setDespawnImmediately()
        if (playSound) {
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7f + 1.0f) * 2.0f)
        }
        this.currentScreenHandler.sendContentUpdates()
    }
    else {
        this.dropItem(stack, false)?.let { itemEntity ->
            itemEntity.resetPickupDelay()
            itemEntity.setOwner(this.uuid)
        }
    }
}

/** Retrieves the battle theme associated with this player, or the default PVP theme if null. */
fun ServerPlayerEntity.getBattleTheme() = Cobblemon.playerData.get(this).battleTheme?.let { Registries.SOUND_EVENT.get(it) } ?: CobblemonSounds.PVP_BATTLE
