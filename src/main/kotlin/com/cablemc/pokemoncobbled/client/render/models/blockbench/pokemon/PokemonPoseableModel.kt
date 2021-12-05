package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.client.entity.PokemonClientDelegate
import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity

/**
 * A poseable model for a Pokémon. Just handles the state accessor to the [PokemonClientDelegate].
 *
 * @author Hiroku
 * @since December 4th, 2021
 */
abstract class PokemonPoseableModel : PoseableEntityModel<PokemonEntity>() {
    override fun getState(entity: PokemonEntity) = entity.delegate as PokemonClientDelegate
}