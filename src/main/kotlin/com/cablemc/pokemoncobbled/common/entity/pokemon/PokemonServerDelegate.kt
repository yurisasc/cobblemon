package com.cablemc.pokemoncobbled.common.entity.pokemon

import com.cablemc.pokemoncobbled.common.api.entity.EntitySideDelegate

/** Handles purely server logic for a Pokémon */
class PokemonServerDelegate : EntitySideDelegate<PokemonEntity> {
    override fun initialize(entity: PokemonEntity) {
        entity.speed = 0.35F
    }
    
    override fun tick(entity: PokemonEntity) {
        val isMoving = entity.deltaMovement.length() > 0.1
        if (isMoving && !entity.isMoving.currentValue) {
            entity.isMoving.set(true)
        } else if (!isMoving && entity.isMoving.currentValue) {
            entity.isMoving.set(false)
        }
    }
}