/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.fallingstar

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.api.spawning.BestSpawner
import com.cobblemon.mod.common.api.spawning.fallingstar.FallingStarSpawnCause
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.get
import com.cobblemon.mod.common.loot.CobblemonLootTables
import com.cobblemon.mod.common.util.math.DoubleRange
import com.cobblemon.mod.common.util.nextBetween
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.util.toBlockPos
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.MovementType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.random.Random

class FallingStarEntity(world: World) : Entity(CobblemonEntities.FALLING_STAR_ENTITY, world) {

    override fun initDataTracker() {
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound?) {
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound?) {
    }

    override fun tick() {
        if (velocity.y == 0.0) {
            velocity = Vec3d(Random.nextBetween(VELOCITY_RANGE.start, VELOCITY_RANGE.endInclusive), -1.0, Random.nextBetween(VELOCITY_RANGE.start, VELOCITY_RANGE.endInclusive))
        }

        if (!world.getBlockState(blockPos.down()).isAir) {
            velocity = Vec3d(0.0, -1.0, 0.0)
        }
        world.addParticle(
            ParticleTypes.WAX_ON,
            pos.x,
            pos.y,
            pos.z,
            0.0, 0.0, 0.0
        )

        move(MovementType.SELF, velocity)

        val player = closestPlayer
        if (player != null) {
            reveal(player)
            kill()
        }
        super.tick()
    }

    private fun reveal(playerEntity: PlayerEntity) {
        world.playSound(null, blockPos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT)

        val random = random.nextBetween(0, 100)

        if (random <= 50) {
            spawnPokemon(playerEntity)
        } else {
            spawnItem()
        }
    }

    private fun spawnItem() {
        if (world.isClient) return
        val lootTable = world.server?.lootManager?.getLootTable(CobblemonLootTables.FALLING_STAR) ?: return
        val stacks = lootTable.generateLoot(LootContextParameterSet.Builder(world as ServerWorld)
            .add(LootContextParameters.THIS_ENTITY, this)
            .build(LootContextTypes.BARTER))
        stacks.forEach { world.spawnEntity(ItemEntity(world, pos.x, pos.y, pos.z, it)) }
    }

    private fun spawnPokemon(playerEntity: PlayerEntity) {
        if (world.isClient) return
        val spawner = BestSpawner.starSpawner

        val spawnCause = FallingStarSpawnCause(
            spawner = spawner,
            bucket = Cobblemon.bestSpawner.config.buckets[0],
            entity = playerEntity
        )

        val result = spawner.run(spawnCause, world as ServerWorld, blockPos) ?: return
    }

    override fun shouldRender(distance: Double): Boolean {
        return false
    }

    private val closestPlayer: PlayerEntity?
        get() = world.players.firstOrNull { it.pos.distanceTo(pos) < 6.0 }

    companion object {
        val VELOCITY_RANGE = DoubleRange(-0.25, 0.25)
    }
}