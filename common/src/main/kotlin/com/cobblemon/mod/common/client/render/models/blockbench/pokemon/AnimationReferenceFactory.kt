/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon

import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity

/**
 * Responsible for creating an animation instance from animation reference strings. In the Bedrock format these often
 * look like "bedrock(pokemon_name, animation_name)" which searches an animation registry for data.
 */
interface AnimationReferenceFactory {
    fun stateless(model: JsonPokemonPoseableModel, animString: String): StatelessAnimation<PokemonEntity, ModelFrame>

    fun stateful(model: JsonPokemonPoseableModel, animString: String): StatefulAnimation<PokemonEntity, ModelFrame>
}