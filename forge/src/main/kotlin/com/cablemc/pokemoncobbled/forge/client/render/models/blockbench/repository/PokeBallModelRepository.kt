package com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.repository

import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.BlockBenchModelWrapper
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.pokeball.PokeBallModel
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
        registerModelTexture(PokeBalls.POKE_BALL, cobbledResource("textures/pokemon/pokeball-base.png"))
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