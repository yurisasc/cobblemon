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
        if (event.phase == TickEvent.Phase.START && event.side == LogicalSide.SERVER) {
            val level = event.world as ServerLevel
            val chunkSrc = level.chunkSource
            chunkSrc.chunkMap.chunks.forEach { chunkHolder ->
                chunkHolder.tickingChunk?.let { chunk ->
                    val pos = chunk.pos
                    if (level.isPositionEntityTicking(pos) && chunkSrc.chunkMap.getPlayersCloseForSpawning(pos).isNotEmpty()) {
                        val spawner: ChunkSpawner
                        if (chunkSpawners.containsKey(chunk))
                            spawner = chunkSpawners[chunk]!!
                        else {
                            spawner = ChunkSpawner()
                            chunkSpawners[chunk] = spawner
                        }

                        spawner.trySpawn(chunk)

                    } else {
                        if (chunkSpawners.containsKey(chunk)) {
                            chunkSpawners.remove(chunk)
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun on(event: EntityLeaveWorldEvent) {
        if (event.entity is PokemonEntity) {
            chunkSpawners.forEach { (_, spawner) ->
                spawner.update()
            }
        }
    }

    @SubscribeEvent
    fun on(event: ChunkEvent.Unload) {
        chunkSpawners[event.chunk as LevelChunk]?.despawn()
    }
}