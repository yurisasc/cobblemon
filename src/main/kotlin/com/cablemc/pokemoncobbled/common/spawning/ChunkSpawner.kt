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

class ChunkSpawner {

    val limit = 1
    private var cur = 0
    private val track: MutableList<PokemonEntity> = mutableListOf()

    fun trySpawn(chunk: LevelChunk) {
        if(cur <= limit) {
            val pos = getRandomPosWithin(chunk.level, chunk)

            val possibleSpawns = BiomeHelper.possibleSpawns(chunk.level.getBiome(pos))
            if(possibleSpawns.isEmpty())
                return

            cur++;
            spawn(possibleSpawns.random(), pos, chunk.level)
        }
    }

    fun despawn() {
        track.forEach {
            it.discard()
        }
    }

    fun update() {
        val toRemove = mutableListOf<PokemonEntity>()
        track.forEach { pokemonEntity ->
            if(pokemonEntity.isRemoved) {
                toRemove.add(pokemonEntity)
                cur--;
            }
        }
        track.removeAll(toRemove)
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