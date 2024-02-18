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
import com.cobblemon.mod.common.loot.CobblemonLootTables
import com.cobblemon.mod.common.util.math.DoubleRange
import com.cobblemon.mod.common.util.nextBetween
import com.cobblemon.mod.common.net.messages.client.effect.SpawnSnowstormParticlePacket
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.MovementType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.explosion.Explosion
import java.time.Instant
import kotlin.random.Random

class FallingStarEntity(world: World) : Entity(CobblemonEntities.FALLING_STAR_ENTITY, world) {
    private var particleOne = Identifier("cobblemon:null")
    private var particleTwo = Identifier("cobblemon:null")
    private var particleThree = Identifier("cobblemon:null")
    private var particleAmbient = Identifier("cobblemon:null")
    private var particleImpact = Identifier("cobblemon:null")
    private var particleTimer: Instant = Instant.now().plusMillis(50)
    private var particleAmbientTimer: Instant = Instant.now().plusMillis(1000)

    private var hasFallen = false

    override fun initDataTracker() {
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound?) {
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound?) {
    }

    override fun tick() {
        particleOne = Identifier("cobblemon:meteor_glitter")
        particleTwo = Identifier("cobblemon:meteor_trail")
        particleThree = Identifier("cobblemon:meteor_falling")

        if (velocity.y == 0.0) {
            velocity = Vec3d(Random.nextBetween(VELOCITY_RANGE.start, VELOCITY_RANGE.endInclusive), -1.0, Random.nextBetween(VELOCITY_RANGE.start, VELOCITY_RANGE.endInclusive))
            particleOne = Identifier("cobblemon:null")
            particleTwo = Identifier("cobblemon:null")
            particleThree = Identifier("cobblemon:null")

            if (Instant.now().isAfter(particleAmbientTimer)) {
                particleAmbient = Identifier("cobblemon:meteor_ambient")
                particleHandler(particleAmbient, this.pos)
                particleAmbientTimer = Instant.now().plusMillis(1000)
            }
        }

        if (!world.getBlockState(blockPos.down()).isAir) {
            velocity = Vec3d(0.0, -1.0, 0.0)
            if (!world.isClient && !hasFallen) {
                particleImpact = Identifier("cobblemon:meteor_impact")
                particleHandler(particleImpact, this.pos)
                (world as ServerWorld).createExplosion(
                    this,
                    x, y, z, // the position of the explosion
                    0.0f, // the power of the explosion
                    World.ExplosionSourceType.MOB
                )
                hasFallen = true
            }
        }

        move(MovementType.SELF, velocity)

        val player = closestPlayer
        if (player != null) {
            reveal(player)
            kill()
        }

        if(Instant.now().isAfter(particleTimer)) {
            particleHandler(particleTwo, this.pos)
            particleHandler(particleThree, this.pos)
            particleTimer = Instant.now().plusMillis(50)
        }

        particleHandler(particleOne, this.pos)

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
        particleOne = Identifier("cobblemon:masterball/ballsparkle")
        particleHandler(particleOne, this.pos)
    }

    private fun spawnPokemon(playerEntity: PlayerEntity) {
        if (world.isClient) return
        val spawner = BestSpawner.starSpawner

        val spawnCause = FallingStarSpawnCause(
            spawner = spawner,
            bucket = Cobblemon.bestSpawner.config.buckets[0],
            entity = playerEntity
        )
        particleOne = Identifier("cobblemon:confetti")
        particleHandler(particleOne, this.pos)
        
        val result = spawner.run(spawnCause, world as ServerWorld, blockPos) ?: return
    }

    private fun particleHandler(id: Identifier, pos: Vec3d) {
        val spawnSnowstormParticlePacket = SpawnSnowstormParticlePacket(id, pos)
        spawnSnowstormParticlePacket.sendToPlayersAround(pos.x, pos.y, pos.z, 128.0, world.registryKey)
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