package com.cablemc.pokemoncobbled.common.entity.pokemon

import com.cablemc.pokemoncobbled.common.api.entity.EntitySideDelegate

/** Handles purely server logic for a Pokémon */
class PokemonServerDelegate : EntitySideDelegate<PokemonEntity> {
    override fun initialize(entity: PokemonEntity) {}
    override fun tick(entity: PokemonEntity) {
        val mag = entity.deltaMovement.length()
        val isMoving = mag > 0.1F
        if (entity.isMoving.currentValue) {
            if (!isMoving) {
                entity.isMoving.set(false)
            }
        } else {
            if (isMoving) {
                entity.isMoving.set(true)
            }
        }
    }
}