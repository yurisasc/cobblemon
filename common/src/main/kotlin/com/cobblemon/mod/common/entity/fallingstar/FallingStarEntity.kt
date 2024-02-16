package com.cobblemon.mod.common.entity.fallingstar

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.api.spawning.BestSpawner
import com.cobblemon.mod.common.api.spawning.fallingstar.FallingStarSpawnCause
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.util.toBlockPos
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.World

class FallingStarEntity(world: World) : Entity(CobblemonEntities.FALLING_STAR_ENTITY, world) {
    override fun initDataTracker() {
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound?) {
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound?) {
    }

    override fun tick() {
        val player = closestPlayer
        if (player != null) {
            player.sendMessage("hello bro".text())
            reveal(player)
            kill()
        }
        super.tick()
    }

    private fun reveal(playerEntity: PlayerEntity) {
        spawnPokemon(playerEntity)
    }

    private fun spawnPokemon(playerEntity: PlayerEntity) {
        if (world.isClient) return
        val spawner = BestSpawner.starSpawner

        val spawnCause = FallingStarSpawnCause(
            spawner = spawner,
            bucket = Cobblemon.bestSpawner.config.buckets[0],
            entity = playerEntity
        )

        val result = spawner.run(spawnCause, world as ServerWorld, pos.toBlockPos())

        val resultingSpawn = result?.get()


    }

    override fun shouldRender(distance: Double): Boolean {
        return false
    }

    private val closestPlayer: PlayerEntity?
        get() = world.players.firstOrNull { it.pos.distanceTo(pos) < 10.0 }
}