package com.cablemc.pokemoncobbled.common.entity.pokemon

import com.cablemc.pokemoncobbled.common.api.entity.PokemonSideDelegate
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/** Handles purely server logic for a Pokémon */
class PokemonServerDelegate : PokemonSideDelegate {
    lateinit var entity: PokemonEntity
    override fun changePokemon(pokemon: Pokemon) {
        entity.initGoals()
    }

    override fun initialize(entity: PokemonEntity) {
        this.entity = entity
        with(entity) {
            speed = 0.35F
            entity.despawner.beginTracking(this)
        }
    }

    override fun tick(entity: PokemonEntity) {
        if (entity.health.toInt() != entity.pokemon.currentHealth) {
            entity.health = entity.pokemon.currentHealth.toFloat()
        }
        if (entity.ownerUuid != entity.pokemon.getOwnerUUID()) {
            entity.ownerUuid = entity.pokemon.getOwnerUUID()
        }

        val isMoving = entity.velocity.length() > 0.1
        if (isMoving && !entity.isMoving.get()) {
            entity.isMoving.set(true)
        } else if (!isMoving && entity.isMoving.get()) {
            entity.isMoving.set(false)
        }
    }
}