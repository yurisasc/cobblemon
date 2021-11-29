package com.cablemc.pokemoncobbled.common.spawning

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide

/**
 * Just a testing object
 */
object SpawningTest {

    val chunkSpawners: MutableMap<LevelChunk, SpawnChunkInfo> = mutableMapOf()

    @SubscribeEvent
    fun on(event: TickEvent.WorldTickEvent) {
        if(event.phase == TickEvent.Phase.START && event.side == LogicalSide.SERVER) {
            val level = event.world as ServerLevel
            val chunkSrc = level.chunkSource
            chunkSpawners.forEach { (_, info) ->
                info.spawner.update()
            }
            chunkSrc.chunkMap.chunks.forEach { chunkHolder ->
                chunkHolder.tickingChunk?.let { chunk ->
                    val pos = chunk.pos
                    if(level.isPositionEntityTicking(pos) && !chunkSrc.chunkMap.noPlayersCloseForSpawning(pos)) {
                        val spawner: ChunkSpawner
                        if(chunkSpawners.containsKey(chunk))
                            spawner = chunkSpawners[chunk]!!.spawner
                        else {
                            spawner = ChunkSpawner()
                            chunkSpawners[chunk] = SpawnChunkInfo(spawner)
                        }

                        spawner.trySpawn(chunk)

                    } else {
                        if(chunkSpawners.containsKey(chunk)) {
                            chunkSpawners.remove(chunk)
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun on(event: ChunkEvent.Unload) {
        val chunk = event.chunk as LevelChunk
        if(chunkSpawners.containsKey(chunk)) {
            chunkSpawners[chunk]?.spawner?.despawn()
        }
    }
}