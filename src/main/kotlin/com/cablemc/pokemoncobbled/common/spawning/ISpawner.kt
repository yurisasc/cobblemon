package com.cablemc.pokemoncobbled.common.spawning

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.spawning.settings.MockSettings
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.random.Random

/**
 * Maybe someone wants to be able to spawn things near other Entities?
 * TODO Replace MockSettings with real Settings
 */
interface ISpawner<T> where T: Entity {

    fun spawnNear(entity: T)

    fun getNearVector(entity: T): Vec3 {
        return entity.position().add(
            Random.nextDouble(MockSettings.maxDist.toDouble()),
            Random.nextDouble(MockSettings.maxHeight.toDouble()),
            Random.nextDouble(MockSettings.maxDist.toDouble())
        )
    }

    fun spawn(species: Species, pos: Vec3, level: Level) {
        val pokemonEntity = PokemonEntity(level).apply {
            pokemon = Pokemon().apply { this.species = species }
            dexNumber.set(pokemon.species.nationalPokedexNumber)
        }
        level.addFreshEntity(pokemonEntity)
        pokemonEntity.setPos(pos)
    }
}