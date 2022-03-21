package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.BlockBenchModelWrapper
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokeball.PokeBallModel
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.resources.ResourceLocation

object PokeBallModelRepository : ModelRepository<EmptyPokeBallEntity>() {
    private val modelsByPokeBall: MutableMap<PokeBall, BlockBenchModelWrapper<EmptyPokeBallEntity>> = mutableMapOf()
    private val modelTexturesByPokeBall: MutableMap<PokeBall, ResourceLocation> = mutableMapOf()

    override fun registerAll() {
        registerModel(PokeBalls.POKE_BALL, BlockBenchModelWrapper(PokeBallModel.LAYER_LOCATION, PokeBallModel::createBodyLayer) { PokeBallModel(it) })
        registerModelTexture(PokeBalls.POKE_BALL, cobbledResource("textures/items/poke_ball.png"))
        registerModelTexture(PokeBalls.GREAT_BALL, cobbledResource("textures/items/great_ball.png"))
        registerModelTexture(PokeBalls.ULTRA_BALL, cobbledResource("textures/items/ultra_ball.png"))
        registerModelTexture(PokeBalls.MASTER_BALL, cobbledResource("textures/items/master_ball.png"))
    }

    private fun registerModel(pokeBall: PokeBall, model: BlockBenchModelWrapper<EmptyPokeBallEntity>) {
        modelsByPokeBall[pokeBall] = model
        addModel(model)
    }

    private fun registerModelTexture(pokeBall: PokeBall, texture: ResourceLocation) {
        modelTexturesByPokeBall[pokeBall] = texture
    }

    fun getModel(pokeBall: PokeBall): BlockBenchModelWrapper<EmptyPokeBallEntity> {
        return modelsByPokeBall[pokeBall] ?: throw IllegalStateException("pokeball has no appropriate model")
    }

    fun getModelTexture(pokeBall: PokeBall): ResourceLocation {
        return modelTexturesByPokeBall[pokeBall] ?: throw IllegalStateException("pokeball has no appropriate model texture")
    }
}