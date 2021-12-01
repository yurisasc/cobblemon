package com.cablemc.pokemoncobbled.client.entity

import com.cablemc.pokemoncobbled.common.api.entity.EntitySideDelegate
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity

class PokemonClientDelegate : EntitySideDelegate<PokemonEntity> {

    // Put any client-only variables or functions in here. This delegate is 1-1 with the entity on the client side
    var animTick = 0F
    override fun initialize(entity: PokemonEntity) {
        entity.dexNumber.subscribeIncludingCurrent {
            entity.pokemon.species = PokemonSpecies.getByPokedexNumber(it)!! // TODO exception handling
        }
        entity.scaleModifier.listen {
            entity.pokemon.scaleModifier = it // TODO exception handling
        }
    }

    override fun tick(entity: PokemonEntity) {

    }
}