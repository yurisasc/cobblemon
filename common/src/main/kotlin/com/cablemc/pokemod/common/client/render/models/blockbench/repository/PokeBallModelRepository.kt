/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.repository

import com.cablemc.pokemod.common.api.pokeball.PokeBalls
import com.cablemc.pokemod.common.client.render.models.blockbench.BlockBenchModelWrapper
import com.cablemc.pokemod.common.client.render.models.blockbench.pokeball.PokeBallModel
import com.cablemc.pokemod.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemod.common.pokeball.PokeBall
import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.util.Identifier

object PokeBallModelRepository : ModelRepository<EmptyPokeBallEntity>() {
    private val modelsByPokeBall: MutableMap<PokeBall, BlockBenchModelWrapper<EmptyPokeBallEntity>> = mutableMapOf()
    private val modelTexturesByPokeBall: MutableMap<PokeBall, Identifier> = mutableMapOf()

    override fun registerAll() {
        val baseModel = BlockBenchModelWrapper(PokeBallModel.LAYER_LOCATION, PokeBallModel::createBodyLayer) { PokeBallModel(it) }
        registerModel(PokeBalls.POKE_BALL, baseModel)
        registerModel(PokeBalls.GREAT_BALL, baseModel)
        registerModel(PokeBalls.ULTRA_BALL, baseModel)
        registerModel(PokeBalls.MASTER_BALL, baseModel)
        registerModelTexture(PokeBalls.POKE_BALL, pokemodResource("textures/items/poke_ball.png"))
        registerModelTexture(PokeBalls.GREAT_BALL, pokemodResource("textures/items/great_ball.png"))
        registerModelTexture(PokeBalls.ULTRA_BALL, pokemodResource("textures/items/ultra_ball.png"))
        registerModelTexture(PokeBalls.MASTER_BALL, pokemodResource("textures/items/master_ball.png"))
    }

    private fun registerModel(pokeBall: PokeBall, model: BlockBenchModelWrapper<EmptyPokeBallEntity>) {
        modelsByPokeBall[pokeBall] = model
        addModel(model)
    }

    private fun registerModelTexture(pokeBall: PokeBall, texture: Identifier) {
        modelTexturesByPokeBall[pokeBall] = texture
    }

    fun getModel(pokeBall: PokeBall): BlockBenchModelWrapper<EmptyPokeBallEntity> {
        return modelsByPokeBall[pokeBall] ?: throw IllegalStateException("pokeball has no appropriate model")
    }

    fun getModelTexture(pokeBall: PokeBall): Identifier {
        return modelTexturesByPokeBall[pokeBall] ?: throw IllegalStateException("pokeball has no appropriate model texture")
    }
}