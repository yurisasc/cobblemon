package com.cablemc.pokemoncobbled.common.spawning

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.spawning.utils.BiomeHelper
import com.cablemc.pokemoncobbled.common.util.toVec3
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.AABB

class ChunkSpawner {

    private val limit = 1
    private val track: MutableList<PokemonEntity> = mutableListOf()

    fun trySpawn(chunk: LevelChunk) {
        // Temp hack fix to stop over spawning
        val zone = AABB(chunk.pos.getMiddleBlockPosition(80)).inflate(100.0)
        if(chunk.level.getEntities(null, zone).filter { it is PokemonEntity }.size > 20)
            return

        if(track.size <= limit) {
            val pos = getRandomPosWithin(chunk.level, chunk)

            val possibleSpawns = BiomeHelper.possibleSpawns(chunk.level.getBiome(pos))
            if/*space*/(possibleSpawns.isEmpty())
                return

            spawn(possibleSpawns.random(), pos, chunk.level)
        }
    }

    fun despawn() {
        track.forEach {
            it.discard()
        }
    }

    fun update() {
        track.removeIf {
            it.isRemoved
        }
    }

    private fun spawn(species: Species, pos: BlockPos, level: Level) {
        val pokemonEntity = PokemonEntity(level).apply {
            pokemon = Pokemon().apply { this.species = species }
            dexNumber.set(pokemon.species.nationalPokedexNumber)
        }
        track.add(pokemonEntity)
        level.addFreshEntity(pokemonEntity)
        pokemonEntity.setPos(pos.toVec3())
    }

    private fun getRandomPosWithin(pLevel: Level, pChunk: LevelChunk): BlockPos {
        val chunkpos = pChunk.pos
        val x = chunkpos.minBlockX + pLevel.random.nextInt(16)
        val z = chunkpos.minBlockZ + pLevel.random.nextInt(16)
        val y = pChunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1
        return BlockPos(x, y, z)
    }
}