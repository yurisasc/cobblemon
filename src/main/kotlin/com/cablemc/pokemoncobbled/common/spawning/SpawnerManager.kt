package com.cablemc.pokemoncobbled.common.spawning

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.EntityLeaveWorldEvent
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide

/**
 * Just a testing object
 */
object SpawnerManager {

    private val chunkSpawners: MutableMap<LevelChunk, ChunkSpawner> = mutableMapOf()

    @SubscribeEvent
    fun on(event: TickEvent.WorldTickEvent) {
        return
        if/*space*/(event.phase == TickEvent.Phase.START && event.side == LogicalSide.SERVER) {
            val level = event.world as ServerLevel
            val chunkSrc = level.chunkSource
            chunkSrc.chunkMap.chunks.forEach { chunkHolder ->
                chunkHolder.tickingChunk?.let { chunk ->
                    val pos = chunk.pos
                    if/*space*/(level.isPositionEntityTicking(pos) && !chunkSrc.chunkMap.noPlayersCloseForSpawning(pos)) {
                        val spawner: ChunkSpawner
                        if/*space*/(chunkSpawners.containsKey(chunk))
                            spawner = chunkSpawners[chunk]!!
                        else {
                            spawner = ChunkSpawner()
                            chunkSpawners[chunk] = spawner
                        }

                        spawner.trySpawn(chunk)

                    } else {
                        if/*space*/(chunkSpawners.containsKey(chunk)) {
                            chunkSpawners.remove(chunk)
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun on(event: EntityLeaveWorldEvent) {
        if/*space*/(event.entity is PokemonEntity)
            chunkSpawners.forEach { (_, spawner) ->
                spawner.update()
            }
    }

    @SubscribeEvent
    fun on(event: ChunkEvent.Unload) {
        val chunk = event.chunk as LevelChunk
        if/*space*/(chunkSpawners.containsKey(chunk)) {
            chunkSpawners[chunk]?.despawn()
        }
    }
}