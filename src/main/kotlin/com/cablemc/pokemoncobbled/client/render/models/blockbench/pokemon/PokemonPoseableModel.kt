package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.client.entity.PokemonClientDelegate
import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity

abstract class PokemonPoseableModel<F : ModelFrame> : PoseableEntityModel<PokemonEntity, F>() {
    override fun getState(entity: PokemonEntity) = entity.delegate as PokemonClientDelegate
}